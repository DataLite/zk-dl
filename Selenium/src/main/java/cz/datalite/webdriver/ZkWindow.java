package cz.datalite.webdriver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines parameters for UnitTest.
 * There is defined url, window id and detail Id.
 * This annotation is read by SeleniumUnitTest and
 * according to it the test prepares the page
 * in browser.
 *
 * @author Karel Cemus
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE } )
public @interface ZkWindow {

    /** @return Url of page to be tested without server prefix. */
    public String url();

    /** @return Identifier of main window in the page.  */
    public String id();

    /** @return Identifier of detail window which is displayed on the page  */
    public String detailId() default "";
}
