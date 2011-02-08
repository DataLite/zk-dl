package cz.datalite.webdriver.components;

import cz.datalite.webdriver.By;
import org.openqa.selenium.WebElement;

/**
 *
 * This class handles messageboxes, standard popup windows.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class MessageBox {

    private MessageBox() {
    }

    /**
     * Finds messagebox with this label and presses Ok
     * @param label Window title
     */
    public static void pressOk( final String label ) {
        press( label, "Ok" );
    }

    /**
     * Finds messagebox with this label and presses Storno
     * @param label Window title
     */
    public static void pressStorno( final String label ) {
        press( label, "Storno" );
    }

    /**
     * Finds messagebox with this label and presses Ano
     * @param label Window title
     */
    public static void pressAno( final String label ) {
        press( label, "Ano" );
    }

    /**
     * Finds messagebox with this label and presses Ne
     * @param label Window title
     */
    public static void pressNe( final String label ) {
        press( label, "Ne" );
    }

    /**
     * Finds messagebox with this label and presses button
     * with specified label
     * @param label Window title
     * @param button Button's label
     */
    public static void press( final String label, final String button ) {
        findButton( findMessageBox( label ), button ).click();
    }

    protected static WebElement findMessageBox( final String label ) {
        return ZkElement.getZkDriver().getWebDriver().findElement( By.xpath( "//div[text()='" + label + "']/ancestor::div[@class='z-window-highlighted-hl']/parent::div[1]" ) );
    }

    protected static Button findButton( final WebElement messageBox, final String label ) {
        return new ZkElement( null, messageBox ).findButton( By.label( label ) );
    }
}
