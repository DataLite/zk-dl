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
import cz.datalite.zk.annotation.invoke.KeyEventHandler;
import java.lang.reflect.Method;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * <p>Annotation processor which handles 
 * processing of {@link ZkEvent}. The main 
 * purpose of this processor to handle
 * key events, eg. SHIFT+A. Handler parses
 * given string and filters request by functional
 * keys, if they are set or not. If keys fits then
 * the method is invoked otherwise event is dropped.</p>
 *
 * <p></p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class KeyEventProcessor extends AbstractProcessor<ZkEvent> {

    public static final KeyEventProcessor INSTANCE = new KeyEventProcessor();

    public Invoke process( ZkEvent annotation, Invoke inner, Method method, Component master, Object controller ) {
        if ( annotation.event().startsWith( "on" ) ) { // this is not definition of keyEvent
            return inner; // no decoration
        }
        Component component = inner.getTarget();

        if ( component instanceof XulElement ) { // adding hot key listener on component
            XulElement target = ( XulElement ) component;
            String keys = target.getCtrlKeys() == null ? "" : target.getCtrlKeys();

            KeyEventHandler handler = new KeyEventHandler( inner, annotation.event() );

            target.setCtrlKeys( keys + handler.getKeys() );

            return handler;
        } else {
            throw new UiException( "CtrlKey event can be set only on XulElements." );
        }
    }
}
