package cz.datalite.zk.components.check;

import org.zkoss.zul.Checkbox;

/**
 * ZK Checkbox extensions - adds value behaviour to the checkbox.
 *
 * You can set what the checkbox checked and uchecked values are and when you call setValue() on checkbox, its
 * state is modified according if the value is equalls as checked/unchecked.
 *
 * List of extensions:
 * <ul>
 *   <li>Value - checked and unchecked value. Convinient for database opperations</li>
 *   <li>Readonly - default implementation is to disable checkbox</li>
 * </ul>
 *
 * @author Jiri Bubnik
 */
public class DLCheckbox extends Checkbox
{

    /**
     * Default constructor
     */
    public DLCheckbox() {
    }
    
    /**
     * Utility constructor to setup default checked/unchecked values
     * @param checkedValue the checked value
     * @param uncheckedValue the unchecked value
     */
    public DLCheckbox(Object checkedValue, Object uncheckedValue)
    {
        setCheckedValue(checkedValue);
        setUncheckedValue(uncheckedValue);
    }

    /*
     * Checkbox value, if it is checked.
     */
    private Object checkedValue = true;

    /**
     * Checkbox value, if it is unchecked 
     */
    private Object uncheckedValue = false ;

    /**
     * If neither checked or unchecked is matched, should the checkbox be checked?
     */
    private boolean otherUnchecked = false;

    /**
     * Modifies the state of checkbox. It compares value to checkedValue and uncheckedValue and calls setChecked of checkbox accordingly.
     *
     * @param value the value
     */
    public void setObjectValue(Object value)
    {
        if (value == null)
        {
            if (getCheckedValue() == null)
            {
                setChecked( true ) ;
            }
            else if (getUncheckedValue() == null)
            {
                setChecked( false ) ;
            }
            else
            {
                setChecked( isOtherUnchecked() ) ;
            }
        }
        else if (value instanceof Boolean)
        {
            setChecked((Boolean)value);
        }
        else if (value.equals(getCheckedValue()))
        {
            setChecked( true );
        }
        else if (value instanceof Character && getCheckedValue() instanceof String && ((String)getCheckedValue()).length() > 0 &&
                ((Character)value) ==((String)getCheckedValue()).charAt(0))
        {
            // pokud porovnavam char se Stringem, tak se musi shodovat prvni znak ze Stringu
            setChecked( true );
        }
        else if (value.equals(getUncheckedValue()))
        {
            setChecked(false);
        }
        else if (value instanceof Character && getUncheckedValue() instanceof String && ((String)getUncheckedValue()).length() > 0 &&
                ((Character)value) ==((String)getUncheckedValue()).charAt(0))
        {
            // pokud porovnavam char se Stringem, tak se musi shodovat prvni znak ze Stringu
            setChecked(false);
        }
        else
        {
            setChecked(isOtherUnchecked());
        }
        
    }

    /**
     * If the checkbox is checked, returns checkedValue otherwise unchecked value.
     *
     * @return checkedValue or unchecked value.
     */
    public Object getObjectValue()
    {
        return ( isChecked() ) ? getCheckedValue() : getUncheckedValue() ;
    }

    /**
     * Sets readonly
     *
     * @param readonly
     */
    public void setReadonly(boolean readonly)
    {
        setDisabled(readonly);
    }

    /**
     * Returns if the component is readonly
     *
     * @return true if component is readonly
     */
    public boolean isReadonly()
    {
        return isDisabled();
    }

    /**
     * Checkbox value, if it is checked.
     *
     * @return the checkedValue
     */
    public Object getCheckedValue() {
        return checkedValue;
    }

    /**
     * Checkbox value, if it is checked.
     *
     * @param checkedValue the checkedValue to set
     */
    public void setCheckedValue(Object checkedValue) {
        this.checkedValue = checkedValue;
    }

    /**
     * Checkbox value, if it is unchecked.
     *
     * @return the uncheckedValue
     */
    public Object getUncheckedValue() {
        return uncheckedValue;
    }

    /**
     * Checkbox value, if it is unchecked.
     *
     * @param uncheckedValue the uncheckedValue to set
     */
    public void setUncheckedValue(Object uncheckedValue) {
        this.uncheckedValue = uncheckedValue;
    }

    /**
     * If neither checked or unchecked is matched, should the checkbox be checked?
     *
     * @return the otherUnchecked
     */
    public boolean isOtherUnchecked() {
        return otherUnchecked;
    }

    /**
     * If neither checked or unchecked is matched, should the checkbox be checked?
     *
     * @param otherUnchecked the otherUnchecked to set
     */
    public void setOtherUnchecked(boolean otherUnchecked) {
        this.otherUnchecked = otherUnchecked;
    }

}
