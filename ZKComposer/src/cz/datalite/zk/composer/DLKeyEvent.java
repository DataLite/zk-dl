package cz.datalite.zk.composer;

import cz.datalite.zk.annotation.Keys;
import java.lang.reflect.InvocationTargetException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.KeyEvent;

/**
 * Event from keybord (@ZkEvent(event="CTRL+ALT+T"), @ZkEvent(event="SHIFT+F11") etc.)
 *
 * @author Jiri Bubnik
 */
public class DLKeyEvent {

    private final boolean ctrl;
    private final boolean alt;
    private final boolean shift;
    private final int code;
    private Keys key = null;

    /**
     * Empty event.
     */
    public DLKeyEvent() {
        super();
        ctrl = false;
        alt = false;
        shift = false;
        code = -1;
    }

    /**
     * Setup event.
     *
     * @param string event definition in format - CTRL+ALT+T
     */
    public DLKeyEvent(final String string) {
        super();
        final String[] haystack = string.split("\\+");
        ctrl = find("CTRL", haystack);
        alt = find("ALT", haystack);
        shift = find("SHIFT", haystack);
        int ascii = 0;
        try {
            key = Keys.valueOf(haystack[haystack.length - 1]);
            ascii = key.getCode();
        } catch (IllegalArgumentException ex) {
            ascii = haystack[haystack.length - 1].charAt(0);
        }
        code = ascii;
    }

    private boolean find(final String needle, final String[] haystack) {
        for (String string : haystack) {
            if (needle.equals(string)) {
                return true;
            }
        }
        return false;
    }

    String getCtrlKey() {
        if (code == -1) {
            return "";
        }
        final StringBuffer s = new StringBuffer("");
        if (ctrl) {
            s.append('^');
        }
        if (alt) {
            s.append('@');
        }
        if (shift) {
            s.append('$');
        }
        s.append(key == null ? (char) code : key.getName());
        return s.toString().toLowerCase();
    }

    /**
     * Invoke the event.
     *
     * @param dlEvent target event
     * @param sourceEvent ZK event
     * @throws NoSuchMethodException  error in target method invocation
     * @throws InvocationTargetException error in target method invocation
     * @throws InterruptedException error in target method invocation
     */
    public void invoke(final DLZkEvent dlEvent, final Event sourceEvent) throws NoSuchMethodException, InvocationTargetException, InterruptedException {
        if (code == -1) {
            dlEvent.onKeyEventPass(sourceEvent);
        } else if (sourceEvent instanceof KeyEvent) {
            final KeyEvent event = (KeyEvent) sourceEvent;
            if (event.isAltKey() != alt) {
                return;
            }
            if (event.isCtrlKey() != ctrl) {
                return;
            }
            if (event.isShiftKey() != shift) {
                return;
            }
            if (event.getKeyCode() != code) {
                return;
            }
            dlEvent.onKeyEventPass(sourceEvent);
        }
    }
}
