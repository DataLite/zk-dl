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

import cz.datalite.helpers.StringHelper;
import java.lang.reflect.InvocationTargetException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

/**
 * <p>Handles exceptions thrown by invocated methods. It the thrown type
 * is defined in annotation, then the exception is caught and messagebox
 * is shown instead.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkExceptionHandler extends Handler {

    private String title;

    private String message;

    private Class type;

    public ZkExceptionHandler( Invoke inner, String title, String message, Class type ) {
        super( inner );
        this.title = title;
        this.message = message;
        this.type = type;
    }

    @Override
    protected void goOn( Event event ) throws Exception {
        try {
            super.goOn( event ); // invoke method
        } catch ( InvocationTargetException ex ) { // catch all
            Throwable target = ex.getTargetException(); // thrown exception
            if ( type.isAssignableFrom( target.getClass() ) ) { // is throw instance of catching type?
                try { // show message instead
                    Messagebox.show( StringHelper.isNull( message ) ? target.getMessage() : message, title, Messagebox.OK, Messagebox.ERROR );
                } catch ( InterruptedException e ) {
                }
            } else {
                throw ex;
            }
        }
    }
}
