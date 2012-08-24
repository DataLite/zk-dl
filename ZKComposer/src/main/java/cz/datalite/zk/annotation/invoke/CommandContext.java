package cz.datalite.zk.annotation.invoke;

import java.util.Map;
import java.util.Set;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Property;
import org.zkoss.zk.ui.Component;

/**
 * Context of current invocation containing stateful data to provide full
 * context knowledge to the annotation handler. This object is designed to allow
 * handlers to be completely stateless. All statefull configuration will be
 * brought with the invocation.
 *
 * @author Karel Cemus <cemus@datalite.cz>
 */
public class CommandContext extends Context {

    /** target method to be invoked */
    protected String command;

    /** binder execution invocation parameter */
    protected Map<String, Object> commandArgs;

    /** binder execution invocation parameter */
    protected BindContext ctx;

    /** binder execution invocation parameter */
    protected Set<Property> notifys;

    public CommandContext( final Invoke invoker, final Object controller, final Component root ) {
        super( invoker, controller, root );
    }

    /** initialize the context before invocation */
    public void init( String command, Map<String, Object> commandArgs, BindContext ctx, Set<Property> notifys, final Component target ) {
        super.init( target );
        this.commandArgs = commandArgs;
        this.command = command;
        this.notifys = notifys;
        this.ctx = ctx;
    }

    /** clear the context after invocation */
    @Override
    public void clear() {
        super.clear();
        this.commandArgs = null;
        this.command = null;
        this.notifys = null;
        this.ctx = null;
    }

    public String getCommand() {
        return command;
    }

    public Map<String, Object> getCommandArgs() {
        return commandArgs;
    }

    public BindContext getCtx() {
        return ctx;
    }

    public Set<Property> getNotifys() {
        return notifys;
    }
}
