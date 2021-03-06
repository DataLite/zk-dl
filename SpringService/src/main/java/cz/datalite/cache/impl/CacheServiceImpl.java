package cz.datalite.cache.impl;

import cz.datalite.cache.CacheService;
import cz.datalite.cache.model.ServiceResult;
import cz.datalite.helpers.EqualsHelper;
import cz.datalite.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Cache pro uložení vytvořených objektů
 */
@SuppressWarnings("unused")
@Service
public class CacheServiceImpl implements CacheService
{
    private ThreadLocal<Map<Class<?>, Map>> values = new ThreadLocal<Map<Class<?>, Map>>()
    {
        @Override
        protected Map<Class<?>, Map> initialValue()
        {
            return new LinkedHashMap<>() ;
        }
    } ;
    private ThreadLocal<Boolean> enabledForThread = new ThreadLocal<Boolean>()
    {
        @Override
        protected Boolean initialValue()
        {
            return false ;
        }
    } ;


    @Override
    public boolean isEnabled()
    {
        return enabledForThread.get() ;
    }

    @Override
    public boolean setEnabled(boolean value)
    {
        boolean ret = isEnabled() ;

        enabledForThread.set( value ) ;

        return ret ;
    }

    @Override
    public <CacheType, XmlType, DatabaseType> void addToCache( Class<CacheType> cacheType, XmlType key, DatabaseType value )
    {
        if ( ! isEnabled() )
        {
            return ;
        }

        Map<XmlType, DatabaseType> arrays = new LinkedHashMap<>() ;

        if ( values.get().containsKey(cacheType) )
        {
            //noinspection unchecked
            arrays = values.get().get(cacheType) ;
        }
        else
        {
            values.get().put(cacheType, arrays) ;
        }

        arrays.put( key, value ) ;
    }

    @Override
    public <CacheType, XmlType, DatabaseType> DatabaseType removeFromCache(Class<CacheType> cacheType, XmlType key, DatabaseType value)
    {
        if ( ! isEnabled() )
        {
            return null ;
        }

        Map<XmlType, DatabaseType> arrays = new LinkedHashMap<>() ;

        if ( values.get().containsKey(cacheType) )
        {
            //noinspection unchecked
            arrays = values.get().get(cacheType) ;
        }

        DatabaseType ret = null ;

        if ( arrays.containsKey( key ) )
        {
            if ( arrays.get( key ) instanceof Collection )
            {
                Collection collection = (Collection) arrays.get( key );

                for( Object obj : collection )
                {
                    if (EqualsHelper.isEquals( obj, value ) )
                    {
                        //noinspection unchecked
                        ret = (DatabaseType) obj;
                        break ;
                    }
                }

                if ( ret != null )
                {
                    collection.remove(ret);
                }

                if ( collection.isEmpty() )
                {
                    arrays.remove( key ) ;
                }
            }
            else
            {
                ret = arrays.get( key ) ;
                arrays.remove( key ) ;
            }
        }

        return ret ;
    }

    @Override
    public <CacheType, XmlType> boolean isExistsInCache( Class<CacheType> cacheType, XmlType key )
    {
        return ( isEnabled() ) && ( values.get().containsKey(cacheType) ) && ( values.get().get(cacheType).containsKey( key ) ) ;
    }

    @Override
    public <CacheType, XmlType, DatabaseType> boolean isExistsInCache( Class<CacheType> cacheType, XmlType key, DatabaseType value )
    {
        if ( isExistsInCache( cacheType, key ) )
        {
            Object obj = getValueFromCache( cacheType, key ) ;

            if ( obj instanceof Collection )
            {
                return ((Collection) obj).contains( value ) ;
            }

            return EqualsHelper.isEqualsNull( obj, value ) ;
        }

        return false ;
    }

    @Override
    public <CacheType> boolean isExistsInCache(Class<CacheType> cacheType) {
        return isEnabled() && values.get().containsKey(cacheType);
    }


    @Override
    public <CacheType, XmlType, DatabaseType> DatabaseType getValueFromCache( Class<CacheType> cacheType, XmlType key )
    {
        if ( ( isEnabled() ) && ( values.get().containsKey(cacheType) ) )
        {
            //noinspection unchecked
            return (DatabaseType) values.get().get(cacheType).get( key );
        }

        return null ;
    }

    @Override
    public <XmlType, DatabaseType> DatabaseType getValueFromCache(String regExpClassName, XmlType key)
    {
        if ( ! isEnabled() )
        {
            return null ;
        }

        for( Map.Entry<Class<?>, Map> entry : values.get().entrySet() )
        {
            if ( entry.getKey().getCanonicalName().matches( regExpClassName ) )
            {
                //noinspection unchecked
                return (DatabaseType) entry.getValue().get( key );
            }
        }

        return null ;
    }

    @Override
    public void clear()
    {
        values.get().clear() ;
    }

    @Override
    public <XmlType> void addServiceResultToCache(@NotNull XmlType key, @NotNull ServiceResult value)
    {
        if ( isEnabled() )
        {
            for (Map.Entry<Class<?>, Object> entry : value.getObjects().entrySet())
            {
                addToCache(entry.getKey(), key, entry.getValue());
            }
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<?> getAllValues(Collection<Class<?>> cacheTypes) {
        List<?> ret = new ArrayList<>();
        Map<Class<?>, Map> cacheMap = values.get();
        for (Class<?> cacheType : cacheTypes) {
            Map map = cacheMap.get(cacheType);
            ret.addAll(map.values());
        }
        return ret;
    }

    @Override
    public <CacheType> Map getAllValues(Class<CacheType> cacheType) {
        Map map = values.get().get(cacheType);
        if (map == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(map);
        }
    }

    @Override
    public Set<Class<?>> getAllClasses() {
        return Collections.unmodifiableSet(values.get().keySet());
    }
}
