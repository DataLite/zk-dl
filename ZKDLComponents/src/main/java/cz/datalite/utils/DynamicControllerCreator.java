package cz.datalite.utils;

import cz.datalite.helpers.StringHelper;
import org.zkoss.lang.Library;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings({"TryWithIdenticalCatches", "unused"})
public class DynamicControllerCreator<TargetType>
{
    private String libraryName ;
    private String defaultClassName ;
    private Class<? extends TargetType> defaultClass ;
    private Class[] constructorArgumentsTypes;

    private Class<? extends TargetType>  controllerClass ;
    private Constructor<? extends TargetType> controllerConstructor;

    public DynamicControllerCreator( String libraryName, Class<? extends TargetType> defaultClass,
                                     Class ... constructorArgumentsTypes )
    {
        this.libraryName = libraryName;
        this.defaultClass = defaultClass ;
        this.constructorArgumentsTypes = constructorArgumentsTypes ;
    }

    public DynamicControllerCreator( String libraryName, String defaultClassName,Class ... constructorArgumentsTypes)
    {
        this.libraryName = libraryName ;
        this.defaultClassName = defaultClassName ;
        this.constructorArgumentsTypes = constructorArgumentsTypes;
    }

    /**
     * @return třída konstruktoru
     */
    private Class<? extends TargetType> getControllerClass()
    {
        if ( controllerClass == null )
        {
            String libraryValue = ( ! StringHelper.isNull( libraryName ) ) ? Library.getProperty(libraryName, defaultClassName ) : defaultClassName ;

            if ( ! StringHelper.isNull( libraryValue ) )
            {
                try
                {
                    //noinspection unchecked
                    controllerClass = (Class<? extends TargetType>) Class.forName( libraryValue );
                }
                catch (ClassNotFoundException e)
                {
                    throw new IllegalStateException( e ) ;
                }
            }
            else
            {
                controllerClass = defaultClass ;
            }
        }

        if ( controllerClass == null )
        {
            throw new IllegalStateException( "Missing controllerClass"  ) ;
        }

        return controllerClass;
    }

    /**
     * @return konstruktor pro vytvoreni nove instance tridy
     */
    private Constructor<? extends TargetType> getControllerConstructor()
    {
        if ( controllerConstructor == null  )
        {
            try
            {
                controllerConstructor = getControllerClass().getConstructor(constructorArgumentsTypes) ;
            }
            catch (NoSuchMethodException e)
            {
                throw new IllegalStateException( e ) ;
            }
        }

        return controllerConstructor;
    }

    /**
     * Vytvoření nové instance
     *
     * @param arguments     parametry kontruktoru
     * @return nová instance
     */
    public TargetType create( Object ... arguments )
    {
        try
        {
            return getControllerConstructor().newInstance( arguments ) ;
        }
        catch (InstantiationException e)
        {
            throw new IllegalStateException( e ) ;
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException( e ) ;
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }
}
