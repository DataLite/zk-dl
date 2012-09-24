package cz.datalite.webdriver;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.internal.FindsByXPath;

import cz.datalite.webdriver.components.ZkElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.internal.FindsById;

/**
 * Factory class for creating seach criteria
 *
 * @author Karel Cemus
 */
public abstract class By extends org.openqa.selenium.By {

    protected final boolean lookForChild;

    public By() {
        super();
        lookForChild = false;
    }

    public By( final boolean lookForChild ) {
        super();
        this.lookForChild = lookForChild;
    }

    public boolean isLookForChild() {
        return lookForChild;
    }

    /**
     * Allows to find in form by label of row. This is based on HTML structure,
     * it can break with new version of ZK framework. Using of this search style
     * requires exact simple structure of form implementation.
     *
     * @param label label in first column in row
     * @return By
     */
    public static org.openqa.selenium.By label( final String label ) {
        return new By( true ) {

            @Override
            public List<WebElement> findElements( final SearchContext context ) {
                return (( FindsByXPath ) context).findElementsByXPath( ".//*[contains(upper-case(text()),upper-case('" + label + "'))]/ancestor::td[1]/following-sibling::td[1]" );
            }

            @Override
            public WebElement findElement( final SearchContext context ) {
//                return (( FindsByXPath ) context).findElementByXPath( ".//*[contains(upper-case(text()),upper-case('" + label + "'))]/ancestor::td[1]/following-sibling::td[1]" );
                return (( FindsByXPath ) context).findElementByXPath( ".//*[contains(text(),'" + label + "')]/ancestor::td[1]/following-sibling::td[1]" );
            }

            @Override
            public String toString() {
                return "By.label: " + label;
            }
        };
    }

    /**
     * Converts serverId to Uuid using back-doors to server. The Id is found
     * and translated to Uuid and then the component is found on the page.
     *
     * @param id component's server identifier
     * @return By
     */
    public static org.openqa.selenium.By serverId( final String id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "Identifier cannot be null." );
        }

        return new org.openqa.selenium.By() {

            @Override
            public List<WebElement> findElements( final SearchContext context ) {
                throw new UnsupportedOperationException( "Find list of elements by serverId has not been implemented yet." );
            }

            @Override
            public WebElement findElement( final SearchContext context ) {
                if ( context instanceof WebElement ) {
                    return find( getUuid( ( WebElement ) context ), context );
                } else {
                    return find( getUuid(), context );
                }

            }

            @Override
            public String toString() {
                return "By.serverId: " + id;
            }

            protected String getUuid( final WebElement parent ) {
                final String request = parent.getAttribute( "id" ) + "," + id;
                final String uuid = ZkElement.getZkDriver().sendCommand( "idToUuidInContext", request );
                if ( uuid == null || uuid.length() == 0 ) {
                    throw new NoSuchElementException( "Component with id " + id + " was not found." );
                }
                return uuid;
            }

            protected String getUuid() {
                final String uuid = ZkElement.getZkDriver().sendCommand( "idToUuid", "/" + id );
                if ( uuid == null || uuid.length() == 0 ) {
                    throw new NoSuchElementException( "Component with id " + id + " was not found." );
                }
                return uuid;
            }

            protected WebElement find( final String id, final SearchContext context ) {
                if ( context instanceof FindsById ) {
                    return (( FindsById ) context).findElementById( id );
                }
                return (( FindsByXPath ) context).findElementByXPath( "*[@id = '" + id + "']" );
            }
        };
    }
}
