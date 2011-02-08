package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Mirror of Combobox
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Combobox extends InputElement {

    public Combobox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    @Override
    public void autoFill() {
        selectValue( 1 );
    }

    @Override
    public void clear() {
        webElement.findElement( By.tagName( "input" ) ).clear();
    }

    @Override
    public void write( final String value ) {
        webElement.findElement( By.tagName( "input" ) ).sendKeys( value );
    }

    @Override
    public String getValue() {
        return webElement.findElement( By.tagName( "input" ) ).getText();
    }

    /**
     * Selects item on this index.
     * @param index item index starts from 1
     */
    public void selectValue( final int index ) {
        final WebElement popup = getPopup();
        final WebElement btn = webElement.findElement( By.className( "z-combobox-btn" ) );
        // není znám počet kliknutí na která to zareaguje, občas 1, někdy 3 ale většinou 2
        // proto se kliká dokud je komponenta neviditelná
        int i = 0;
        while ( popup.getAttribute( "style" ).contains( "display: none" ) ) {
            if ( i++ >= 5 ) {
                throw new IllegalStateException( "Combobox couldn't be opened." );
            }
            btn.click();
            zkDriver.waitForProcessing();
        }
        final WebElement item = popup.findElements( By.tagName( "tr" ) ).get( index - 1 );
        item.click();
    }

    private WebElement getPopup() {
        final String id = webElement.getAttribute( "id" );
        return ZkElement.findElement( By.id( id + "-pp" ) );
    }
}
