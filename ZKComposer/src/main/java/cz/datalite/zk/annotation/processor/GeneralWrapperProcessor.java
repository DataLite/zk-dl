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

/**
 * <p>GeneralInitializerProcessor is an class
 * which allows definition of processors producing
 * MethodInvokers. Their API is based on same
 * signature without compile-time controll. These
 * methods are invoked via reflection.</p>
 *
 * @author Karel Cemus
 */
public class GeneralWrapperProcessor<T> implements Wrapper {

    /** annotation class */
    private Class<T> type;

    /** annotation handler */
    private Class processor;

    public GeneralWrapperProcessor( Class<T> annotation, Class processor ) {
        this.type = annotation;
        this.processor = processor;
    }

    public Invoke process( Method method, Invoke inner ) {
        try {
            T annotation = ReflectionHelper.findAnnotation( method, type );
            if ( annotation != null ) {
                Method processingMethod = processor.getMethod( "process", Invoke.class, type );
                return ( Invoke ) processingMethod.invoke( null, inner, annotation );
            }
            return inner;
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }
}
