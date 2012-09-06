/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Set value of property to parameter value in doBeforeCompose(). Used by DLComposer in doBeforeComposet to set its fields to parameter values.</p>
 * 
 * This method checkes Zk's maps (first map where the parameter is set is used):
 * <ul>
 *   <li>execution.getArg() - usually set by createComponents(comp, parent, arg) the arg map.</li>
 *   <li>execution.getAttributes() - usually set &lt;include param="value"&gt;.</li>
 *   <li>execution.getParameters() - request parameters.</li>
 * </ul>
 *
 * <p>You can set the parameter name explicitly, or the property name is used.</p>
 * <p>Untill you set required to false, an exception is thrown if this parametr is not found in any of the maps.</p>
 * In some cases you might want to create new instance of the object automatically, if the parameter is either not set at all, or set to null.
 * Use createIfNull to autocreate new instances. However, parameter class has to implement default public constructor.
 *
 * @author Jiri Bubnik
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.FIELD, ElementType.METHOD} )
public @interface ZkParameter {

    /**
     * Parameter name. If not set, default is the property name.
     *
     * @return  name name of the parameter.
     **/
    public String name() default "";

    /**
     * Throw an error, if the parameter is not set.
     * If false and the parameter is not set, ZkParameter annotation is ignored alltogether (createIfNull is ignored).
     *
     * @return true if should throw an error (default).
     */
    public boolean required() default true;

    /**
     * If the parameter is not set or is set to null, create new instance of the object with default constructor.
     *
     * @return true if new object should be created (must be set by user to ensure user knows what's going on).
     */
    public boolean createIfNull() default false;
    
}
