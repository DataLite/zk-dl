package cz.datalite.cache.model;


import cz.datalite.helpers.StringHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Vysledek funkce
 */
@SuppressWarnings("unused")
public class ServiceResult
{
    Map<Class<?>, Object> objects = new LinkedHashMap<>() ;
    String result ;

    public ServiceResult(String result)
    {
        this.result = result;
    }


    /**
     * @param objectTypeClass       typ vytvořeného (získaného) objektu
     * @param object                vytvořený (získaný) objekt
     * @param result                výsledek vytvoření (získáná)
     */
    public <ObjectType> ServiceResult(Class<ObjectType> objectTypeClass, ObjectType object, String result )
    {
        this.result = result ;
        putObject( objectTypeClass, object ) ;
    }

    /**
     * @param objectTypeClass       typ vytvořeného (získaného) objektu
     * @param object                vytvořený (získaný) objekt
     * @param result                výsledek vytvoření (získáná)
     */
    public <ObjectType> ServiceResult(Class<ObjectType> objectTypeClass, List<ObjectType> object, String result )
    {
        this.result = result ;
        putObject( objectTypeClass, object ) ;
    }

    /**
     * @return vytvořený objekt
     */
    public <ObjectType> ObjectType getObject( Class<ObjectType> objectTypeClass )
    {
        //noinspection unchecked
        return (ObjectType) objects.get( objectTypeClass );
    }

    /**
     * @return vytvořený objekt
     */
    public <ObjectType> List<ObjectType> getObjectList( Class<ObjectType> objectTypeClass )
    {
        //noinspection unchecked
        return (List<ObjectType>) objects.get( objectTypeClass );
    }

    /**
     * @param objectTypeClass typ vytvořeného (získaného) objektu
     * @param object          vytvořený (získaný) objekt
     */
    public <ObjectType> void putObject( Class<ObjectType> objectTypeClass, ObjectType object )
    {
        //noinspection unchecked
        objects.put( objectTypeClass, object ) ;
    }

    /**
     * @param objectTypeClass typ vytvořeného (získaného) objektu
     * @param object          vytvořený (získaný) objekt
     */
    public <ObjectType> void putObject( Class<ObjectType> objectTypeClass, List<ObjectType> object )
    {
        //noinspection unchecked
        objects.put( objectTypeClass, object ) ;
    }

    /**
     * @return OK nebo popis chyby
     */
    public String getResult()
    {
        return result ;
    }

    /**
     * @return příznak zda je výsledek OK
     */
    public boolean isOk()
    {
        return StringHelper.isEqualsIgnoreCase(result, "Ok") || result != null && result.toUpperCase().startsWith("OK -");
    }

    /**
     * @return příznak zda je došlo k chybě
     */
    public boolean isFail()
    {
        return ! isOk() ;
    }

    /**
     * @return vytvořené (získané) objekty
     */
    public Map<Class<?>, Object> getObjects()
    {
        return objects;
    }
}
