package cz.datalite.zk.util;

import java.util.ResourceBundle;

/**
 *
 * @author Karel Cemus
 */
public final class Translator {

    private static Translator instance;
    private final ResourceBundle bundle;

    private Translator() {
        bundle = ResourceBundle.getBundle( "cz.datalite.zk.util.zkLibDict" );
    }

    private static Translator getInstance() {
        if ( instance == null )
            instance = new Translator();
        return instance;
    }

    public static String translate( final String key ) {
        return getInstance().bundle.getString( key );
    }
}
