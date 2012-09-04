package cz.datalite.zk.bind;

import cz.datalite.zk.annotation.invoke.CommandContext;
import cz.datalite.zk.annotation.invoke.Invoke;
import cz.datalite.zk.annotation.processor.AnnotationProcessor;
import cz.datalite.zk.annotation.processor.InvokeListener;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Property;
import org.zkoss.zk.ui.Component;

/**
 * Binder extension providing support ZK-DL for annotation processing
 * 
 * @author Karel Cemus <cemus@datalite.cz>
 */


public class AnnotationBinder extends Binder {
    
    @Override
    protected void doExecute( Component comp, String command, Map<String, Object> commandArgs, BindContext ctx, Set<Property> notifys ) {
        // class of view-model instance
        final Class type = getViewModel().getClass();
        // get the annotation processor of the view-model instance
        final AnnotationProcessor processor = AnnotationProcessor.getProcessor( type );

        // annotated method to be invoked
        final Method method = getCommandMethod( type, command );
        if ( method == null ) {
            LOGGER.debug( "Binder caught request for '{}' command but ZK BinderImpl doesn't know the mapping method. Execution failed.", command );
            return;
        }

        // get command invoker wrapped by the ZK-DL annotation invokers
        final Invoke invoker = processor.getCommandInvoker( method );
        if ( invoker == null ) {
            // report error and finish
            LOGGER.error( "Binder tries to invoke the command invoker on the method '{}' but the invoker wasn't found.", method.getName() );
            return;
        }

        // invoke the command invoker
        LOGGER.trace( "Binder invokes the command '{}' on the method '{}'.", command, method.getName() );

        // initialize the context
        final CommandContext context = new CommandContext( invoker, getViewModel(), getView() );
        context.init( command, commandArgs, ctx, notifys, comp );

        try {
            // invoke the command
            InvokeListener.invoke( context );
        } catch ( Exception ex ) {
            // something has happened, log it
            LOGGER.error( "Binder invoked command '{}' on the method '{}' but it failed.", new Object[]{ command, method.getName(), ex } );
        }
    }

    /** Resumes the execution of ZKOSS @Command */
    public void resumeCommand( Component comp, String command, Map<String, Object> commandArgs, BindContext ctx, Set<Property> notifys ) {
        super.doExecute( comp, command, commandArgs, ctx, notifys );
    }
}
