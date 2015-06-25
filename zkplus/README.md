# Patch
## AnnotateDataBinderInit.java
Added

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