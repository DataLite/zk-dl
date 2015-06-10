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
import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.annotation.ZkAsync;
import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkBindings;
import cz.datalite.zk.annotation.ZkBlocking;
import cz.datalite.zk.annotation.ZkConfirm;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkException;
import cz.datalite.zk.annotation.ZkExceptions;
import cz.datalite.zk.annotation.ZkRole;
import cz.datalite.zk.annotation.invoke.CommandInvoker;
import cz.datalite.zk.annotation.invoke.Invoke;
import cz.datalite.zk.annotation.invoke.MethodInvoker;
import cz.datalite.zk.annotation.invoke.ZkAsyncHandler;
import cz.datalite.zk.annotation.invoke.ZkBindingHandler;
import cz.datalite.zk.annotation.invoke.ZkBlockingHandler;
import cz.datalite.zk.annotation.invoke.ZkConfirmHandler;
import cz.datalite.zk.annotation.invoke.ZkEventContext;
import cz.datalite.zk.annotation.invoke.ZkExceptionHandler;
import cz.datalite.zk.annotation.invoke.ZkRoleHandler;
import org.zkoss.bind.annotation.Command;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Annotation Processor handles all ZK annotations on the methods. For each
 * annotation there is defined specific annotation processor which handles that
 * annotation type and provides desired functionality. AnnotationProcessor cares
 * about right order of decorating {@link Invoke} object because if they would
 * be decorated in wrong order then the final functionality could be
 * undeterministic or at least undesired.</p>
 *
 * <p>Instance of annotation processor is bound to the instance of component
 * controller but there is possible way to refactor it as unbound to anything.
 * Then the class would create templates which would be cloned and bound to the
 * specific instance.</p>
 *
 * @author Karel Cemus
 */
public class AnnotationProcessor<T> {

    /** Map of created processors. Serves as annotation cache */
    private static final Map< Class, AnnotationProcessor> processors = new HashMap<>();

    /** list of processors producing method invokers or similar */
    private static final List<Initializer> initializers = new ArrayList<>();
    
    /** list of processors producing wrappers for invoke object providing additional
     * functionality */
    private static final List<Wrapper> wrappers = new ArrayList<>();

    /** configuration key in property files */
    private static final String CONFIG = "zk-dl.annotation.cache";

	/**
	 * Library property key prefix to redefine annotation {@link cz.datalite.zk.annotation.invoke.Handler}s. Prefix key is followed by a class FQN.
	 * For example {@code zk-dl.annotation.handler.cz.datalite.zk.annotation.ZkException=mypackage.MyExceptionHandler} replace default {@link cz.datalite.zk.annotation.invoke.ZkExceptionHandler} with custom one.
	 */
	private static final String CONFIG_HANDLER_PREFIX = "zk-dl.annotation.handler";

    /**
     * Library property key to define list user annotations for invoker. Value separator is comma. Value must be FQN
     * For example {@code zk-dl.annotation.users=cz.datalite.zk.annotation.ZkAutoConfirm} append new invoker ZkAutoConfirm
     * For define Handler for user annotations use library property  with prefix zk-dl.annotation.handler
     */
    private static final String CONFIG_USER_ANNOTATIONS = "zk-dl.annotation.users";

    /**
     * Library property key to define system annotation for invoker before they will add custom annotace. Value is FQN class
     * For example {@code zk-dl.annotation.users.before=cz.datalite.zk.annotation.ZkConfirm} user annotation is adding before cz.datalite.zk.annotation.ZkConfirm
     */
    private static final String CONFIG_USER_ANNOTATIONS_BEFORE = "zk-dl.annotation.users.before";

    /** caching is enabled / disabled */
    private static boolean cache = true;


    private static String userAnnotationsInsertBefore ;

    static {

        userAnnotationsInsertBefore = Library.getProperty( CONFIG_USER_ANNOTATIONS_BEFORE ) ;

        //noinspection unchecked
        initializers.add( new GeneralInitializerProcessor( Command.class, CommandInvoker.class ) );
        //noinspection unchecked
        initializers.add( new GeneralInitializerProcessor( ZkEvent.class, MethodInvoker.class ) );
        //noinspection unchecked
        initializers.add( new GeneralInitializerProcessor( ZkEvents.class, MethodInvoker.class ) );

        generateWrappers() ;

        // loading property of caching or not
        cache = Boolean.parseBoolean( System.getProperty( CONFIG, Library.getProperty( CONFIG, "true" ) ) );
    }

	/** List of cached ZkEvents */
    private Map<Method,Set<MethodCache>> zkEvents = new HashMap<>();
    
    /** List of cached Commands */
    private Map<Method,Cache> commands = new HashMap<>();

