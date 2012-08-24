package cz.datalite.zk.annotation.invoke;

import java.lang.reflect.Method;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Context of current invocation containing stateful data to provide full
 * context knowledge to the annotation handler. This object is designed to allow
 * handlers to be completely stateless. All statefull configuration will be
 * brought with the invocation.
 *
 * @author Karel Cemus <cemus@datalite.cz>
 */
public class ZkEventContext extends Context{

    /** target method to be invoked */
    private final Method method;

    /** invocation initializator */
    private Event event;

    public ZkEventContext( Method method, Invoke invoker, final Object controller, final Component root ) {
        super( invoker, controller, root );
        this.method = method;
    }
    
    /** initialize the context before invocation */
    public void init( final Event event ) {
        super.init( event.getTarget() );
        this.event = event;
    }
    
    /** clear the context after invocation */
    @Override
    public void clear() {
        super.clear();
        this.event = null;
    }

    public Event getEvent() {
        return event;
    }


    public Method getMethod() {
        return method;
    }
}
