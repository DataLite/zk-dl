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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

/**
 * <p></p>
 *
 * <p></p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkConfirmHandler extends Handler {

    private String message;

    private String title;

    private int buttons;

    private final int accessButton;

    private final String icon;

    public ZkConfirmHandler( Invoke inner, String message, String title, int buttons, int accessButton, String icon ) {
        super( inner );
        this.message = message;
        this.title = title;
        this.buttons = buttons;
        this.accessButton = accessButton;
        this.icon = icon;
    }

    @Override
    protected boolean doBeforeInvoke( final Event event ) {
        if ( message == null ) {
            return true; // continue
        } else {
            try {
                Messagebox.show( message, title, buttons, icon, new EventListener() {

                    public void onEvent( final Event msgEvent ) throws Exception {
                        if ( ( Integer ) msgEvent.getData() == accessButton ) {
                            goOn( event );
                        }
                    }
                } );
            } catch ( InterruptedException ex ) {
            }
        }
        return false; // do not continue invocation
    }
}
