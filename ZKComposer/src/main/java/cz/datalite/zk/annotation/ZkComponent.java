package cz.datalite.zk.annotation;

import cz.datalite.zk.composer.DLComposer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation marks a property in DLComposer as a view and inject the property with target Component after page composition.</p>
 *
 * <p>
 * Usage:<br/>
 * > @ZkComponent Textbox myTextboxId;<br/>
 * will call getFellow("myTextboxId") on "self" component in DLComposer.doAfterCompose().
 * </p>
 * <p>This will cause failure in doAfterCompose, if componentId is not found or the target component is of different type.</p>
 *
 * @author Jiri Bubnik
 * @see DLComposer
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ZkComponent
{
    /**
     * ZUL ID of the component. If not set, property name will be used.
     *
     * @return  ZUL ID of the component.
     **/
    public String id() default "";

    /**
     * Should an exception be thrown in case the component is not available?
     */
    public boolean mandatory() default true;
}