    /**
     * Generate processor wrapper list
     */
    private static void generateWrappers() {
        addWrapper(ZkException.class);
        addWrapper(ZkExceptions.class);
        addWrapper(ZkBinding.class);
        addWrapper(ZkBindings.class);
        addWrapper(ZkConfirm.class);
        addWrapper(ZkRole.class);
        addWrapper(ZkBlocking.class);
        addWrapper(ZkAsync.class);

        if ( StringHelper.isNull( userAnnotationsInsertBefore ) ) {
            addUserAnnotationsWrapper() ;
        }
    }

    /**
     * Add user annotation wrappers
     */
    private static void addUserAnnotationsWrapper() {
        String userAnnotations = Library.getProperty( CONFIG_USER_ANNOTATIONS ) ;

        if ( ! StringHelper.isNull( userAnnotations ) ) {
            for( String fqn : userAnnotations.split(",") ) {
                try {
                    //noinspection unchecked
                    wrappers.add(createProcessor((Class<? extends Annotation>) Class.forName(fqn))) ;
                }
                catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Class not found: '" + fqn + "'. Check zk.xml parameter " + CONFIG_USER_ANNOTATIONS + ".", e) ;
                }
            }
        }
    }

    /**
     * Add annotation class to wrappers list
     *
     * @param annotation        added annotation type
     */
    private static void addWrapper( Class<? extends Annotation> annotation ) {
        if ( StringHelper.isEquals( annotation.getCanonicalName(), userAnnotationsInsertBefore ) ) {
            addUserAnnotationsWrapper() ;
        }
        wrappers.add( createProcessor( annotation ) ) ;
    }

    /**
     * Returns AnnotationProcessor for existing class
     *
     * @param type annotated class
     * @return processor
     */
    public static <T> AnnotationProcessor<T> getProcessor( Class<T> type ) {
        //noinspection unchecked
        AnnotationProcessor<T> ap = processors.get( type );
        if ( ap == null ) {
            ap = new AnnotationProcessor<>(type);
            if ( cache ) // if caching is enabled
                processors.put( type, ap );
        }
        return ap;
    }

    /**
     * <p>Registers all detected events to the component and bound events to
     * given controller. Bounds invokes to the components to handle
     * executions.</p>
     *
     * @param root targeted master component
     * @param controller object with given
     */
    public void registerZkEventsTo( Component root, T controller ) {
        for ( final Set<MethodCache> set : zkEvents.values() ) {
            for ( final MethodCache cached : set ) {
                // get the target component
                final Component target = cached.invoker.bind( root );
                // build the event context
                final ZkEventContext context = new ZkEventContext( cached.method, cached.invoker, controller, root );
                // attach listener
                //noinspection unchecked
                target.addEventListener( cached.methodInvoker.getEventName(), new InvokeListener( context ) );
            }
        }

        // allow handlers to do custom bindings
        for ( final Cache cached : commands.values() ) {
            cached.invoker.bind(root);
        }
    }
    
    /**
     * Returns all command invokers bound to given method including all detected
     * wrappers. This can be used for example for manual invoking the command
     *
     * @param method decorated method
     */
    public Invoke getCommandInvoker( final Method method ) {
        // get the command mapped on the method
        final Cache command = commands.get( method );
        // if the command is cached return the invoker
        return command == null ? null : command.invoker;
    }

    private AnnotationProcessor( Class<T> type ) {
        for ( final Method method : ReflectionHelper.getAllMethods( type ) ) {
            // extract core invokers
            List<Invoke> invokers = processInvokerAnnotations( method );
            
            // extract wrapping invokers, preserves order
            List<Invoke> wrapped = processWrappingAnnotations( method, invokers );
            
            // cache and separate invokers
            store( method, invokers, wrapped );
        }
    }
    
    /**
     * Classifies the zkEvent invokers and command invokers to sepparate maps and stores
     * them into the cache to allow quick reattatching without another class processing.
     * 
     * Both list with invokers should be same sized.
     * 
     * @param method proccessed method
     * @param invokers core invokers
     * @param wrapped  wrapped invokers
     */
    private void store( final Method method, final List<Invoke> invokers, final List<Invoke> wrapped ) {
        assert invokers.size() == wrapped.size();
               
        for ( int i = 0; i < invokers.size(); ++i ) {
            // core invoker
            final Invoke invoker = invokers.get( i );
            // wrapped invoker
            final Invoke wrapper = wrapped.get( i );
            
            if ( invoker instanceof MethodInvoker ) {
                if ( zkEvents.get( method ) == null ) zkEvents.put( method, new HashSet<>() );
                // store method invoker
                zkEvents.get( method ).add( new MethodCache( method, (MethodInvoker) invoker, wrapper ) );
            } else {
                // store command invoker
                commands.put( method, new Cache( method, wrapper ) );
            }            
        }
    }

    /**
     * <p>Basic method responsible for processing annotations on the methods.
     * This method is called by composer processor and the rest of annotating is
     * proceed here.</p>
     *
     * <p>This method holds processing order of annotations. There is defined
     * exact order from the most inner to the most outer. If somebody whats to
     * add another annotation then he have to decide which annotatin should it
     * be and set its order in decorater order. Then the adding the new row on
     * the rigth place is enough to register it.</p>
     * 
     * <p>This method processes the core invokers only.</p>
     *
     * @param method proceed method
     */
    private List<Invoke> processInvokerAnnotations( Method method ) {
        // core invokers
        List<Invoke> invokers = new ArrayList<>();
        
        // read all core invokers
        for ( Initializer initializer : initializers ) {
            invokers.addAll( initializer.process( method ) );
        }
        
        return invokers;
    }
    
    /**
     * <p>Basic method responsible for processing annotations on the methods.
     * This method is called by composer processor and the rest of annotating is
     * proceed here.</p>
     *
     * <p>This method holds processing order of annotations. There is defined
     * exact order from the most inner to the most outer. If somebody whats to
     * add another annotation then he have to decide which annotatin should it
     * be and set its order in decorater order. Then the adding the new row on
     * the rigth place is enough to register it.</p>
     * 
     * <p>This method processes the wrapping invokers only.</p>
     *
     * @param method proceed method
     */
    private List<Invoke> processWrappingAnnotations( Method method, List<Invoke> invokers ) {
        // wrapped invokers
        List<Invoke> invokes = new ArrayList<>();
        
        if ( !invokers.isEmpty() ) {
            invokes.addAll( invokers );
            final List<Invoke> output = new LinkedList<>();
            for ( Invoke invoke : invokes ) {
                output.add( processWrappers( method, invoke) );
            }
            invokes = output;
        }
        
        return invokes;
    }
    
    
    /** 
     
     * Process all annotation wrappers on given method and decorate the given
     * core invoker.
     * @param method method to be processed
     * @param invoker invoker to be decorated
     * @return decorated invoker
     */
    private Invoke processWrappers( Method method, Invoke invoker ) {
        Invoke invoke = invoker;
        for ( Wrapper wrapper : wrappers ) {
            invoke = wrapper.process( method, invoke );
        }
        return invoke;
    }

    private static class Cache {

        /** method to be executed */
        protected final Method method;

        /** wrapped invoker */
        protected final Invoke invoker;

        public Cache( Method method, Invoke invoker ) {
            this.method = method;
            this.invoker = invoker;
        }
    }

    private static class MethodCache extends Cache {

        /** inner invoker */
        protected MethodInvoker methodInvoker;

        public MethodCache( Method method, MethodInvoker methodInvoker, Invoke wrappedInvoker ) {
            super( method, wrappedInvoker );
            this.methodInvoker = methodInvoker;
        }
    }


	/**
	 * Creates a new processor
	 * @param annotation
	 * @param <U>
	 * @return
	 */
	private static <U> GeneralWrapperProcessor<U> createProcessor(Class<U> annotation) {
		return new GeneralWrapperProcessor<>(annotation, findHandler(annotation));
	}

	/**
	 * Finds handler according to library property {@link cz.datalite.zk.annotation.processor.AnnotationProcessor#CONFIG_HANDLER_PREFIX} or default one.
	 * @param annotation {@link cz.datalite.zk.annotation}
	 * @return
	 */
	private static Class<?> findHandler(Class annotation) {
		Class defaultHandler = null ;
		String handlerClassName = Library.getProperty(
				String.format("%s.%s", CONFIG_HANDLER_PREFIX, annotation.getName()));

        if ( StringHelper.isNull( handlerClassName ) ) {
            defaultHandler = findDefaultHandler(annotation) ;
        }

		try {
			return handlerClassName == null ? defaultHandler : Class.forName(handlerClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					String.format("Handler '%s' for '%s' was not found, default handler: '%s'. See Zk library property %s.",
							handlerClassName, annotation, defaultHandler == null ? "missing" : defaultHandler, CONFIG_HANDLER_PREFIX
					), e);
		}
	}

	/**
	 * Default handler configuration
	 * @param annotation
	 * @return
	 */
	private static Class findDefaultHandler(Class annotation) {
		if (annotation.isAssignableFrom(ZkException.class) || annotation.isAssignableFrom(ZkExceptions.class)) {
			return ZkExceptionHandler.class;
		} else if (annotation.isAssignableFrom(ZkBinding.class) || annotation.isAssignableFrom(ZkBindings.class)) {
			return ZkBindingHandler.class;
		} else if (annotation.isAssignableFrom(ZkConfirm.class)) {
			return ZkConfirmHandler.class;
		} else if (annotation.isAssignableFrom(ZkRole.class)) {
			return ZkRoleHandler.class;
		} else if (annotation.isAssignableFrom(ZkBlocking.class)) {
			return ZkBlockingHandler.class;
		} else if (annotation.isAssignableFrom(ZkAsync.class)) {
			return ZkAsyncHandler.class;
		}
		throw new IllegalArgumentException(String.format("Cannot find default handler for annotation %s.", annotation));
	}
}
