package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation defines that property is used like model in MVC architecture.
 * It means that some component uses it for read/write data.</p>
 * <p>Property is published with its property name or with name defined in
 * the annotation. If getter or setter is created for this property, it is used
 * in getting or setting data. If getter or setter is not defined, value is
 * readed / setted directly from / to field even if it is private. Getter and
 * setter has to have the same name like a alias of this field. It means if name
 * is defined in this annotation then getter and setter has to have name like
 * definition in the annotation, no like a property name. Getter and setter
 * needn't be defined both.</p>
 * @author Karel Čemus <cemus@datalite.cz>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.TYPE } )
public @interface ZkModel {

    /**
     * Alias of this property field. With this name filed will be
     * accessible from zul also throw getters and setters. If it isn't
     * defined, property field name is used instead.
     * @return  name property alias
     **/
    public String name() default "";
}
