package cz.datalite.zk.composer;

import cz.datalite.helpers.ReflectionHelper;
import cz.datalite.helpers.StringHelper;
import cz.datalite.helpers.ZKHelper;
import cz.datalite.zk.annotation.*;
import cz.datalite.zk.annotation.processor.AnnotationProcessor;
import cz.datalite.zk.composer.listener.DLDetailController;
import cz.datalite.zk.composer.listener.DLMainModel;
import cz.datalite.zk.composer.listener.DLMasterController;
import org.zkoss.lang.Classes;
import org.zkoss.lang.SystemException.Aide;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericAutowireComposer;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Composer for MVC development.</p>
 * <p>It is like ZK's GenericForwardComposer, but you have to explicitly bound properties and methods with annotations:
 * <ul>
 *   <li>@ZkEvent - register event on component and call annotated method when event fires</li>
 *   <li>@ZkComponent - attach ZUL component to this propery
 *   <li>@ZkController - is accessible from ZUL page whith the {controller}.property name.
 *         Default value for {controller} is "ctl" unless you set it to other value with @Zkcontroller(name="controller") on class level.
 *         Example: @ZkController ListControll listCtl;  ZUL: controller="${ctl.listCtl}" sets controller. Controller is read only.
 *   </li>
 *   <li>@ZkModel - is accessible from ZUL page whith the {model}.property name.
 *         Default value for {model} is "ctl" unless you set it to other value with @ZkModel(name="model") on class level.
 *         Example: @ZkModel String modelProperty = "xx";  ZUL: value="@{ctl.modelProperty}" sets value to "xx". Model is read/write.
 *   </li>
 *   <li>@ZkParameter - checks ZK's parameter maps for parameter name and set field to this value in doBeforeCompose().
 *           It checks these maps: execution.getArg(), execution.getAttributes(), execution.getParameters() and uses first map with this parameter set.
 *   </li>
 *   <li>@ZkBinding - save binding before method invocation and/or load after method invocation.</li>
 *   <li>@ZkConfirm - ask user with question dialog befor method is invoked</li> *
 * </ul>
 *  Check those annotations documentation for more information. Although it extends GenericAutowireComposer it <b>does not</b> wires
 *  custom varibles. It is used solely for implicit objects.
 * <p>
 *
 * <p>This class implements map interface. This is usefull for map access from ZUL or with binding. In doBeforeComposeChildren, instance of controller
 * is set to component variable as "ctl" (or custom name) and is immediately from ZUL processing - ctl.property will access this map, which
 * will return property mapped by @ZkController or @ZkModel.</p>
 *
 * <p>Similar to GenericAutowireComposer it has basic protected properties like session, requestScope, arg etc., but it doesn't wire all
 *  properties and methods autmatically (because it can be performance bottleneck if you use multiple composers with many methods.)</p>
 *
 * <p>Notice that since this composer kept references to the components, single
 * instance object cannot be shared by multiple components.</p>
 *
 * <p>It implements DLMasterController interface, while each controller can be master. Defualt implementation only constructs list of child controllers
 * and resend each message to all children that implements DLDetailController interface.</p>
 * <p>Usage for automatic master/detail setup:<br/>
 * &lt;include src="xx" masterController="${ctl}"&gt;<br/>
 * Include child page into master page and set masterController to this variable. DLComposer will connect master/detail automatically.
 * </p>
 *
 * @author Jiri Bubnik
 */
