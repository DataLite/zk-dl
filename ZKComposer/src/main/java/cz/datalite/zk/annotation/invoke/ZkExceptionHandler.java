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
import cz.datalite.zk.annotation.ZkException;
import cz.datalite.zk.annotation.ZkExceptions;
import java.lang.reflect.InvocationTargetException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
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

    private boolean localize;

    public static Invoke process( Invoke inner, ZkException annotation ) {
        return new ZkExceptionHandler( inner, annotation.title(), annotation.message(), annotation.type(), annotation.i18n() );
    }

    public static Invoke process( Invoke inner, ZkExceptions annotations ) {
        for ( ZkException annotation : annotations.exceptions() ) {
            inner = new ZkExceptionHandler( inner, annotation.title(), annotation.message(), annotation.type(), annotation.i18n() );
        }
        return inner;
    }

    public ZkExceptionHandler( Invoke inner, String title, String message, Class type, boolean localize ) {
        super( inner );
        this.title = localize ? Labels.getLabel( title ) : title;
        this.message = message;
        this.type = type;
        this.localize = localize;
    }

    @Override
    protected void goOn( Event event, Component master, Object controller ) throws Exception {
        try {
            super.goOn( event, master, controller ); // invoke method
        } catch ( InvocationTargetException ex ) { // catch all
            Throwable target = ex.getTargetException(); // thrown exception
            if ( type.isAssignableFrom( target.getClass() ) ) { // is throw instance of catching type?
                try { // show message instead
                    String msg = StringHelper.isNull( message ) ? target.getMessage() : message;
                    if ( localize ) { // error message localization
                        msg = Labels.getLabel( msg );
                    }
                    Messagebox.show( msg, title, Messagebox.OK, Messagebox.ERROR );
                } catch ( InterruptedException e ) {
                }
            } else {
                throw ex;
            }
        }
    }
}
