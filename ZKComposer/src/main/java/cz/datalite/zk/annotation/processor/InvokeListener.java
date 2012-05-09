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

import cz.datalite.zk.annotation.invoke.Invoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * <p>Invocation listener is bound to components and listens on required event.
 * When it is called then the {@link Invoke} object is executed.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class InvokeListener implements EventListener {

    private Invoke invoke;

    private Object controller;

    private Component master;

    public InvokeListener(Invoke invoke, Component master, Object controller) {
        this.invoke = invoke;
        this.controller = controller;
        this.master = master;
    }

    public void onEvent(Event event) throws Exception {
        if (invoke.doBeforeInvoke(event, master, controller)) {
            if (invoke.invoke(event, master, controller)) {
                invoke.doAfterInvoke(event, master, controller);
            }
        }
    }
}
