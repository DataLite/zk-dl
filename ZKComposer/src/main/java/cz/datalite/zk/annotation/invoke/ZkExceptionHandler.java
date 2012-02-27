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
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

import java.lang.reflect.InvocationTargetException;

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

    private static boolean localizeAll = false;

    private boolean unwrap;

    private static boolean unwrapAll = false;

    static {
        /** Reads default configuration for library */
        unwrapAll = Boolean.parseBoolean( Library.getProperty( "zk-dl.annotation.exception.unwrap", "false" ) );
        /** Reads default configuration for library */
        localizeAll = Boolean.parseBoolean( Library.getProperty( "zk-dl.annotation.i18n", "false" ) );
    }

    public static Invoke process( Invoke inner, ZkException annotation ) {
        return new ZkExceptionHandler( inner, annotation.title(), annotation.message(), annotation.type(), annotation.i18n() || localizeAll, annotation.unwrap() || unwrapAll );
    }

    public static Invoke process( Invoke inner, ZkExceptions annotations ) {
        for ( ZkException annotation : annotations.exceptions() ) {
            inner = process( inner, annotation );
        }
        return inner;
    }

    public ZkExceptionHandler( Invoke inner, String title, String message, Class type, boolean localize, boolean unwrap ) {
        super( inner );
        this.message = message;
        this.type = type;
        this.localize = localize;
        this.unwrap = unwrap;

        if (localize)
            this.title =  Labels.getLabel( title );

        if (this.title == null)
            this.title = title;

    }

    @Override
    protected void goOn( Event event, Component master, Object controller ) throws Exception {
        try {
            super.goOn( event, master, controller ); // invoke method
        } catch ( InvocationTargetException ex ) { // catch all
            Throwable target = getTypeOf( ex.getTargetException(), type, unwrap );
            if ( target != null ) { // is throw instance of catching type?
                try { // show message instead
                    String msg = StringHelper.isNull( message ) ? target.getMessage() : message;
                    if ( localize ) { // error message localization
                        String localizedMessage = Labels.getLabel( msg );
                        if (localizedMessage != null)
                            msg = localizedMessage;
                    }
                    Messagebox.show( msg, title, Messagebox.OK, Messagebox.ERROR );
                } catch ( InterruptedException e ) {
                }
            } else {
                throw ex;
            }
        }
    }

    /**
     * Test if given exception is instance of given type
     * @param exception
     * @param type
     * @return
     */
    protected Throwable getTypeOf( Throwable exception, Class type, boolean recursively ) {
        if ( exception == null ) {
            return null;
        }
        if ( type.isAssignableFrom( exception.getClass() ) ) {
            return exception;
        }
        if ( recursively ) {
            return getTypeOf( exception.getCause(), type, recursively );
        }
        return null;
    }
}
