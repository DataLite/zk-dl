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
import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkBindings;
import cz.datalite.zk.annotation.ZkConfirm;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkException;
import cz.datalite.zk.annotation.ZkExceptions;
import cz.datalite.zk.annotation.invoke.Invoke;
import cz.datalite.zk.annotation.invoke.MethodInvoker;
import cz.datalite.zk.annotation.invoke.ZkBindingHandler;
import cz.datalite.zk.annotation.invoke.ZkConfirmHandler;
import cz.datalite.zk.annotation.invoke.ZkExceptionHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;

/**
 * <p>Annotation Processor handles all ZK annotations on the methods.
 * For each annotation there is defined specific annotation processor 
 * which handles that annotation type and provides desired functionality.
 * AnnotationProcessor cares about right order of decorating {@link Invoke}
 * object because if they would be decorated in wrong order then the
 * final functionality could be undeterministic or at least undesired.</p>
 *
 * <p>Instance of annotation processor is bound to the instance of component 
 * controller but there is possible way to refactor it as unbound to anything.
 * Then the class would create templates which would be cloned and bound to 
 * the specific instance.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class AnnotationProcessor<T> {

    /** Map of created processors. Serves as annotation cache */
    private static Map< Class, AnnotationProcessor> processors = new HashMap<Class, AnnotationProcessor>();

    /** List of all events gathered from class */
    private List<Invoke> invokes = new LinkedList<Invoke>();

    /**
     * Returns AnnotationProcessor for existing class
     * @param type annotated class
     * @return processor
     */
    public static <T> AnnotationProcessor<T> getProcessor( Class<T> type ) {
        AnnotationProcessor<T> ap = processors.get( type );
        if ( ap == null ) {
            processors.put( type, ap = new AnnotationProcessor<T>( type ) );
        }
        return ap;
    }

    /**
     * <p>Registers all detected events to the component and bound events
     * to given controller. Bounds invokes to the components to handle executions.</p>
     * @param component targeted master component
     * @param controller object with given
     */
    public void registerTo( Component component, T controller ) {
        for ( final Invoke invoke : invokes ) {
            invoke.bind( component ).addEventListener( invoke.getEvent(), new InvokeListener( invoke, component, controller ) );
        }
    }

    private AnnotationProcessor( Class<T> type ) {
        for ( final Method method : ReflectionHelper.getAllMethods( type ) ) {
            List<Invoke> result = processAnnotations( method );
            if ( result != null ) {
                invokes.addAll( result );
            }
        }
    }

    /**
     * <p>Basic method responsible for processing annotations on the methods. This method
     * is called by composer processor and the rest of annotating is proceed here.</p>
     * 
     * <p>This method holds processing order of annotations. There is defined exact
     * order from the most inner to the most outer. If somebody whats to add another
     * annotation then he have to decide which annotatin should it be and set its
     * order in decorater order. Then the adding the new row on the rigth place 
     * is enough to register it.</p>
     * @param method proceed method
     */
    private List<Invoke> processAnnotations( Method method ) {
        try {
            List<Invoke> events = new ArrayList<Invoke>();

            events.addAll( initAnnotations( MethodInvoker.class, method, ZkEvent.class ) );
            events.addAll( initAnnotations( MethodInvoker.class, method, ZkEvents.class ) );
            if ( !events.isEmpty() ) {
                // the most inner object, invoked last (if the previous proceed right and event is not dropped)
                events = processAnnotation( ZkExceptionHandler.class, method, events, ZkException.class );
                events = processAnnotation( ZkExceptionHandler.class, method, events, ZkExceptions.class );
                events = processAnnotation( ZkBindingHandler.class, method, events, ZkBinding.class );
                events = processAnnotation( ZkBindingHandler.class, method, events, ZkBindings.class );
                events = processAnnotation( ZkConfirmHandler.class, method, events, ZkConfirm.class );
                // the most outer object, invoked first
            }

            return events;
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    private <S> List<Invoke> initAnnotations( Class processor, Method method, Class<S> type ) throws Exception {
        List<Invoke> output = new ArrayList<Invoke>();
        S annotation = findAnnotation( method, type );
        if ( annotation != null ) {
            Method processingMethod = processor.getMethod( "process", Method.class, type );
            output.addAll( ( List<Invoke> ) processingMethod.invoke( null, method, annotation ) );
        }
        return output;
    }

    /**
     * Method calls annotation processor if the annotation is not null
     * @param <T> type of proceeded annotation
     * @param processor annotation processor which can handle given type of annotation
     * @param type of annotation
     * @param invokes set of inner invokes
     * @param method inspecting method
     * @return set of decorated invokes
     */
    private <S> List<Invoke> processAnnotation( Class processor, Method method, List<Invoke> invokes, Class<S> type ) throws Exception {
        S annotation = findAnnotation( method, type );
        if ( annotation != null ) {
            List<Invoke> output = new ArrayList<Invoke>();
            Method processingMethod = processor.getMethod( "process", Invoke.class, type );
            for ( Invoke invoke : invokes ) {
                output.add( ( Invoke ) processingMethod.invoke( null, invoke, annotation ) );
            }
            return output;
        }
        return invokes;
    }

    /**
     * Finds desired annotation on the given method. If the annotation
     * is not set then method returns null.
     * @param <S> type of annotation
     * @param method investigated method
     * @param type type of annotation
     * @return found annotation or NULL if not set
     */
    private <S> S findAnnotation( Method method, Class<S> type ) {
        for ( Annotation annotation : method.getAnnotations() ) {
            if ( type.isAssignableFrom( annotation.getClass() ) ) {
                return ( S ) annotation;
            }
        }
        return null;
    }
}
