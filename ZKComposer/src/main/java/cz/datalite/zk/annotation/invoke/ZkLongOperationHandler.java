/*
 * Copyright (c) 2012, DataLite. All rights reserved.
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

import cz.datalite.zk.annotation.ZkLongOperation;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * <p>Handles binding request before and after method invocation. For all
 * registered component executes load or safe based on annotation's
 * properties.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkLongOperationHandler extends Handler {

    private static final String EVENT = "onEchoEvent";
    /**
     * message to be shown to a user
     */
    private final String message;
    /**
     * if a message is localized
     */
    private final boolean i18n;
    /**
     * if the operation is interruptable
     */
    private final boolean cancelable;

    public static Invoke process(Invoke inner, ZkLongOperation annotation) {
        return new ZkLongOperationHandler(inner, annotation.message(), annotation.i18n(), annotation.cancelable());
    }

    public ZkLongOperationHandler(Invoke inner, final String message, final boolean i18n, final boolean cancelable) {
        super(inner);
        this.message = message;
        this.i18n = i18n;
        this.cancelable = cancelable;
    }

    @Override
    protected boolean doBeforeInvoke(final Event event, final Component master, final Object controller) {
        master.addEventListener(EVENT, new EventListener() {

            public void onEvent(Event event) throws Exception {
                master.removeEventListener(EVENT, this);
                goOn(event, master, controller); // correct answer, go on in executing
            }
        });

        // show busy message
        // ToDo localization
        // ToDo cancelable
        Clients.showBusy(message);
        // send echo event
        Events.echoEvent(EVENT, master, null);
        // prevent invoke propagation
        return false;
    }

    @Override
    protected void doAfterInvoke(Event event, Component master, Object controller) {
        Clients.clearBusy();
    }

    private Component getComponent(String id, Component master) {
        try {
            return id.length() == 0 ? master : master.getFellow(id);
        } catch (ComponentNotFoundException ex) {
            throw new ComponentNotFoundException("ZkBinding could not be registered on component \"" + id + "\" because component wasn\'t found.", ex);
        }
    }
}
