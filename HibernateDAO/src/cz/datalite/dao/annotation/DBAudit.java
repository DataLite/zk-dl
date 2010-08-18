package cz.datalite.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Označuje sloupce pro auditování pomocí databázových triggerů.
 * Pokud je na úrovni class (entity), budou se auditovat všechny sloupce.
 * Jinak je možné přidat anotaci pouze na některé sloupce. Při použití obou, má přednost anotace na úrovni třídy.
 * </p>
 * <p>Pro sloupce typu odkazu na entitu (typicky @ManyToOne) se při auditování kromě ID zapisuje i textový popis návazné entity.
 * Tento popis se složí ze sloupců označených v návazné entitě jako @MainColumn, to však lze přepsat pomocí anotace @DBAuditTarget
 * na vzdálené entitě, nebo pomocí vlastnosti targetColumns této anotace.
 *
 * @author Jiri Bubnik <bubnik at datalite.cz>
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface DBAudit
{
    /**
     * Určuje seznam sloupců z odkazované entity, ze kterých se má složit textový popis auditu.
     * Typicky se zadává pro @ManyToOne entity, pokud nevyhovuje jejich defaultní nastavení pro audit
     * (defaultní chování je určeno na odkazované entitě pomocí @DBAuditTarget resp. @MainColumn).
     */
    public String[] targetColumns() default "";

    /**
     * Zda se má audit opravdu provádět. Příznak má význam zejména v situaci,
     * kdy se nastaví audit pro celou entitu a je potřeba vypnout konkrétní sloupec.
     */
    public boolean audit() default true;
}
