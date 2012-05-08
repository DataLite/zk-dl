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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Window;

/**
 * <p>Handles binding request before and after method invocation. For all
 * registered component executes load or safe based on annotation's
 * properties.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkLongOperationHandler extends Handler {

    private static final String EVENT = "onEchoEvent";

    /** message to be shown to a user */
    private final String message;

    /** if the operation is interruptable */
    private final boolean cancellable;

    /** state of general property */
    private static boolean localizeAll;

    /** opened busy window */
    private Window busy;

    static {
        /** Reads default configuration for library */
        localizeAll = Boolean.parseBoolean(Library.getProperty("zk-dl.annotation.i18n", "false"));
    }

    public static Invoke process(Invoke inner, ZkLongOperation annotation) {
        String message = annotation.message();
        // check for default localized message
        boolean i18n = localizeAll || message.startsWith("zkcomposer.") || annotation.i18n();
        return new ZkLongOperationHandler(inner, message, i18n, annotation.cancellable());
    }

    public ZkLongOperationHandler(Invoke inner, final String message, final boolean i18n, final boolean cancellable) {
        super(inner);
        this.message = i18n ? Labels.getLabel(message) : message;
        this.cancellable = cancellable;
    }

    @Override
    protected boolean doBeforeInvoke(final Event event, final Component master, final Object controller) {
        master.addEventListener(EVENT, new EventListener() {

            public void onEvent(Event event) throws Exception {
                master.removeEventListener(EVENT, this);
                // user was informed, go on in execution
                goOn(event, master, controller);
            }
        });

        // invokes status window informing user about operation
        invokeBusyBox();
        
        // send echo event
        Events.echoEvent(EVENT, master, null);

        // prevent invoke propagation
        return false;
    }

    @Override
    protected void doAfterInvoke(Event event, Component master, Object controller) {
        busy.detach();
        busy = null;
    }

    private Component getComponent(String id, Component master) {
        try {
            return id.length() == 0 ? master : master.getFellow(id);
        } catch (ComponentNotFoundException ex) {
            throw new ComponentNotFoundException("ZkBinding could not be registered on component \"" + id + "\" because component wasn\'t found.", ex);
        }
    }

    /**
     * Invokes busy box window informing user about long running operation
     */
    private void invokeBusyBox() {
        // show busy message
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cancellable", cancellable);
        parameters.put("message", message);
        busy = (Window) Executions.createComponents("~./busybox.zul", null, parameters);
        
        // ToDo cancelable
    }
}
