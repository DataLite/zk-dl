package cz.datalite.dao.plsql.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace pro definici položky PL/SQL struktury
 */
@SuppressWarnings("UnusedDeclaration")
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface SqlField
{
    /**
     * @return jméno položky v SQL struktuře
     */
    String value() ;
}
