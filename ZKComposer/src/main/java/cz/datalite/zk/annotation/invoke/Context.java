package cz.datalite.zk.annotation.invoke;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;

/**
 * Context of current invocation containing stateful data to provide full
 * context knowledge to the annotation handler. This object is designed to allow
 * handlers to be completely stateless. All statefull configuration will be
 * brought with the invocation.
 *
 * @author Karel Cemus <cemus@datalite.cz>
 */
public class Context {

    /** instance of logger */
    protected static final Logger LOGGER = LoggerFactory.getLogger( Context.class );
    
    /** parameters added by handlers */
    protected final Map<String, Object> parameters = new HashMap<String, Object>();
    
    /** outer invocation instance to allow full invocation of some phase */
    protected final Invoke invoker;
    
    /** instance of controller / view-model */
    protected final Object controller;
    
    /** root component in the page, usually the window */
    protected final Component root;

    /** component which was the target */
    protected Component target;   

    public Context( Invoke invoker, final Object controller, final Component root ) {
        this.invoker = invoker;
        this.controller = controller;
        this.root = root;
    }
    
    /** initialize the context before invocation */
    public void init( final Component target ) {
        this.target = target;
        
    }
    
    /** clear the context after invocation */
    public void clear() {
        this.target = null;
        this.parameters.clear();
    }

    public Object getController() {
        return controller;
    }

    public Invoke getInvoker() {
        return invoker;
    }

    
    public Component getRoot() {
        return root;
    }

    public Component getTarget() {
        return target;
    }
    
    public void putParameter( final String key, final Object value ) {
        if ( parameters.containsKey( key ) )
            LOGGER.warn( "Attempt to overwrite context parameter '{}'"
                    + " but it is not allowed to prevent accidental"
                    + " overwriting of the value by another handler."
                    + " The request was ignore, value remained unchanged."
                    + " If it was the intention look at the method"
                    + " removeParameter().", key );
        else parameters.put( key, value );
    }
    
    public Object getParameter( final String key ) {
        return parameters.get( key );
    }
    
    public void removeParameter ( final String key ) {
        if ( parameters.containsKey( key ) )
            parameters.remove( key );
        else LOGGER.warn( "Attempt to remove context parameter '{}' but it is not set.", key );
    }
}
