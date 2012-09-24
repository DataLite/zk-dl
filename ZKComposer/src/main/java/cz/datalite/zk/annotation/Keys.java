package cz.datalite.zk.annotation;

/**
 * <p>Definition of function keys.</p>
 *
 * @author Karel Cemus
 */
public enum Keys {

    F1( 112, "#f1" ),
    F2( 113, "#f2" ),
    F3( 114, "#f3" ),
    F4( 115, "#f4" ),
    F5( 116, "#f5" ),
    F6( 117, "#f6" ),
    F7( 118, "#f7" ),
    F8( 119, "#f8" ),
    F9( 120, "#f9" ),
    F10( 121, "#f10" ),
    F11( 122, "#f11" ),
    F12( 123, "#f12" ),
    PAGE_UP( 33, "#pgup" ),
    PAGE_DOWN( 34, "#pgdn" ),
    END( 35, "#end" ),
    HOME( 36, "#home" ),
    LEFT( 37, "#left" ),
    UP( 38, "#up" ),
    RIGHT( 39, "#right" ),
    DOWN( 40, "#down" ),
    INSERT( 45, "#ins" ),
    DELETE( 46, "#del" );
    private final int code;
    private final String name;

    private Keys( final int code, final String name ) {
        this.code = code;
        this.name = name;
    }

    /**
     * Returns key code
     * @return key code like ascii
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns key name for zul definition
     * @return key name
     */
    public String getName() {
        return name;
    }
}
