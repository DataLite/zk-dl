package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Register event listeners on the component with identifier @id.
 * Listener are defined in the array with {@link cz.datalite.zk.annotation.ZkEvent}.
 *
 * @author Karel Cemus
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ZkEvents {

    /**
    * The onRefreshParent event to notify that child has been edited so parent should be refreshed.
    * For example in detail/master cases detail sends this event to master.
    */
    public static final String ON_REFRESH_PARENT = "onRefreshParent";

    /**
    * The onRefresh event to notify refresh requirement.
    * For example when detail has been closed and master should be refreshed.
    */
    public static final String ON_REFRESH = "onRefresh";
    
    
    /**
     * Default event name to be fired after asynchronnous
     * method invocation finished. The method to be invoked asynchronnously is
     * set by {@link ZkAsync}.
     */
    public static String ON_ASYNC_FINISHED = "onAsyncFinished";

    /**
     * Default event name to be fired after asynchronnous. Please note, that progress info is not
     * enabled by default, you need to explicitly use it in ZkAsync annotation.
     *
     * method invocation progress event. The method to be invoked asynchronnously is
     * set by {@link ZkAsync}.
     */
    public static String ON_ASYNC_PROGRESS = "onAsyncProgress";


    /**
     * Predefined constant for default event type for payload.
     */
    public static final int EVENT_DEFAULT = 0;

    /**
     * Predefined constant for custom event payload, event is intended for creation - e.g. new button.
     */
    public static final int EVENT_NEW = 1;

    /**
     * Predefined constant for custom event payload, event is intended for modification - e.g. edit button.
     */
    public static final int EVENT_EDIT = 2;

    /**
     * Predefined constant for custom event payload, event is intended for deletion - e.g. delete button.
     */
    public static final int EVENT_DELETE = 3;


    /**
     * Array of event listeners
     * @return  event listeners
     **/
    public ZkEvent[] events();
}
