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
package cz.datalite.zk.annotation.processor;

import cz.datalite.zk.annotation.ZkException;
import cz.datalite.zk.annotation.ZkExceptions;
import cz.datalite.zk.annotation.invoke.Invoke;
import java.lang.reflect.Method;
import org.zkoss.zk.ui.Component;

/**
 * <p>Annotation processor which handles 
 * processing of {@link ZkExceptions}</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkExceptionsProcessor extends AbstractProcessor<ZkExceptions> {

    public static final ZkExceptionsProcessor INSTANCE = new ZkExceptionsProcessor();

    public Invoke process( ZkExceptions annotation, Invoke invoke, Method method, Component master, Object controller ) {
        for ( ZkException exception : annotation.exceptions() ) {
            invoke = ZkExceptionProcessor.INSTANCE.process( exception, invoke, method, master, controller );
        }
        return invoke;
    }
}
