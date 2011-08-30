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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * <p>Interface defines functionality of invokable object. These objects
 * wrap target method and provides additional functionality usually
 * based on targeted method's annotations.</p>
 *
 * <p>The main concept of {@link Invoke} interface lies in design
 * pattern <strong>decorator</strong>. Each object which wrappes
 * the method should provide one additional functionality and can
 * be used as a part of many object like in matryoshka doll. The order
 * of composed object is important, the functionality could be different
 * with different order. The similar example is interface 
 * {@link java.io.InputStream}</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface Invoke {

    /**
     * Invoke the desired method including additional functionality
     * @param event source event
     * @param master master component of controller
     * @param controller controller object
     * @throws Exception Any exception has occured.
     */
    void invoke( Event event, Component master, Object controller ) throws Exception;

    /**
     * Binds to the component. Sets up component's properties according to
     * the handler's requirements
     * @return observed component
     */
    Component bind( Component master );

    /**
     * Targeted event which the invoke object should listen
     * @return listened event
     */
    String getEvent();
}
