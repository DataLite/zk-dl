package cz.datalite.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Scope;

/**
 * Třída označená @Controller slouží jako web vrstva aplikace.
 * Odpovídá anotaci @Controller ze Springu.<br/>
 *
 * Pro ZK Spring aplikace se konfiguruje jako scope="prototype"
 *
 * @author Jiri Bubnik
 * @deprecated use {@link org.springframework.stereotype.Controller} instead
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@Scope("prototype")
@org.springframework.stereotype.Controller
@Deprecated
public @interface Controller {
}