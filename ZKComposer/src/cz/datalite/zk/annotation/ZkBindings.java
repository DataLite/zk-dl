package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Register binding listeners
 * Listeners are defined in the array with {@link cz.datalite.zk.annotation.ZkBinding}.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ZkBindings {

    /**
     * Array of bindings
     * @return  bindings
     **/
    public ZkBinding[] bindings();
}
