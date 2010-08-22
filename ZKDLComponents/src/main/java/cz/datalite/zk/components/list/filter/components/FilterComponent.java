package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;

/**
 *
 * This interface defines methods for filter processor
 * which defines operators, value component and validation method. This component
 * can be defined specifically for each column in the ZUL files so every column
 * can have a special component and validation method.  For a usage is recommended
 *{@link cz.datalite.zk.components.list.filter.components.AbstractFilterComponent}
 * which offers few implemented methods so there needn't be everything redefined.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface FilterComponent {

    /**
     * This method returns existing instance of the component which is used to
     * set up the filter value in the normal filter form. 
     * @return component for the value
     */
    Component getComponent();

    /**
     * This method returns value setted up in the component.
     * @return setted value
     */
    Object getValue();

    /**
     * There are setted new value to the component. This method is called
     * on OnChange event.
     * @param value value to be setted
     */
    void setValue( final Object value );

    /**
     * There have to be registered Event Listener on the event, which is emitted
     * when the value of the component is change. In the major part of cases
     * it is "OnChange" event but not always.
     * @param listener listener to be registred
     */
    void addOnChangeEventListener( EventListener listener );

    /**
     * This method validates the value of the component. If the value is valid
     * then nothing happends else the exception is thrown. This exception is
     * processed as a validation exception which is shown as a small hint using
     * a javascript. To catch the exception as a small hint there <b>have to be
     * defined a source component</b> elsewhere it is processed as a regular
     * exception.
     * @throws WrongValueException validation exception.
     */
    void validate() throws WrongValueException;
}
