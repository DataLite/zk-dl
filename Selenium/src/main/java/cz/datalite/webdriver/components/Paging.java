package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * This is mirror of ZK Paging component
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Paging extends ZkElement {

    public Paging( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    public void firstPage() {
        webElement.findElement( By.className( "z-paging-first" ) ).click();
        zkDriver.waitForProcessing();
    }

    public void lastPage() {
        webElement.findElement( By.className( "z-paging-last" ) ).click();
        zkDriver.waitForProcessing();
    }

    public void nextPage() {
        webElement.findElement( By.className( "z-paging-next" ) ).click();
        zkDriver.waitForProcessing();
    }

    public void previousPage() {
        webElement.findElement( By.className( "z-paging-first" ) ).click();
        zkDriver.waitForProcessing();
    }

    public void goToPage( final int page ) {
        final WebElement input = webElement.findElement( By.tagName( "input" ) );
        input.clear();
        input.sendKeys( String.valueOf( page ) );
        webElement.click();
        zkDriver.waitForProcessing();
    }

    public int getPageCount() {
        final WebElement pageCountSpan = webElement.findElement( By.tagName( "table" ) ).findElements( By.tagName( "td" ) ).get( 8 );
        return "??".equals( pageCountSpan.getText() ) ? -1 : Integer.parseInt( pageCountSpan.getText() );
    }

    public int getPageIndex() {
        return Integer.parseInt( webElement.findElement( By.tagName( "input" ) ).getValue() );
    }
}
