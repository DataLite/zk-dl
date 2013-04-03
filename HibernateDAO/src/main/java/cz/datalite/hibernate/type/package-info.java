/**
 * Hibernate user types.
 */
@TypeDefs(
        {
                @TypeDef(
                        name="ano_ne",
                        typeClass = CharBooleanUserType.class
                ),
                @TypeDef(
                        name="ano_ne_N",
                        typeClass = CharBooleanUserType.class,
                        parameters = {@Parameter(name = "defaultValue", value = "N")}
                ),
                @TypeDef(
                        name="ano_ne_A",
                        typeClass = CharBooleanUserType.class,
                        parameters = {@Parameter(name = "defaultValue", value = "A")}
                )
        }
)
package cz.datalite.hibernate.type;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;