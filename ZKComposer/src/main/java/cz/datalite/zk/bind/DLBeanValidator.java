package cz.datalite.zk.bind;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.impl.PropertyImpl;
import org.zkoss.bind.validator.BeanValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

/**
 * Extension for ZK BeanValidator adding some fixes and enhancement to allow UI
 * validation based on model validation constraints.
 *
 * @author Karel Cemus
 */
public class DLBeanValidator extends BeanValidator {

    @Override
    public void validate( ValidationContext ctx ) {
        // osetreni "" misto null 
        // translate empty String to null - ZK sends always empty string instead of null for field and @NotNull doesn't work 
        Object validateValue = ctx.getProperty().getValue();
        if ( validateValue != null && validateValue instanceof String && validateValue.toString().trim().length() == 0 )
            (( PropertyImpl ) ctx.getProperty()).setValue( null );

        try {
            super.validate( ctx );
        } catch( IllegalArgumentException ignored ) {
            // drop argument exception because it tries to validate 
            // everything, event "virtual" fields of maps etc.
        }

        final Component component = ctx.getBindContext().getComponent();
        // automaticke nastvani chyby na komponentu 
        if ( ctx.isValid() )
            Clients.clearWrongValue( component );
    }

    @Override
    protected void addInvalidMessages( ValidationContext ctx, String key, String[] messages ) {
        super.addInvalidMessages( ctx, key, messages );
        if ( messages.length > 0 )
            Clients.wrongValue( ctx.getBindContext().getComponent(), messages[0] );
    }
}
