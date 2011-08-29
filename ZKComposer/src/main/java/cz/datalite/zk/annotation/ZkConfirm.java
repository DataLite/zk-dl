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
 * @author Karel Čemus <cemus@datalite.cz>
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
     * Defines if the title and message should be
     * translated using {@link org.zkoss.util.resource.Labels}
     * or not. In default it is <strong>false</strong>
     */
    public boolean localize() default false;
}
