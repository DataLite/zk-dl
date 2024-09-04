package cz.datalite.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.transaction.annotation.Transactional;

/**
 * Třída označená @Service slouží jako business vrstva pro aplikaci.
 * Odpovídá anotaci @Service ze Springu.
 *
 * @author Jiri Bubnik
 * @deprecated use {@link org.springframework.stereotype.Service} instead
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@Transactional
@org.springframework.stereotype.Service
@Deprecated
public @interface Service {
}