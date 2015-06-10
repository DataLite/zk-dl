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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

/**
 * <p>Handles exceptions thrown by invocated methods. It the thrown type is
 * defined in annotation, then the exception is caught and messagebox is shown
 * instead.</p>
 *
 * @author Karel Cemus
 */
public class ZkExceptionHandler extends Handler {
    
     /** key of the exception in context map */
    private static final String EXCEPTION_EXCEPTION = "Exception::exception";

    private final String title;

    private final String message;

    private final Class type;

    private final boolean localize;

    private final boolean unwrap;

    private final static boolean localizeAll;

    private final static boolean unwrapAll;

    static {
        /** Reads default configuration for library */
        unwrapAll = Boolean.parseBoolean(Library.getProperty("zk-dl.annotation.exception.unwrap", "false"));
        /** Reads default configuration for library */
        localizeAll = Boolean.parseBoolean(Library.getProperty("zk-dl.annotation.i18n", "false"));
    }

    public static Invoke process(Invoke inner, ZkException annotation) {
        return new ZkExceptionHandler(inner, annotation.title(), annotation.message(), annotation.type(), annotation.i18n() || localizeAll, annotation.unwrap() || unwrapAll);
    }

    public static Invoke process(Invoke inner, ZkExceptions annotations) {
        for (ZkException annotation : annotations.exceptions()) {
            inner = process(inner, annotation);
        }
        return inner;
    }

    public ZkExceptionHandler(Invoke inner, String title, String message, Class type, boolean localize, boolean unwrap) {
        super(inner);
        this.message = message;
        this.type = type;
        this.localize = localize;
        this.unwrap = unwrap;
        this.title = localize ? Labels.getLabel(title) : title;
    }

    @Override
    public boolean invoke(final Context context) throws Exception {
        try {
            super.invoke(context);
        } catch (Exception ex) { // catch all            
            final Throwable throwable = getTypeOf(ex, type, unwrap);            
            if (throwable == null) { // is throw instance of catching type?                
                throw ex; // if not, pass through
            } else {
                // the exception is a type of throwable
                // register it in the context
                context.putParameter( EXCEPTION_EXCEPTION, throwable );
            }
        }
        return true;
    }

    @Override
    protected void doAfter(final Context context) {
        final Throwable throwable = ( Throwable ) context.getParameter( EXCEPTION_EXCEPTION );
        if (throwable != null) { // is there message to be show?
            // show message instead
            String msg = StringHelper.isNull( message ) ? throwable.getMessage() : message;
            if ( localize ) { // error message localization
                msg = Labels.getLabel( msg, msg );
            }
            Clients.clearBusy();
            Messagebox.show( msg, title, Messagebox.OK, Messagebox.ERROR );
            // message processed
        }
    }

    /**
     * Test if given exception is instance of given type
     *
     * @param exception param type
     *
     * @return unwrapped exception or null
     */
    protected Throwable getTypeOf(Throwable exception, Class type, boolean recursively) {
        if (exception == null) {
            return null;
        }
        if (type.isAssignableFrom(exception.getClass())) {
            return exception;
        }
        if (recursively) {
            return getTypeOf(exception.getCause(), type, recursively);
        }
        return null;
    }
}
