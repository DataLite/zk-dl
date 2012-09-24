package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Field defined in ZUL with $ are <b>read only</b>. Mostly they are
 * controllers for components because they are evaluated during compose.
 * This annotation defines that this field is used like controller in MVC
 * architecture. This field needn't has a getter if it isn't necessary.
 * If getter is defined then is used. Else is used direct access to the
 * field even if field is private.</p>
 * <p>Field can be published with its own property name or with pseudonym
 * defined in this annotation. If pseudonym is defined, getter also has to
 * used this pseudonum, no native property name.</p>
 * @author Karel Cemus
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.TYPE } )
public @interface ZkController {

    /**
     * Property pseudonym which will be accessible from ZUL. If pseudonym
     * is not defined, native property name is used.
     * @return  name property pseudonym
     **/
    public String name() default "";
}
