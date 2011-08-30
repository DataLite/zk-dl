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

import cz.datalite.helpers.ReflectionHelper;
import cz.datalite.zk.annotation.invoke.Invoke;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * <p>GeneralInitializerProcessor is an class
 * which allows definition of processors producing
 * MethodInvokers. Their API is based on same
 * signature without compile-time controll. These
 * methods are invoked via reflection.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class GeneralInitializerProcessor<T> implements Initializer {

    /** annotation class */
    private Class<T> type;

    /** annotation handler */
    private Class processor;

    public GeneralInitializerProcessor( Class<T> annotation, Class processor ) {
        this.type = annotation;
        this.processor = processor;
    }

    public List<Invoke> process( Method method ) {
        try {
            List<Invoke> output = null;
            T annotation = ReflectionHelper.findAnnotation( method, type );
            if ( annotation != null ) {

                Method processingMethod = processor.getMethod( "process", Method.class, type );
                output = ( List<Invoke> ) processingMethod.invoke( null, method, annotation );
            }
            return output == null ? Collections.EMPTY_LIST : output;
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }
}
