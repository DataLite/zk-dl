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
 * <p>Abstract class handler simplifies the implementation
 * of {@link Invoke} interface. Handler serves as template
 * for mathryoska class. The class provides basic functionality
 * like delegation getters and keeping the the order of
 * invocated methods.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public abstract class Handler implements Invoke {

    /** Inner (wrapped) object */
    protected Invoke inner;

    public Handler( Invoke inner ) {
        this.inner = inner;
    }

    /** 
     * Additional functionality appended before method invocation
     * @param event source event
     * @return TRUE if continue invoking, FALSE for stop propagation
     */
    protected boolean doBeforeInvoke( Event event, Component master, Object controller ) {
        return true;
    }

    /**
     * Additional functionality appended after method invocation.
     * @param event  Source event
     */
    protected void doAfterInvoke( Event event, Component master, Object controller ) {
    }

    public void invoke( Event event, Component master, Object controller ) throws Exception {
        if ( doBeforeInvoke( event, master, controller ) ) { // if propagate == true
            goOn( event, master, controller ); // execute invoke and do After
        }
    }

    /**
     * Invocation part of process, initialization is already done.
     * This method can be also called by child class if it interrupted
     * event propagation. After invoking resume just call this method.
     * @param event source event
     * @throws Exception  something went wrong
     */
    protected void goOn( Event event, Component master, Object controller ) throws Exception {
        inner.invoke( event, master, controller );
        doAfterInvoke( event, master, controller );
    }

    public String getEvent() {
        return inner.getEvent();
    }

    public Component bind( Component component ) {
        return inner.bind( component );
    }
}
