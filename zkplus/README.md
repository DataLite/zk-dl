# Patch
## AnnotateDataBinderInit.java
Added

    	private ValidatorAdapter _validator;
...

		Object loadOnSave = args.get("loadOnSave");
		if (loadOnSave == null) {
			_loadOnSave = false; // DTL JB - loadOnSave set default to false
		} else if (loadOnSave instanceof String) {
			_loadOnSave = !"false".equals(loadOnSave);
		} else if (loadOnSave instanceof Boolean) {
			_loadOnSave = ((Boolean)loadOnSave).booleanValue();
		}

        // DTL JB - bean validation
        Object validator = args.get("validator");
        if (validator != null)
        {
            _validator = new ValidatorAdapter(validator);
        }
	}
	
...
	
			_binder.setLoadOnSave(_loadOnSave);

        // DTL JB - bean validation
        _binder.setValidator(_validator);

		_binder.loadAll(); //load data bean properties into UI components
		
		
## Binding.java
In

	/*package*/ void setConverter(String cvtClsName) {
added

            } else {
                try {
                    cls = _comp.getPage().resolveClass(cvtClsName);
                } catch (ClassNotFoundException ex) {
                    if ( cvtClsName.contains( "." ) ) {
                        // DTL JB added converter method in existing bean
                        _converter = new MethodTypeConverter( cvtClsName, _binder,
                                "Coerce databinding '" + _expression + "' with expression '" + cvtClsName + "': " );
                        return;
                    } else
                        throw UiException.Aide.wrap( new ClassNotFoundException( cvtClsName ) );

                }
            }
			
			
## DataBinder.java
Added

    /** DTL JB Bean validation */
    protected ValidatorAdapter _validator;

    /**
     * Bean validation validator.
     * @return Bean validation validator
     * @author DTL JB
     */
    public ValidatorAdapter getValidator()
    {
        return _validator;
    }

    /**
     * Bean validation validator.
     * @param _validator Bean validation validator.
     * @author DTL JB
     */
    public void setValidator(ValidatorAdapter _validator)
    {
        this._validator = _validator;
    }


    // DTL JB - loadOnSave set default to false
    private boolean _loadOnSave = false; //whether firing the onLoadOnSave event to automate the loadOnSave operation
