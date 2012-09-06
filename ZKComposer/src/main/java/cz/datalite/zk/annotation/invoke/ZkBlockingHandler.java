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

import cz.datalite.zk.annotation.ZkBlocking;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * <p>Handles binding request before and after method invocation. For all
 * registered component executes load or safe based on annotation's
 * properties.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkBlockingHandler extends Handler {

    /** name of echo event */
    private static final String EVENT = "onEchoEvent";
    
    /** key of the exception in context map */
    private static final String BLOCKING_BUSYBOX = "Blocking::busybox";

    /** state of general property */
    private static final boolean localizeAll;
    
    /** message to be shown */
    private final String message;
    
    /** use localization */
    private final boolean i18n;
    
    /** parent component */
    private final String component;

    static {
        /** Reads default configuration for library */
        localizeAll = Boolean.parseBoolean(Library.getProperty("zk-dl.annotation.i18n", "false"));
    }

    public static Invoke process(Invoke inner, ZkBlocking annotation) {
        String message = annotation.message();
        // check for default localized message
        boolean i18n = localizeAll || message.startsWith("zkcomposer.") || annotation.i18n();
        // parent component
        String component = "".equals(annotation.component()) ? null : annotation.component();

        return new ZkBlockingHandler(inner, message, i18n, component);
    }

    public ZkBlockingHandler(Invoke inner, final String message, final boolean i18n, final String component) {
        super(inner);
        this.message = message;
        this.i18n = i18n;
        this.component = component;
    }

    @Override
    public boolean invoke(final Context context) throws Exception {
        final Component root = context.getRoot();
        
        // echo event handler
        root.addEventListener(EVENT, new EventListener() {

            public void onEvent(Event event) throws Exception {
                // handler used, remove itself
                root.removeEventListener(EVENT, this);

                // user was informed, resumeBeforeInvoke in execution
                resumeInvoke(context);
            }
        });
        // fire echo event
        Events.echoEvent(EVENT, root, null);

        // invokes status window informing user about operation
        final BusyBoxHandler busybox = new BusyBoxHandler(message, i18n, false, component);
        busybox.show(root);
        context.putParameter( BLOCKING_BUSYBOX, busybox );

        // invocation is not complete, prevent resuming
        return false;
    }

    @Override
    protected void doAfter(final Context context) {
        final Component root = context.getRoot();
        final BusyBoxHandler busybox = ( BusyBoxHandler ) context.getParameter( BLOCKING_BUSYBOX );
        busybox.close(root);
    }
}
