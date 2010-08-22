package cz.datalite.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Repository;

/**
 * Třída označená @DAO slouží pro správu Repository - typicky přístup do databáze.
 * Odpovídá anotaci @Repository ze Springu.
 *
 * @author Jiri Bubnik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@Repository
public @interface DAO {
}


