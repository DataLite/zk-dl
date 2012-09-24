package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines binding behaviour before and after this method. This
 * has to be used with {@link ZkEvent}. Default settings are not save binding
 * before the method and loadAll after the method. There are options save-before
 * and load-after. Binding can by applied on whole page or on the specific
 * component defined in the @component.
 *
 * @author Karel Cemus
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ZkBinding {

    /**
     * Specific component id which should be target of the binding.
     * Default setting is window.
     * @return binding targer componet
     */
    public String component() default "";

    /**
     * Defines if save-binding should be called before this method.
     * In the default setting is false
     * @return save binding before this method
     */
    public boolean saveBefore() default false;

    /**
     * Defines if load-binding should be called after this method.
     * In the default setting is true
     * @return load binding after this method
     */
    public boolean loadAfter() default true;
}
