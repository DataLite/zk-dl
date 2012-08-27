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

import cz.datalite.zk.annotation.ZkConfirm;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

/**
 * <p>Handles confirm question before action is invoked. When the action is
 * invoked then the propagation of invocation is stopped and question is popped
 * up instead. When positive answer comes then the event propagation goes
 * on.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkConfirmHandler extends Handler {

    /** question message */
    private final String message;

    /** window title */
    private final String title;

    /** available buttons */
    private final int buttons;

    /** button of accept */
    private final int accessButton;

    /** type of box */
    private final String icon;

    private final static boolean localizeAll;

    static {
        /** Reads default configuration for library */
        localizeAll = Boolean.parseBoolean(Library.getProperty("zk-dl.annotation.i18n", "false"));
    }

    public static Invoke process(Invoke inner, ZkConfirm annotation) {
        return new ZkConfirmHandler(inner, annotation.message(), annotation.title(), annotation.buttons(), annotation.accessButton(), annotation.icon(), localizeAll || annotation.i18n());
    }

    public ZkConfirmHandler(Invoke inner, String message, String title, int buttons, int accessButton, String icon, boolean localize) {
        super(inner);
        this.message = localize ? Labels.getLabel(message) : message;
        this.title = localize ? Labels.getLabel(title) : title;
        this.buttons = buttons;
        this.accessButton = accessButton;
        this.icon = icon;
    }

    @Override
    protected boolean doBefore(final Context context) {
        if (message == null) {
            return true; // continue
        } else {
            // prompt question
            Messagebox.show( message, title, buttons, icon, new EventListener() {

                public void onEvent( final Event event ) throws Exception {
                    if ( ( Integer ) event.getData() == accessButton ) {
                        // correct answer, resumeBeforeInvoke executing
                        resumeBeforeInvoke( context );
                    }
                }
            } );
        }
        return false; // do not continue invocation
    }
}
