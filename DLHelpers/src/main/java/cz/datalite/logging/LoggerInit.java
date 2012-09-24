package cz.datalite.logging;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * LoggerInit allows to redefine JUL (java.util.logging) default handler. This
 * can be used to programatically override the default behaviour instead of
 * using property file 'logging.properties'. The class resets the current
 * settings and sets the given handler instead as the handler of root logger. It
 * can be used for example to redirect JUL logs to SLF4J.
 *
 * @author Karel Cemus
 */
public class LoggerInit {

    /**
     * LoggerInit allows to redefine JUL (java.util.logging) default handler.
     * This can be used to programatically override the default behaviour
     * instead of using property file 'logging.properties'. The class resets the
     * current settings and sets the given handler instead as the handler of
     * root logger. It can be used for example to redirect JUL logs to SLF4J.
     *
     * @param handler logging handler
     */
    public static void init(Handler handler) {

        // java.util.logging configuration manager
        final LogManager manager = LogManager.getLogManager();
        // reset current settings
        manager.reset();
        
        // get root logger
        final Logger root = manager.getLogger(""); // root logger
        // set new handler
        root.addHandler(handler);
    }
}
