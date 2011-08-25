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

import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkBindings;
import cz.datalite.zk.annotation.ZkConfirm;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkException;
import cz.datalite.zk.annotation.invoke.Invoke;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.zkoss.zk.ui.Component;

/**
 * <p></p>
 *
 * <p></p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class AnnotationProcessor {

    final Component master;

    Object controller;

    public AnnotationProcessor( Component target, Object controller ) {
        this.master = target;
        this.controller = controller;
    }

    public void processAnnotations( Method method ) {
        List<Invoke> invokes = Collections.emptyList();
        invokes = processAnnotation( ZkEventsProcessor.INSTANCE, ZkEvents.class, invokes, method );
        invokes = processAnnotation( ZkEventProcessor.INSTANCE, ZkEvent.class, invokes, method );
        invokes = processAnnotation( ZkExceptionProcessor.INSTANCE, ZkException.class, invokes, method );
        invokes = processAnnotation( ZkBindingProcessor.INSTANCE, ZkBinding.class, invokes, method );
        invokes = processAnnotation( ZkBindingsProcessor.INSTANCE, ZkBindings.class, invokes, method );
        invokes = processAnnotation( ZkConfirmProcessor.INSTANCE, ZkConfirm.class, invokes, method );

        registerInvokes( invokes );
    }

    private <T> List<Invoke> processAnnotation( Processor<T> processor, Class<T> type, List<Invoke> invokes, Method method ) {
        T annotation = findAnnotation( method, type );
        if ( annotation != null ) {
            return processor.process( annotation, invokes, method, master, controller );
        }
        return invokes;
    }

    private <T> T findAnnotation( Method method, Class<T> type ) {
        for ( Annotation annotation : method.getAnnotations() ) {
            if ( type.isAssignableFrom( annotation.getClass() ) ) {
                return ( T ) annotation;
            }
        }
        return null;
    }

    private void registerInvokes( List<Invoke> invokes ) {
        for ( final Invoke invoke : invokes ) {
            invoke.getTarget().addEventListener( invoke.getEvent(), new InvokeListener( invoke ) );
        }
    }
}
