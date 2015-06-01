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
