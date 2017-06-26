package cz.datalite.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Utility class for ExpectedCondition interface to locate element with By specification.<br/>
 * <br/>
 * Usage:<br/>
 * new WebDriverWait(driver, 30).until(new VisibilityOfElementLocated(By.id("myElement")));
 *
 * @author http://seleniumexamples.com/blog/examples/selenium-2-examples/
 * @author Jiri Bubnik
 */
public class VisibilityOfElementLocated implements ExpectedCondition<Boolean>
{
    /** Parent element. */
    WebElement parent;

    /** The condition to fulfill. */
    By findCondition;

    /**
     * Create new condition to wait for.
     *
     * @param by the condition
     */
    public VisibilityOfElementLocated(By by) {
            this.findCondition = by;
    }

    /**
     * Create new condition to wait for.
     *
     * @param parent parnet
     * @param by the condition
     */
    public VisibilityOfElementLocated(WebElement parent, By by) {
        this.parent = parent;
        this.findCondition = by;
    }

    /**
     * Called by wait() method.
     *
     * @param driver the driver
     * @return always true (throws exception if the element is not found)
     *
     * @throws NoSuchElementException If no matching elements are found
     */
    public Boolean apply(WebDriver driver) {
            if (parent == null) {
                driver.findElement(this.findCondition);
            } else {
                parent.findElement(this.findCondition);
            }
            return true;
    }
}
