package cz.datalite.zk.composer;

import cz.datalite.zk.annotation.ZkConfirm;
import java.lang.reflect.InvocationTargetException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

/**
 * The @ZkConfirm annotation - confirm dialog before method invocation.
 *
 * @author Jiri Bubnik
 */
public class DLZkConfirmCommand {

    private final String message;
    private final String title;
    private final int buttons;
    private final int accessButton;
    private final String icon;

    /**
     * Setup the confirm dialog.
     * @param confirm the @ZkConfirm annotation
     */
    public DLZkConfirmCommand(final ZkConfirm confirm) {
        super();
        message = confirm.message();
        title = confirm.title();
        buttons = confirm.buttons();
        accessButton = confirm.accessButton();
        icon = confirm.icon();
    }

    /**
     * Setup the confirm dialog.
     */
    public DLZkConfirmCommand() {
        super();
        message = null;
        title = null;
        buttons = -1;
        accessButton = -1;
        icon = null;
    }

    /**
     * Show the dialog.
     *
     * @param dlEvent target event
     * @param sourceEvent ZK event
     * @throws NoSuchMethodException error in target method invocation
     * @throws InvocationTargetException error in target method invocation
     * @throws InterruptedException dialog was closed.
     */
    public void invoke(final DLZkEvent dlEvent, final Event sourceEvent) throws NoSuchMethodException, InvocationTargetException, InterruptedException {
        if (message == null) {
            dlEvent.onConfirmPass(sourceEvent);
        } else {
            Messagebox.show(message, title, buttons, icon, new EventListener() {

                public void onEvent(final Event msgEvent) throws NoSuchMethodException, InvocationTargetException {
                    if ((Integer) msgEvent.getData() == accessButton) {
                        dlEvent.onConfirmPass(sourceEvent);
                    }
                }
            });
        }
    }
}