...
In

    protected Object[] loadPropertyAnnotation(Component comp, String propName, String bindName) {

added

	// HACK JB - s vice rozsirenimi tam je vice stejnym hodnot duplicitne - odstraneni duplicity
	Set<String> tagvalSet = new HashSet();
	for (String t : tagval)
		tagvalSet.add(t);
					
...
In

    public void setupTemplateComponent(Component comp, Object owner) {

added

    try {
                    orgVal = Fields.get(bean, beanid);

                    // DTL JB - bean validation - check if value is null or if changed
                    boolean sameValue = Objects.equals(orgVal, val);
                    if (val == null || !sameValue)
                    {
                        ValidatorAdapter validator = getValidator();
                        if (validator != null)
                            validator.validate(comp, bean, beanid, val);
                    }

                    if(sameValue) {
                        return; //same value, no need to do anything
                    }

                    Fields.set(bean, beanid, val, autoConvert);
                } catch (NoSuchMethodException ex) {
                    throw UiException.Aide.wrap(ex);
                }
                
## MethodTypeConverter

    package org.zkoss.zkplus.databind;
    
    import org.zkoss.lang.Classes;
    import org.zkoss.zk.ui.Component;
    import org.zkoss.zk.ui.UiException;
    
    import java.io.PrintWriter;
    import java.io.StringWriter;
    import java.lang.reflect.InvocationTargetException;
    import java.lang.reflect.Method;
    
    /**
     *  Convenient converter, which is called directly from Binding.
     *
     * @author Jiri Bubnik
     */
    public class MethodTypeConverter implements TypeConverter
    {
        // Converter definition (like "ctl.myConverter")
        private String converter;
        // databinder
        private DataBinder binder;
        // error description
        private String errorDesc;
    
        private String controller;
        private String method;
    
        private Method coerceMethod = null;
        private Object controllerObj = null;
    
        public MethodTypeConverter(String converter, DataBinder binder, String errorDesc)
        {
            this.binder = binder;
            this.errorDesc = errorDesc;
            this.converter = converter;
    
            controller = (String) DataBinder.parseExpression(this.converter, ".").get(0);
            method = (String) DataBinder.parseExpression(this.converter, ".").get(1);
        }
    
    
        /**
         * Find the coerce method in composer
         * @param comp component for the context
         * @param valueClass to which class it should be converted
         * @return
         */
        public Method getCoerceMethod(Component comp, Class valueClass)
        {
            if (coerceMethod == null) {
                // controller bean must be in variables
                controllerObj = binder.lookupBean(comp, controller);
                if (controllerObj == null) {
                    throw new UiException(errorDesc + "Unable to find bean '" + controller + "'.");
                }
    
                // load method with only value param or component + value param
                try {
                    coerceMethod = Classes.getCloseMethod(controllerObj.getClass(), method, new Class[]{valueClass});
                } catch (NoSuchMethodException ex) {
                }
    
    
                if (coerceMethod == null) {
                    try {
                        coerceMethod = Classes.getCloseMethod(controllerObj.getClass(), method, new Class[]{valueClass, comp.getClass()});
                    } catch (NoSuchMethodException ex) {
                    }
                }
    
                if (coerceMethod == null) {
                    throw new UiException(errorDesc
                            + " Method '" + method + "' not found in class '" + controllerObj.getClass() + "'. Params: "
                            + valueClass + ", " + comp.getClass() + " (optional)");
                }
    
            }
            return coerceMethod;
        }
    
        public Object coerceToUi(Object val, Component comp)
        {
            try {
                Method m = getCoerceMethod(comp, val == null ? null : val.getClass());
                if (m.getGenericParameterTypes().length == 1) {
                    return m.invoke(controllerObj, val);
                } else {
                    return m.invoke(controllerObj, val, comp);
                }
            } catch (IllegalAccessException ex) {
                throw new UiException(errorDesc + " Illegal access: " + ex.getLocalizedMessage(), ex);
            } catch (IllegalArgumentException ex) {
                throw new UiException(errorDesc + " Illegal argument: " + ex.getLocalizedMessage(), ex);
            } catch (InvocationTargetException ex) {
                throw new UiException(errorDesc + " Error while invoking method: " + ex.getLocalizedMessage()
                        + "\n" + getStackTrace(ex), ex);
            } catch (Throwable ex) {
                throw new UiException(errorDesc + " Unexpected exception: " + ex.getLocalizedMessage()
                        + "\n" + getStackTrace(ex), ex);
            }
        }
    
        /**
         * Coerce to bean is not supported by this converter
         *
         * @param val the object to be corece to backend bean property type.
         * @param comp associated component
         * @return always the val (same as input)
         */
        public Object coerceToBean(Object val, Component comp)
        {
            return val;
        }
    
        public String getStackTrace(Throwable throwable) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }
    }

## Validator Adapter
    package org.zkoss.zkplus.databind;
    
    import java.util.LinkedList;
    import java.util.List;
    import java.util.Set;
    import javax.validation.ConstraintViolation;
    import javax.validation.Validator;
    import org.zkoss.zk.ui.Component;
    import org.zkoss.zk.ui.UiException;
    import org.zkoss.zk.ui.WrongValueException;
    import org.zkoss.zk.ui.WrongValuesException;
    
    /**
     *
     *
     * @author DTL Jiri Bubnik
     */
    public class ValidatorAdapter {
    
        /** The actual validator */
        Validator validator;
    
        /**
         * Create the adapter for actual validator.
         *
         * @param validator the validator. Type of object, because the dependency javax.validation.Validator should be only in this class (not in binding).
         */
        public ValidatorAdapter(Object validator)
        {
            if (!(validator instanceof Validator))
                throw new UiException("Validator has to be of type javax.validation.Validator: "+ validator);
    
            this.validator = (Validator) validator;
        }
    
    
        /**
         * Validate component comp - bind the validateValue to property beanid of object bean.
         * 
         * @param comp UI component with new value
         * @param bean bean to set value
         * @param beanid property of the bean
         * @param validateValue new value (to validate)
         */
        public void validate(Component comp, Object bean, String beanid, Object validateValue)
        {
            if (!validator.getConstraintsForClass(bean.getClass()).isBeanConstrained())
                return;
    
            // translate empty String to null - ZK sends always empty string instead of null for field and @NotNull doesn't work
            if (validateValue != null && validateValue instanceof String && ((String)validateValue).length() == 0)
            {
                validateValue = null;
            }
    
            Set constraintViolations = validator.validateValue(bean.getClass(), beanid, validateValue);
    
            if (constraintViolations.size() > 0)
            {
                List<WrongValueException> exceptions = new LinkedList();
                for (ConstraintViolation violation : (Set<ConstraintViolation>)constraintViolations)
                {
                    exceptions.add(new WrongValueException(comp, violation.getMessage()));
                }
                throw new WrongValuesException(exceptions.toArray(new WrongValueException[] {}));
            }
        }
    }


## Mavem pom.xml
Added dependency to

		<dependency>
			<groupId>javax.validation</groupId>
			<artifactId>validation-api</artifactId>
			<version>1.0.0.GA</version>
			<type>jar</type>
			<scope>compile</scope>
			<optional>true</optional>
		</dependency>