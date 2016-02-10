package cz.datalite.hibernate.type;


import java.sql.Types;

/**
 * Typ pro konverze textové DB reprezentace na enum
 */
@SuppressWarnings("WeakerAccess")
public class EnumIntType extends AbstractEnumType
{
    @Override
    protected int getSqlType()
    {
        return Types.NUMERIC ;
    }
}