public class DLComposer<T extends DLMainModel> extends GenericAutowireComposer implements java.util.Map<String, Object>, DLMasterController<T>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger( DLComposer.class );
    
    /** Map model name to field representing this model */
    private final Map<String, Field> zkModels = new java.util.HashMap<String, Field>();

    /** Map controller name to field representing this model */
    private final Map<String, Field> zkControllers = new java.util.HashMap<String, Field>();

    /** Master controller for event propagation in master/detail. **/
    private DLMasterController masterController;

    /** Model in master/detail. **/
    private T masterControllerModel;

    /** List of child controller for master / detail page composition. **/
    private List<DLDetailController> detailControllers = new LinkedList<DLDetailController>();

    /**
     * <p>Befor main component children are created, setup master/detail and publish controller variables into component namespace.
     * You can than use value="${ctl.property}" in child components. This method is called by ZK framework.</p>
     *
     * DLComposer extension here:
     * <ul>
     *   <li>sets "self" variable to master component</li>
     *   <li>calls setupMasterController() - if </li>
     *   <li>validates @ZkXXX annotations - e.g. @ZkBinding can be used only with @ZkEvent</li>
     *   <li>loads all @ZkXXX annotations</li>
     * </ul>
     * 
     * @param comp master component
     * @throws Exception
     */
    @Override
    public void doBeforeComposeChildren( final Component comp ) throws Exception {
        super.doBeforeComposeChildren( comp );

        // setup self in advance - it might be used in composition.
        self = comp;

        // Master / detail 
        setupMasterController();

        // check that annotations are correct
        validMethodAnnotations( this.getClass() );

        // setup parameters
        setupZkParameters();

        // publish controller into component namespace. It can be than accessed from ZUL as "ctl.xxx".
        // defualt value is ctl, but it can be changed with @ZkController annotation on class level
        comp.setAttribute( loadControllerClass( this.getClass() ), this, Component.COMPONENT_SCOPE );

        // the same for model. The default value is same as controller as well. It is more convenient to have only one variable to access controller
        // however, you can change the value with @ZkModel annotation on class level
        if ( !loadControllerClass( this.getClass() ).equals( loadModelClass( this.getClass() ) ) ) {
            comp.setAttribute( loadModelClass( this.getClass() ), this, Component.COMPONENT_SCOPE );
        }

        // setup model and controller fields and methods.
        zkModels.putAll( loadModelFields( this.getClass() ) );
        zkModels.putAll( loadModelMethod( this.getClass() ) );
        zkControllers.putAll( loadControllerFields( this.getClass() ) );
        zkControllers.putAll( loadControllerMethod( this.getClass() ) );
    }

    /**
     * <p>After all child components are prepared, inject components into @ZkComponent properies and register all @ZkEvents.
     * This is called by ZK framework.</p>
     *
     * <p>Super implementation GenericAutowireComposer sets binds properties and methods by mnemonic. This behaviour is disabled, because
     * it can be performance bottleneck and it can colide with @ZkXX annotations anyway.
     * Only implicit variables from GenericAutowireComposer are used.</p>
     *
     * DLComposer extension here:
     * <ul>
     *   <li>calls bindComponent from GenericComposer (efectively means - skip parent doAfterCompose)</li>
     *   <li>registerZkComponents() - injects components from @ZkComponents annotation</li>
     *   <li>registerZkEvents() - add event to component as to @ZkEvents annotation</li>
     * </ul>
     *
     * @param comp komponenta
     * @throws java.lang.Exception chyba pri spusteni
     */
    @Override
    public void doAfterCompose( Component comp ) throws Exception {

        // it is not desirable to run autowiring. It is slow and can colide with @ZkXX annotations
        // super.doAfterCompose(comp);

        // wire all implicit variables
        wireImplicit();

        // @ZkComponent annotation
        registerZkComponents( this, comp );

        // @ZkEvents annotation
        registerZkEvents( this, comp );
    }

    @Override
    public void bindComponent( Component comp ) {
        // it is not desirable to run autowiring. It is slow and can colide with @ZkXX annotations
        // only for legacy projects - @ZkEvent is used instead.
    }

    /*************************************************  Master / Detail **************************************************************/
    /**
     * <p>Check masterController variable in attributes / arguments / parameterMap.</p>
     *
     * <p>Usage:<br/>
     * &lt;include src="xx" masterController="${ctl}"&gt;<br/>
     * Include child page into master page and set masterController to this variable. This method will connect master/detail automatically.
     * </p>
     * <p>If this property is defined, but not correctly set up - throws an exception.</p>
     */
    protected void setupMasterController() {
        // try to find masterController in attributes (e.g. <include src="xx" masterController="${ctl}">)
        Object ctl = Executions.getCurrent().getAttribute( "masterController" );
        if ( ctl == null ) {
            // try to find masterController in arguments (e.g. <macroComponent masterController="${ctl}">)
            ctl = Executions.getCurrent().getArg().get( "masterController" );
        }
        if ( ctl == null ) {
            // try to find masterController in params (e.g. Executions.createComponents(target,centerPlaceholder,Map("masterController" -> ctl)); )
            ctl = Executions.getCurrent().getParameterMap().get( "masterController" );
        }

        if ( ctl != null ) {
            if ( !(ctl instanceof DLMasterController) ) {
                throw new UiException( "Attribute masterController has to be of type cz.datalite.composer.DLMasterController, got " + ctl );
            }

            setMasterController( ( DLComposer ) ctl );
        }
    }

    /**
     * <p>Convenient method for master to hold model. Implementation will usually override this method to customize model.</p>
     *
     * <p>Child may call this method anytime to get master controller.</p>
     *
     * @return model Default implementation will store model from childe and resend new model to all children.
     */
    public T getMasterControllerModel() {
        return masterControllerModel;
    }

    public void postEvent(String eventName, Object data) {
        Events.postEvent(eventName, self, data);
    }

    /**
     * Sets new value of master controller model.
     *
     * @param masterControllerModel new value
     */
    public void setMasterControllerModel( T masterControllerModel ) {
        this.masterControllerModel = masterControllerModel;
    }

    /**
     * <p>Setup master controller (used by detail).</p>
     *
     * <p>If this class implements DLDetailController, it want to recieve master change events. Method will add this as a child into master controller.
     * Nastaví novou hodnotu pro Master Controller a pokud jsem instanceof DLDetailController, přidá se mu jako detail.</p>
     *
     * <p>Typically this is set in doBeforeCompose from execution.getAttribute("masterController") automatically. Use only in special case./<p>
     *
     * @param masterController new value for master controller
     */
    protected void setMasterController( final DLMasterController masterController ) {
        this.masterController = masterController;

        if ( this instanceof DLDetailController ) {
            masterController.addChildController( ( DLDetailController ) this );

            // and register event on close to automatically remove from master controller
            final DLDetailController thisAsDetailController = ( DLDetailController ) this;
            self.addEventListener( Events.ON_CLOSE, new EventListener() {

                public void onEvent( Event event ) throws Exception {
                    masterController.removeChildController( thisAsDetailController );
                }
            } );

        }
    }

    /**
     * Returns master controller if is set (used by detail).
     * Typically master controller is set in doBeforeCompose from execution.getAttribute("masterController") automatically.
     *
     * @return masterController if is defined, null othervise.
     */
    public DLMasterController<T> getMasterController() {
        return masterController;
    }

    public void addChildController( DLDetailController detailController ) {
        detailControllers.add( detailController );
    }

    public void removeChildController( DLDetailController detailController ) {
        detailControllers.remove( detailController );
    }

    /**
     * Default implementation of master listener automatically calls all registered children (calls onMasterChanged() on each child).
     *
     * @param model new model set by child.
     */
    public void onDetailChanged( T model ) {
        setMasterControllerModel( model );

        // opportunity to modify child model befor resend.
        T newModel = getMasterControllerModel();

        try
        {
            for ( DLDetailController detail : detailControllers ) {
                detail.onMasterChanged( newModel );
            }
        }
        finally {
            // we want to clear flags in all cases, because if a flag triggers an error, we need to
            // clear it to recover from it.
            newModel.clearRefreshFlags();
        }
    }

    /*************************************************  @ZkModel and @ZkController ********************************************************/
    /**
     * Map interface is used to simulate @ZkModel and @ZkController property as it is a real public property of controller class.
     *
     * If no @ZkModel and @ZkController annotaton is used at all, it asumes "old" behaviour - get field value directly.
     *
     * @param key name of property marked with @ZkModel or @ZkController
     * @return value of the property
     */
    public Object get( final Object key ) {
        if ( zkModels.containsKey( ( String ) key ) ) {
            return get( ( String ) key, zkModels );
        } else if ( zkControllers.containsKey( ( String ) key ) ) {
            return get( ( String ) key, zkControllers );
        } else if ( zkModels.isEmpty() && zkControllers.isEmpty() ) {
            return getDefault( ( String ) key );
        } else {
            throw new UiException( new NoSuchFieldException( "Composer \"" + getClass().getName()
                    + "\"doesn't contain @ZkModel or @ZkController property \"" + key + "\"." ) );
        }
    }

    /** Only to implement all Map interface methods. Don't use. */
    public boolean containsKey( final Object key ) {
        // ZK's databinding check, if the key is available in a map.
        // We can skip this here, because get() method covers all cases.
        return true;
    }

    private Object getDefault( final String key ) {
        try {
            return org.zkoss.lang.reflect.Fields.get( this, key );
        } catch ( NoSuchMethodException ex ) {
            LOGGER.error( "Something went wrong.", ex );
            return null;
        }
    }

    private Object get( final String key, final Map<String, Field> fieldMap ) {
        try {
            try {
                return org.zkoss.lang.reflect.Fields.get( this, key );
            } catch ( NoSuchMethodException ex ) {
                final Field field = fieldMap.get( key );
                if ( field != null ) {
                    field.setAccessible( true );
                    final Object value = field.get( this );
                    field.setAccessible( false );
                    return value;
                } else {
                    throw new NoSuchMethodException( "No such getter or field for key \"" + key + "\"." );
                }
            }
        } catch ( Exception ex ) {
            LOGGER.error( "Something went wrong.", ex );
            return null;
        }
    }

    /**
     * Map interface is used to simulate @ZkModel property as it is a real public propert of controller class.
     *
     * If no @ZkModel annotaton is used at all, it asumes "old" behaviour - set field value directly.
     *
     * @param key name of property marked with @ZkModel
     * @param value new value
     */
    public Object put( final String key, final Object value ) {
        if ( zkModels.containsKey( key ) ) {
            put( key, value, zkModels );
        } else if ( zkModels.isEmpty() ) {
            putDefault( key, value );
        } else {
            LOGGER.error( "Something went wrong.", new NoSuchFieldException( "Unknown model for key \"" + key + "\"" ) );
        }
        return value;
    }

    private void putDefault( final String key, final Object value ) {
        try {
            org.zkoss.lang.reflect.Fields.set( this, key, value, true );
        } catch ( NoSuchMethodException ex ) {
            throw Aide.wrap( ex );
        }
    }

    private void put( final String key, final Object value, final Map<String, Field> fieldMap ) {
        try {
            try {
                org.zkoss.lang.reflect.Fields.set( this, key, value, true );
            } catch ( NoSuchMethodException ex ) {
                final Field field = fieldMap.get( key );
                if ( field != null ) {
                    field.setAccessible( true );
                    field.set( this, value );
                    field.setAccessible( false );
                } else {
                    throw new NoSuchMethodException( "No such setter or field for key \"" + key + "\"." );
                }
            }
        } catch ( Exception ex ) {
            throw Aide.wrap( ex );
        }
    }

    /**
     * Traverse all fields and set up parameters according o @ZkParameters annotation.
     */
    protected void setupZkParameters() {
        for ( Field field : ReflectionHelper.getAllFields( this.getClass() ) ) {
            for ( Annotation annotation : field.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkParameter ) {
                    setupZkParameter( ( ZkParameter ) annotation, getName( ( ZkParameter ) annotation, field ), field.getType(), field, null );
                }
            }
        }

        for ( Method method : ReflectionHelper.getAllMethods( this.getClass() ) ) {
            for ( Annotation annotation : method.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkParameter ) {
                    String paramName = ((ZkParameter)annotation).name();
                    if (StringHelper.isNull(paramName))
                    {
                        if ( !method.getName().startsWith( "set" ) ) {
                            throw new InstantiationError( "@ZkParameter must be on method in form of setXXX(ParamType p)  (e.g. setParamName). "
                                    + "Found: " + method.getName() );
                        }
                        paramName = getMethodLowerCase( method.getName().substring( 3 ) );
                    }

                    if ( method.getParameterTypes().length != 1 ) {
                        throw new InstantiationError( "@ZkParameter must be on method in form of setXXX(ParamType p), wrong number of parameters. "
                                + "Actual number of parameters: " + method.getParameterTypes().length );
                    }

                    setupZkParameter( ( ZkParameter ) annotation, paramName, method.getParameterTypes()[0], null, method );
                }
            }
        }

    }

    /**
     * Setup parameter accordnig to ZkParameter annotation.
     *
     * @param <T> type of field.
     * @param annot the annotation with parameter definition
     * @param field the field to set
     * @param clazz class of the field and parameter
     */
    protected <T> void setupZkParameter( ZkParameter annot, String paramName, Class<T> clazz, Field field, Method method ) {
        T result = null;

        Map[] paramMaps = new Map[]{
            Executions.getCurrent().getArg(),
            Executions.getCurrent().getAttributes(),
            Executions.getCurrent().getParameterMap()
        };

        Map paramMap = paramMaps[0];
        for ( Map map : paramMaps ) {
            if ( map.containsKey( paramName ) ) {
                paramMap = map;
                break;
            }
        }

        try {
            if ( annot.required() ) {
                result = ZKHelper.getRequiredParameter( paramMap, paramName, clazz );
            } else if ( !paramMap.containsKey( paramName ) ) {
                return; // optional parameter doesn't do anything
            } else {
                result = ZKHelper.getOptionalParameter( paramMap, paramName, clazz, null );
            }
        } catch ( IllegalArgumentException ex ) {
            throw new IllegalArgumentException( "@ZkParameter(name='" + paramName + "', "
                    + "required=" + (annot.required() ? "true" : "false") + ", "
                    + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                    + "): " + ex.getLocalizedMessage() );
        }

        if ( result == null && annot.createIfNull() ) {
            try {
                result = ( T ) clazz.newInstance();
            } catch ( InstantiationException ex ) {
                throw new InstantiationError( "@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Parameter is null, unable to create new object with error: " + ex.getLocalizedMessage() );
            } catch ( IllegalAccessException ex ) {
                throw new InstantiationError( "@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Parameter is null, unable to create new object, no public default constructor: " + ex.getLocalizedMessage() );
            }
        }

        if ( field != null ) {
            try {
                field.setAccessible( true );
                field.set( this, result );
                field.setAccessible( false );
            } catch ( IllegalArgumentException ex ) {
                throw new IllegalArgumentException( "@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Unable to set new value of field to '" + result + "': " + ex.getLocalizedMessage() );
            } catch ( IllegalAccessException ex ) {
                throw Aide.wrap( ex );
            }
        }
        if ( method != null ) {
            try {
                method.setAccessible( true );
                method.invoke( this, new Object[]{ result } );
                method.setAccessible( false );
            } catch ( IllegalAccessException ex ) {
                throw Aide.wrap( ex );
            } catch ( IllegalArgumentException ex ) {
                throw new IllegalArgumentException( "@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Unable to set new value of method to '" + result + "': " + ex.getClass() + " - " + ex.getLocalizedMessage() );
            } catch ( InvocationTargetException ex ) {
                throw new IllegalArgumentException( "@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Unable to set new value of method to '" + result + "', error in method invocation: "
                        + ex.getClass() + " - " + (ex.getTargetException() == null ? ex.getLocalizedMessage() : ex.getTargetException().getLocalizedMessage()) );
            }
        }
    }

    private static Map<String, Field> loadModelFields( final Class cls ) {
        final Map<String, Field> fields = new java.util.HashMap<String, Field>();
        for ( Field field : ReflectionHelper.getAllFields( cls ) ) {
            for ( Annotation annotation : field.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkModel ) {
                    fields.put( getId( ( ZkModel ) annotation, field ), field );
                    break;
                }
            }
        }
        return fields;
    }

    private static String loadModelClass( final Class cls ) {
        for ( Annotation annotation : cls.getDeclaredAnnotations() ) {
            if ( annotation instanceof ZkModel ) {
                return (( ZkModel ) annotation).name().length() == 0 ? "ctl" : (( ZkModel ) annotation).name();
            }
        }
        return loadControllerClass( cls );
    }

    private static Map<String, Field> loadModelMethod( final Class cls ) {
        final Map<String, Field> fields = new java.util.HashMap<String, Field>();
        for ( Method method : ReflectionHelper.getAllMethods( cls ) ) {
            for ( Annotation annotation : method.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkModel ) {
                    fields.put( getId( ( ZkModel ) annotation, method ), null );
                    break;
                }
            }
        }
        return fields;
    }

    private static Map<String, Field> loadControllerFields( final Class cls ) {
        final Map<String, Field> fields = new java.util.HashMap<String, Field>();
        for ( Field field : ReflectionHelper.getAllFields( cls ) ) {
            for ( Annotation annotation : field.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkController ) {
                    fields.put( getId( ( ZkController ) annotation, field ), field );
                    break;
                }
            }
        }
        return fields;
    }

    private static String loadControllerClass( final Class cls ) {
        for ( Annotation annotation : cls.getDeclaredAnnotations() ) {
            if ( annotation instanceof ZkController ) {
                return (( ZkController ) annotation).name().length() == 0 ? "ctl" : (( ZkController ) annotation).name();
            }
        }
        return "ctl";
    }

    private static Map<String, Field> loadControllerMethod( final Class cls ) {
        final Map<String, Field> fields = new java.util.HashMap<String, Field>();
        for ( Method method : ReflectionHelper.getAllMethods( cls ) ) {
            for ( Annotation annotation : method.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkController ) {
                    fields.put( getId( ( ZkController ) annotation, method ), null );
                    break;
                }
            }
        }
        return fields;
    }

    private static String getId( final ZkModel annotation, final Field field ) {
        return "".equals( annotation.name() ) ? field.getName() : annotation.name();
    }

    private static String getId( final ZkController annotation, final Field field ) {
        return "".equals( annotation.name() ) ? field.getName() : annotation.name();
    }

    private static String getId( final ZkComponent annotation, final Field field ) {
        return "".equals( annotation.id() ) ? field.getName() : annotation.id();
    }

    private static String getName( final ZkParameter annotation, final Field field ) {
        return "".equals( annotation.name() ) ? field.getName() : annotation.name();
    }

    private static String getId( final ZkModel annotation, final Method method ) {
        return "".equals( annotation.name() ) ? getMethodNameWithoutGetSet( method.getName() ) : annotation.name();
    }

    private static String getId( final ZkController annotation, final Method method ) {
        return "".equals( annotation.name() ) ? getMethodNameWithoutGetSet( method.getName() ) : annotation.name();
    }

    private static String getMethodNameWithoutGetSet( final String name ) {
        if ( name.startsWith( "get" ) ) {
            return getMethodLowerCase( name.substring( 3 ) );
        } else if ( name.startsWith( "set" ) ) {
            return getMethodLowerCase( name.substring( 3 ) );
        } else if ( name.startsWith( "is" ) ) {
            return getMethodLowerCase( name.substring( 2 ) );
        } else {
            return name;
        }
    }

    protected static String getMethodLowerCase( final String name ) {
        return name.substring( 0, 1 ).toLowerCase() + name.substring( 1 );
    }

    /**
     * Checks each method for @ZkEvent(s) annotation and registers all events with DLZKEvent class.
     * @param ctl controller
     * @param component master component
     */
    public static void registerZkEvents( final Object ctl, final Component component ) {
        AnnotationProcessor processor = AnnotationProcessor.getProcessor( ctl.getClass() );
        processor.registerZkEventsTo( component, ctl );
    }

    /**
     * Check all fields in ctl object for annotation @ZkComponent. For each property get id from annotation (or property name if not set)
     * and find fellow component of component by this id. If not found or not correct type, throws exception. If everythnig is ok, set
     * the property to new value of the fellow component
     *
     * @param ctl controller with @ZkComponent annotations
     * @param component master component to find fellow components in
     */
    public static void registerZkComponents( final DLComposer ctl, final Component component ) {
        for ( final Field field : ReflectionHelper.getAllFields( ctl.getClass() ) ) {
            for ( Annotation annot : field.getDeclaredAnnotations() ) {
                if ( annot instanceof ZkComponent ) {
                    String id = getId( ( ZkComponent ) annot, field );
                    Component target = component.getFellowIfAny( id );

                    if ( target == null ) {
                        if (((ZkComponent)annot).mandatory()) {
                            throw new IllegalArgumentException("@ZkComponent injection: Unable to inject Component with ID '" + id + "'. "
                                    + "Component not found in idspace of composer self component '" + component.getId() + "'.");
                        }
                    } else {
                        try {
                            field.setAccessible( true );
                            field.set( ctl, target );
                            field.setAccessible( false );
                        } catch ( IllegalArgumentException ex ) {
                            throw new IllegalArgumentException( "@ZkComponent injection: Unable to inject Component with ID '" + id + "'. "
                                    + "Component is of different class: '" + target.getClass().getName() + "'." );
                        } catch ( IllegalAccessException ex ) {
                            throw Aide.wrap( ex );
                        }

                    }
                }
            }
        }
    }

    /**
     * It is possible to add @ZkConfirm or @ZkBinding annotations to any method in composer. However they may be processed only together
     * with @ZkEvent annotation, while in event invocation phase. It is similar to Spring proxy invocation - it handles only calls from outside.
     *
     * It thorws exception if invalid annotation combination is met.
     *
     * @param clazz class to check.
     */
    public static void validMethodAnnotations( final Class clazz ) {
        for ( Method method : ReflectionHelper.getAllMethods( clazz ) ) {
            boolean zkEvent = false;
            boolean zkConfirm = false;
            boolean zkBinding = false;
            for ( Annotation annot : method.getDeclaredAnnotations() ) {
                if ( annot instanceof ZkEvent || annot instanceof ZkEvents ) {
                    zkEvent = true;
                    continue;
                } else if ( annot instanceof ZkConfirm ) {
                    zkConfirm = true;
                    continue;
                } else if ( annot instanceof ZkBinding || annot instanceof ZkBindings ) {
                    zkBinding = true;
                    continue;
                }
            }

            if ( !zkEvent && (zkConfirm || zkBinding) ) {
                throw new IllegalArgumentException( "Unsupported annotation combination on method \"" + method.getName() + "\" @ZkEvent or @Command is required." );
            }
        }
    }
    // unfortunettly, ZK makes everything private and doesn't allow to reuse if from outside.

    private static final Set IMPLICIT_NAMES = new HashSet();

    static {
        IMPLICIT_NAMES.add( "application" );
        IMPLICIT_NAMES.add( "applicationScope" );
        IMPLICIT_NAMES.add( "arg" );
        IMPLICIT_NAMES.add( "componentScope" );
        IMPLICIT_NAMES.add( "desktop" );
        IMPLICIT_NAMES.add( "desktopScope" );
        IMPLICIT_NAMES.add( "execution" );
        IMPLICIT_NAMES.add( "event" ); //since 3.6.1, #bug 2681819: normal page throws exception after installed zkspring
        IMPLICIT_NAMES.add( "self" );
        IMPLICIT_NAMES.add( "session" );
        IMPLICIT_NAMES.add( "sessionScope" );
        IMPLICIT_NAMES.add( "spaceOwner" );
        IMPLICIT_NAMES.add( "spaceScope" );
        IMPLICIT_NAMES.add( "page" );
        IMPLICIT_NAMES.add( "pageScope" );
        IMPLICIT_NAMES.add( "requestScope" );
        IMPLICIT_NAMES.add( "param" );
    }

    /**
     * We want to maintain implecit objects like GenericAutowireComposer does, however it is not possible to reuse part of Components class
     * while it is private. This code uses Components.getImplicit() method.
     */
    protected void wireImplicit() {
        for ( final Iterator it = IMPLICIT_NAMES.iterator(); it.hasNext(); ) {
            final String fdname = ( String ) it.next();
            //we cannot inject event proxy because it is not an Interface
            if ( "event".equals( fdname ) ) {
                continue;
            }
            final Object argVal = Components.getImplicit( self, fdname );
            try {
                final Field field = Classes.getAnyField( this.getClass(), fdname );
                field.setAccessible( true );
                field.set( this, argVal );
                field.setAccessible( false );
            } catch ( IllegalArgumentException ex ) {
                throw Aide.wrap( ex );
            } catch ( IllegalAccessException ex ) {
                throw Aide.wrap( ex );
            } catch ( NoSuchFieldException ex ) {
                throw Aide.wrap( ex );
            }
        }
    }

    /** Only to implement all Map interface methods. Don't use. */
    public Object remove( final Object key ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /** Only to implement all Map interface methods. Don't use. */
    public void putAll( final java.util.Map<? extends String, ? extends Object> m ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /** Only to implement all Map interface methods. Don't use. */
    public void clear() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /** Only to implement all Map interface methods. Don't use. */
    public java.util.Set<String> keySet() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /** Only to implement all Map interface methods. Don't use. */
    public Collection<Object> values() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /** Only to implement all Map interface methods. Don't use. */
    public java.util.Set<java.util.Map.Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /** Only to implement all Map interface methods. Don't use. */
    public int size() {
        //throw new UnsupportedOperationException( "Not supported yet." );
        // unsupport exception causes problems while debuging in netbeans, which tries to show in variables window this as Map()
        return 0;
    }

    /** Only to implement all Map interface methods. Don't use. */
    public boolean isEmpty() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /** Only to implement all Map interface methods. Don't use. */
    public boolean containsValue( final Object value ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
