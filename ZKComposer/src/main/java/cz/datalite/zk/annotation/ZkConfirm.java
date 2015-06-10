package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.zkoss.zul.Messagebox;

/**
 * <p>Add messagebox before the events. If method is annotated with
 * {@link cz.datalite.zk.annotation.ZkEvent} or 
 * {@link cz.datalite.zk.annotation.ZkEvents} this annotation adds
 * messagebox alert before method is invoked. There is @accessButton,
 * which is like key to this method. If user press other button than @accessButton,
 * method will not be invoked. If user press correct
 * button, method is invoked.</p>
 * <p>Default is question with button with Yes and No and accessButton
 * is Yes.</p>
 * 
 * @author Karel Cemus
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ZkConfirm {

    /**
     * Message for user
     * @return  message
     **/
    public String message();

    /**
     * Title of the messagebox
     * @return title
     */
    public String title();

    /**
     * Button in the messagebox. Default are {@link org.zkoss.zul.Messagebox#YES }
     * and {@link org.zkoss.zul.Messagebox#NO }
     * @return buttons
     */
    public int buttons() default Messagebox.YES | Messagebox.NO;

    /**
     * Messagebox type. Default is {@link org.zkoss.zul.Messagebox#QUESTION }
     * @return messagebox type
     */
    public String icon() default Messagebox.QUESTION;

    /**
     * Access button - key to the metod. Default is {@link org.zkoss.zul.Messagebox#YES }
     * @return access button
     */
    public int accessButton() default Messagebox.YES;

    /**
     * <p>Some applications are written localizable. Usually
     * the approach requires property file which contains
     * pairs key-value where key is text written in code
     * and value is localized message which is shown to user.
     * This annotation also contains some texts which can be localized.
     * This attribute says if the text in the annotation is text to be
     * directly shown to user or if it is only key into property file</p>
     *
     * <p>If the attribute is false, then the text is directly shown without
     * any modification. If the attirbute is true then there is conversion to
     * the value through
     * {@link org.zkoss.util.resource.Labels#getLabel(java.lang.String)}. This
     * class reads all property files defined as language packs and provides
     * coorect translates.</p>
     *
     * <p>Usage of this annotation attribute is highly reccomended for writing
     * some encapsulate component which can be distributed in stand-alone mode.
     * Otherwise <strong>this attribute is not comfortable for localization whole
     * application</strong>.</p>
     *
     * <h3>Project localization</h3>
     * <p>For localization of whole application the i18n can be activated
     * in configuration file. The disadvantage of this solution lies in
     * impossibility of localy disabling i18n and each message have to be
     * localized. Application configuration file <strong>zk.xml</strong>
     * allows definining of library properties. Write in this code
     * to enable complete localization of Zk annotations {@link ZkException},
     * .
     * <pre>{@code
     * &lt;!-- Enables required localization of &#064;ZkException
     *      and &#064;ZkConfirm annotations. --&gt;
     *  &lt;library-property&gt;
     *      &lt;name&gt;zk-dl.annotation.i18n&lt;/name&gt;
     *          &lt;value&gt;true&lt;/value&gt;
     *  &lt;/library-property&gt;
     * }</pre>
     * </p>
     *
     * <h3>Note:</h3>
     * <p>In default the localization is disabled.</p>
     *
     */
    public boolean i18n() default false;
}
