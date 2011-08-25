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
package cz.datalite.zk.annotation.processor;

import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.invoke.Invoke;
import cz.datalite.zk.annotation.invoke.MethodInvoker;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Events;

/**
 * <p></p>
 *
 * <p></p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkEventProcessor implements Processor<ZkEvent> {

    public static final ZkEventProcessor INSTANCE = new ZkEventProcessor();

    public List<Invoke> process( ZkEvent annotation, List<Invoke> inner, Method method, Component target, Object controller ) {
        // load the component
        final Component source;
        if ( annotation.id().length() == 0 ) {
            source = target;
        } else if ( annotation.id().startsWith( "/" ) ) {
            source = Path.getComponent( annotation.id() );
            if ( source == null ) {
                throw new ComponentNotFoundException( annotation.id() );
            }
        } else {
            source = target.getFellow( annotation.id() );
        }

        String event = annotation.event().startsWith( "on" ) ? annotation.event() : Events.ON_CTRL_KEY;

        Invoke methodInvoker = new MethodInvoker( event, source, method, controller, annotation.payload() );
        Invoke invoke = KeyEventProcessor.INSTANCE.process( annotation, methodInvoker, method, target, controller );

        return Collections.singletonList( invoke );
    }
}
