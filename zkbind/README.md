# Patched org.zkoss.zk:zkbind version.

## BinderImpl.java
### Added:
        /** Method allowing to load a specific binding */
        protected void loadBinding( Component component, BindingKey binding ) {
            try {
                _propertyBindingHandler.doLoad( component, binding );
            } catch ( PropertyNotFoundException ignored ) {
                // binding is not appliable, drop it
            }
        }

        /** Method to save any binding on the component */
        protected boolean saveBinding( Component component, BindingKey binding ) {
            try {
                return _propertyBindingHandler.doSaveEvent( binding, component, new Event( "ON_SAVE" ), new LinkedHashSet<Property>() );
            } catch ( PropertyNotFoundException ignored ) {
                // binding is not appliable, drop it
                return true;
            }
        }
        
        /** Saves all bindings bound to the component */
        protected boolean saveAllBindings( Component component ) {
            // validation flag
            boolean success = true;

            // get all bindings
            Set<Entry<BindingKey, List<SavePropertyBinding>>> allBindings = _propertyBindingHandler.getSaveEventBindings().entrySet();

            for ( final Entry<BindingKey, List<SavePropertyBinding>> bindings : allBindings ) {

                // nothing to save, drop it
                if ( bindings.getValue().isEmpty() ) continue;

                // binding component, the key is bound to
                Component bindTo = bindings.getValue().get( 0 ).getComponent();

                // test whether the component is a child of given component. If not, skip
                if ( !isAncestorOf( component, bindTo ) ) continue;

                // save binding
                success &= saveBinding( bindTo, bindings.getKey() );
            }

            // return whether or not all bindings was successfully saved
            return success;
        }
        
        /** determines whether the component is the ancestor of the given one */
        protected boolean isAncestorOf( Component ancestor, Component descendant ) {
        Component parent = descendant;
        // while has parent
        while ( parent != null ) {
            // test if the parent is the ancestor
            if ( ancestor.equals( parent ) ) return true;
            // move up
            parent = parent.getParent();
        }
        // component is not the descendant of the given potential ancestor
        return false;
    }

    /**
     * Make the method accessible for DLBinder.
     */
    protected Method getCommandMethod(Class<?> clz, String command)
    {
        return getCommandMethod(clz, command, _commandMethodInfoProvider, _commandMethodCache);
    }

    /**
     * Make the bindings accessible for DLBinder.
     */
    protected Map<Component, Map<String, List<Binding>>> getBindings()
    {
        return _bindings;
    }

### Modified:
method ```doExecute()``` was changed from ```private``` to ```protected```.

## PropertyBindingHandler.java
"   dlBeanValidator je kvůli nějakým specialitám pro BeanValidation"

![modified code](https://raw.githubusercontent.com/DataLite/zk-dl/1.5.X/zkbind/doc/propertyBindingHandler.png "Modified code")

## PropertyImpl.java
### Modified:
```private final Object _value``` removed ```final```.

### Added:
```public void setValue(Object value)```
