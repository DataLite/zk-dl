package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Register eventListener on the component with @id. When event is posted,
 * this metod is invoked. </p>
 *
 * <p><b>Available method parameters (you can specify any of these parameters in any order):</b></p>
 * <dl>
 * <dt>ForwardEvent</dt>
 *     <dd>If you want to get original ForwardEvent, otherwise it is unwrapped with forwardEvent.getOrigin()</dd>
 * <dt>Event, MouseEvent, ...</dt>
 *     <dd>Any other event</dd>
 * <dt>Component)</dt>
 *     <dd>Event target component (aka event.getTarget())</dd>
 * <dt>onEvent( Object data )</dt>
 *     <dd>Data in the event is the parameter (aka event.getData())</dd>
 * <dt>onEvent( int payload )</dt>
 *     <dd>payload specified in this annotation (default is ZkEvents.EVENT_DEFAULT)</dd>
 * </dl>
 * @author Karel Cemus
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ZkEvent {

    /**
     * Source component identifier. On this component is registered event
     * listener. If id is empty, event listener is register on the window.
     * @return  id component identifier
     **/
    public String id() default "";

    /**
     * Event which is listened. Default is onClick
     * @return listened event, default {@link org.zkoss.zk.ui.event.Events#ON_CLICK}
     */
    public String event() default org.zkoss.zk.ui.event.Events.ON_CLICK;

    /**
     * Payload may be used to distinguish multiple events on a method.
     * If an event method contains int parameter type, the invocation will contain payload as its value.
     *
     * @return user payload or default value ZkEvents.EVENT_DEFAULT
     */
    public int payload() default ZkEvents.EVENT_DEFAULT;
}
