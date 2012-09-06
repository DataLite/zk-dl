package cz.datalite.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Identifikace hlavních sloupců v entitě.</p>
 *
 * <p>Např. pro entitu uživatele to bude login, případně jméno a příjmení.</p>
 * <p>Využití:
 * <ul>
 *   <li>Databázový audit: v kombinaci s DBAudit - pokud se odkazuje na entitu přes @ManyToOne,
 *     do auditu se zapíše mezerou oddělený seznam sloupců označených s @MainColumn. Lze ale přepsat anotací @DBAuditTarget.</li>
 *   <li>Do budoucna využití např. pro automatické generování toString() apod.</li>
 * </ul>
 * <p>
 * @author Jiri Bubnik <bubnik at datalite.cz>
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MainColumn {

}
