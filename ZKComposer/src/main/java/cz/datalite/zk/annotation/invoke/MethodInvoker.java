/*
 * Copyright (c) 2011, DataLite. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package cz.datalite.zk.annotation.invoke;

import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

/**
 * <p>Method invoker handles requests for invocation given
 * method on given target. This class provides desired functionality
 * of {@link cz.datalite.zk.annotation.ZkEvent}. After all decorations
 * are handled then the target method is invoked.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class MethodInvoker implements Invoke {

    /** Target event */
    private String event;

    /** Id of target component */
    private String target;

    /** Target Method */
    private Method method;

    /** Method payload */
    private int payload;

    public static List<Invoke> process( Method method, ZkEvent annotation ) {
        // if event starts with ON then it is regular event otherwise it is supposed as HOT KEY definition
        String event = annotation.event().startsWith( "on" ) ? annotation.event() : Events.ON_CTRL_KEY;
        Invoke methodInvoker = new MethodInvoker( method, event, annotation.id(), annotation.payload() );
        return Collections.singletonList( KeyEventHandler.process( methodInvoker, annotation ) );
    }

    public static List<Invoke> process( Method method, ZkEvents annotations ) {
        List<Invoke> invokes = new ArrayList<Invoke>();
        for ( ZkEvent annotation : annotations.events() ) {
            invokes.addAll( process( method, annotation ) );
        }
        return invokes;
    }

    public MethodInvoker( Method method, String event, String target, int payload ) {
        this.event = event;
        this.method = method;
        this.payload = payload;
        this.target = target;
    }

    public void invoke( Event event, Component master, Object controller ) throws Exception {
        // unwrap forward event
        final Event unwrappedEvent = (event instanceof org.zkoss.zk.ui.event.ForwardEvent)
                ? (( org.zkoss.zk.ui.event.ForwardEvent ) event).getOrigin() : event;

        List<Object> args = buildArgs( event, unwrappedEvent );

        try {
            method.invoke( controller, args.toArray() );
        } catch ( IllegalAccessException ex ) {
            throw new NoSuchMethodException( "Cannot access method \"" + method.getName() + "\". Error " + ex.getMessage() );
        } catch ( IllegalArgumentException ex ) {
            throw new NoSuchMethodException( "Invalid arguments for method \"" + method.getName() + "\". Error " + ex.getMessage() );
        }
    }

    private List<Object> buildArgs( Event event, Event unwrappedEvent ) throws NoSuchMethodException {
        List args = new LinkedList();
        for ( Class type : method.getParameterTypes() ) {
            if ( type.isAssignableFrom( unwrappedEvent.getClass() ) ) {
                args.add( unwrappedEvent ); // unwrapped event
            } else if ( type.isAssignableFrom( event.getClass() ) ) {
                args.add( event );       // forward event
            } else if ( type.getName().equals( "int" ) ) {
                args.add( payload );      // payload has to be primitive int type
            } else if ( event.getTarget() != null && type.isAssignableFrom( event.getTarget().getClass() ) ) {
                args.add( event.getTarget() );   // target component
            } else if ( unwrappedEvent.getData() != null && type.isAssignableFrom( unwrappedEvent.getData().getClass() ) ) {
                args.add( unwrappedEvent.getData() );   // event data
            } else if ( event.getTarget() == null && type.isAssignableFrom( org.zkoss.zk.ui.Component.class ) ) {
                args.add( null ); // null target component
            } else if ( unwrappedEvent.getData() == null ) {
                args.add( null ); // null data (can be of any type)
            } else {
                throw new NoSuchMethodException( "No such method \"" + method.getName() + "\" with correct arguments for event " + event.toString()
                        + ". Unknown parameter type: " + type.getName() );
            }
        }
        return args;
    }

    public String getEvent() {
        return event;
    }

    public Component bind( Component master ) {
        // load the component
        final Component source;
        if ( target.length() == 0 ) {
            source = master;
        } else if ( target.startsWith( "/" ) ) {
            source = Path.getComponent( target );
            if ( source == null ) {
                throw new ComponentNotFoundException( target );
            }
        } else {
            source = master.getFellow( target );
        }

        return source;
    }
}
