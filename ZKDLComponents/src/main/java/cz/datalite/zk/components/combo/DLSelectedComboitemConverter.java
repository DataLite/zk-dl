package cz.datalite.zk.components.combo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.SelectedComboitemConverter;
import org.zkoss.zkplus.databind.TypeConverter;

/**
 * Convertor is used when databinding is saving/loading selected
 * item from component. Because this component allows to set selected
 * item which isn't in the combobox model. There is added
 * the option to add that item and refresh list model.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLSelectedComboitemConverter extends SelectedComboitemConverter implements TypeConverter, java.io.Serializable {

    private static final long serialVersionUID = 20090908;

    @Override
    @SuppressWarnings( "unchecked" )
    public Object coerceToUi( final Object val, final Component comp ) {
        // items haven't been rendered yet
        if ( comp.getAttribute( "zul.Combobox.ON_INITRENDER" ) != null ) {
            return TypeConverter.IGNORE;
        }

        if ( comp instanceof DLCombobox ) { // if it is instance of out modified class
            final DLCombobox combobox = ( DLCombobox ) comp;

            // no model yet, ignore
            if ( combobox.getController().getModel() == null ) {
                return TypeConverter.IGNORE;
            }

            if ( val == null || combobox.getController().isInModel( val ) ) // if item is in the model
            {
                return super.coerceToUi( val, comp ); // set it - do default implementation
            } else {
                combobox.getController().add( val ); // else add it into the model and return ignore
                return TypeConverter.IGNORE;
            }
        } // else do default implementation
        else {
            return super.coerceToUi( val, comp );
        }
    }
    /** specific attribute which servers to direct input the special bean */
    public static final String ON_CASCADE_REFRESH = "cz.datalite.databind.onCascadeRefresh";
    /** dummy value to detect when there should be null  */
    public static final Object DUMMY = new Object();

    /**
     * The ZK convertor was exteded to be able to set the specific value into
     * the bean. It is done through attribute in the component. This is used
     * when there is cascade change and due to performacne optimization
     * there is better to store the bean as soon as possible. Otherwise the
     * model has to be rerendered third times and still there is mixed calling
     * of events.
     * @param val
     * @param component
     * @return bean value
     */
    @Override
    public Object coerceToBean( final Object val, final Component component ) {
        if ( component.hasAttribute( ON_CASCADE_REFRESH, false ) ) {
            final Object value = component.getAttribute( ON_CASCADE_REFRESH );
            component.removeAttribute( ON_CASCADE_REFRESH );
            return value == DUMMY ? null : value;
        } else {
            return super.coerceToBean( val, component );
        }
    }
}
