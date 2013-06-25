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

import cz.datalite.zk.bind.AnnotationBinder;
import cz.datalite.zk.bind.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Karel Cemus
 */
public class CommandInvoker implements Invoke {
    
    /** instance of logger */
    private static final Logger LOGGER = LoggerFactory.getLogger( CommandInvoker.class );
    
    public static List<Invoke> process(Method method, Command annotation) {
        
        Invoke commandInvoker = new CommandInvoker();
        return Collections.singletonList(commandInvoker);
    }

    public boolean doBeforeInvoke(final Context context) {
        return true;
    }

    public boolean invoke(final Context invocationContext) throws Exception {
        if ( !(invocationContext instanceof CommandContext) ) {
            final String error =  "CommandInvoker is bound to @org.zkoss.bind.annotation.Command and takes CommandContext only.";
            LOGGER.error( error );
            throw new IllegalArgumentException( error );
        }
        
        final CommandContext context = ( CommandContext ) invocationContext;
        
        // get binder
        // test the correct instance of Binder implementation
        // it takes advantage of a single-thread execution 
        // and that the binder is attached to the component 
        // as well as controller (view-model)
        // Due to that we can resume to invocation of an event
        final Object objBinder = context.getRoot().getAttribute( "$BINDER$" );
        if ( !(objBinder instanceof Binder) ) {
            final String error =  "The CommandInvoker requires DataLite implementation of ZKOSS binder.";
            LOGGER.error( error );
            throw new IllegalStateException( error );
        }
        final AnnotationBinder binder = ( AnnotationBinder ) objBinder;

        // resume ZKOSS command execution
        binder.resumeCommand(context.getTarget(), context.getCommand(), context.getCommandArgs(), context.getCtx(), context.getNotifys() );
        
        return true;
    }

    public void doAfterInvoke(final Context context) {
    }

    public Component bind(Component master) {
        return master;
    }
}
