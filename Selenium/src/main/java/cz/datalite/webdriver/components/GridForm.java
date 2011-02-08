package cz.datalite.webdriver.components;

import cz.datalite.webdriver.By;
import cz.datalite.webdriver.ZkComponents;
import cz.datalite.webdriver.ZkDriver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.NoSuchElementException;

/**
 *
 * GridForm is class which offers simple work with window which contains form
 * styled in grid. There are methods which can detect and fill these components
 *
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class GridForm extends Grid {

    /** Window with the form. */
    protected Window window;
    /** Map with detected elements. As keys are used labels */
    protected final Map<String, InputElement> elements;
    /** Map of values to be manually set. As keys are used labels */
    protected final Map<String, String> values;

    public GridForm( final Window window ) {
        super( window, findElement( ZkComponents.GRID.getBy(), window ) );
        elements = new HashMap<String, InputElement>( 30 );
        values = new HashMap<String, String>( 30 );
        this.window = window;
    }

    /**
     * Finds button with this label and tries to click on it
     * If the button is not found or is not visible then
     * the exception is thrown.
     *
     * @param label button label
     */
    public void submit( final String label ) {
        submit( By.label( label ) );
    }

    /**
     * Finds button by the given criteria. If the
     * button is found then the click event is sent to it.
     * If the button is not
     * found or is nto visible then the exception is thrown.
     *
     * @param by search criterion
     */
    public void submit( final org.openqa.selenium.By by ) {
        final Button submit = window.findButton( by );
        submit.click();
    }

    /**
     * <p>Detect all components on this form. It means that the form tries
     * to found all form components according to the style class. If the
     * components are found they have to be visible or they are skipped.</p>
     *
     * <p>The main usage of this function is to fill the form. If some component
     * shouldn't be filled or wasn't found then the programmer still has an
     * option to find it and fill it manually.</p>
     *
     * <p>For each row can be detected only one component. More complex
     * forms are intestable with this class.</p>
     *
     */
    public void detectAll() {
        for ( ZkComponents component : ZkComponents.values() ) {
            if ( !component.isFormElement() ) {
                continue;
            }
            final List<InputElement> found = window.findAll( component.getBy() );
            for ( ZkElement zkElement : found ) {
                try {
                    if ( !zkElement.isVisible() ) { // element is not probably shown
                        ZkDriver.getLogger().debug( "Ignored hidden element " + component.getStyleClass() );
                        continue;
                    }
                    final String label = findLabelFor( zkElement );
                    if ( label == null || label.length() == 0 ) {
                        throw new NoSuchElementException( "Label for element wasn't found." );
                    }
                    ZkDriver.getLogger().debug( "Detected " + component.getStyleClass() + " with label " + label );
                    if ( zkElement instanceof InputElement ) {
                        elements.put( label, ( InputElement ) zkElement );
                    } else {
                        ZkDriver.getLogger().error( "Element " + zkElement.getClass() + " doesn't implement InputElement interface." );
                    }
                } catch ( NoSuchElementException ex ) {
                    ZkDriver.getLogger().error( "Element with style " + component.getStyleClass() + " was ignored. Label wasn't found." );
                }
            }
        }
    }

    /**
     * Returns label for given component.
     *
     * @param element component in form whose label should be find
     * @return found label or exception
     */
    public static String findLabelFor( final ZkElement element ) {
        return findElement( By.xpath( "./ancestor::td/preceding-sibling::td[1]/div/span" ), element ).getText();
    }

    /**
     * To the component with this label is set this value. It means that
     * on this component won't be called autofill but will be called write
     * with this parameter
     * @param label component's label
     * @param value value to be filled
     */
    public void setValue( final String label, final String value ) {
        if ( !elements.containsKey( label ) ) {
            ZkDriver.getLogger().error( "Label '" + label + "' was not found in the form. Your value won't be used." );
        }
        values.put( label, value );
    }

    /**
     * Removes component from the form - it causes that on this component
     * won't be called any fill method
     * @param label label of the component
     */
    public void removeComponent( final String label ) {
        elements.remove( label );
    }

    /**
     * Fills all detected component with auto or parametrized fill.
     */
    public void fill() {
        for ( String label : elements.keySet() ) {
            ZkDriver.getLogger().debug( "Filling '" + label + "'." );
            final InputElement element = elements.get( label );
            if ( values.containsKey( label ) ) {
                element.write( values.get( label ) );
            } else {
                element.autoFill();
            }
        }
    }
}
