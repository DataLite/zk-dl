package cz.datalite.webdriver;

import cz.datalite.webdriver.components.ZkElement;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory which creating elements found in HTML
 * according to the style of element.
 *
 *
 * @author Karel Cemus
 */
public class ElementFactory {

    public static <T extends ZkElement> T create( final WebElement element, final ZkElement contextElement ) {
        final String cssClass = element.getAttribute( "class" );
        if ( cssClass != null ) {
            final Set<String> classes = new HashSet<>(Arrays.asList(cssClass.split(" ")));
            if ( !classes.isEmpty() ) {
                for ( ZkComponents component : ZkComponents.values() ) {
                    if ( classes.contains( component.getStyleClass() ) ) {
                        return ( T ) component.create( contextElement, element );
                    }
                }
            }
        }
        return ( T ) new ZkElement( contextElement, element );
    }
}
