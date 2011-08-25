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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

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

    /** Target of event */
    private Component target;

    /** Target Method */
    private Method method;

    /** Target object with method */
    private Object controller;

    /** Method payload */
    private int payload;

    public MethodInvoker( String event, Component target, Method method, Object controller, int payload ) {
        this.event = event;
        this.target = target;
        this.method = method;
        this.controller = controller;
        this.payload = payload;
    }

    public void invoke( Event event ) throws Exception {
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

    public Component getTarget() {
        return target;
    }
}
