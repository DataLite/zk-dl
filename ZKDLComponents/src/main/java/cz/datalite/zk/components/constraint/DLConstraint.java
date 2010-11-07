package cz.datalite.zk.components.constraint;

import cz.datalite.zk.components.combo.DLCombobox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.SimpleConstraint;

/**
 * Extended ZK constraint.
 *
 * List of extensions:
 * <ul>
 *   <li>Disable constraint on readonly combobox.</li>
 * </ul>
 *
 * @author Jiri Bubnik
 */
public class DLConstraint extends SimpleConstraint
{
	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 * @since 3.0.2
	 */
    public DLConstraint(String constraint)
    {
        super(constraint);
    }

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public DLConstraint(int flags, String regex, String errmsg)
    {
        super(flags, regex, errmsg);
    }

	/** Constructs a constraint with flags.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 */
	public DLConstraint(int flags) {
		this(flags, null, null);
	}
	/** Constructs a constraint with flags and an error message.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public DLConstraint(int flags, String errmsg) {
		this(flags, null, errmsg);
	}
	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public DLConstraint(String regex, String errmsg) {
		this(0, regex, errmsg);
	}

     /**
     * Vlastni zpracovani constraintu
     *
     *
     * @param comp ktera komponenta
     * @param value nastavovana hodnota
     *
     * @throws org.zkoss.zk.ui.WrongValueException
     */
    @Override
	public void validate(Component comp, Object value) throws WrongValueException {

        if (comp instanceof DLCombobox)
        {
            // Disable constraint validation for readonly combobox
            if (((DLCombobox)comp).isReadonly())
            {
                return;
            }
//            JB - don't know the reason behind this
//            if ((_flags & NO_EMPTY) != 0)
//            {
//                return;
//            }
        }

        super.validate(comp, value);
    }

	/** Parses a list of constraints from a string to an integer
	 * representing a combination of {@link #NO_POSITIVE} and other flags.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 */
	public static final DLConstraint getDLConstraint(String constraint) {
		return new DLConstraint(constraint);
	}    
}
