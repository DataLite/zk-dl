/*
 * Copyright (c) 2011, DataLite. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package cz.datalite.zk.annotation.invoke;

import cz.datalite.zk.annotation.Keys;
import cz.datalite.zk.annotation.ZkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zul.impl.XulElement;

/**
 * <p>Handler filters key events if they are set. ZK
 * doesnt offer specific event for each hot key so if
 * there are more hot keys set on one component then
 * there is only one fired event. But each source of
 * event can be handled in a different way (different
 * method, payload etc.) so there has to be key filter.
 * Otherwise user would need to filter it himself.</p>
 *
 * @author Karel Cemus
 */
public class KeyEventHandler extends Handler {
    
    /** instance of logger */
    private static final Logger LOGGER = LoggerFactory.getLogger( MethodInvoker.class );

    /** has to be used ctrl or not */
    private final boolean ctrl;

    /** has to be used alt or not */
    private final boolean alt;

    /** has to be used shift or not */
    private final boolean shift;

    /** ascii code of key */
    private final int code;

    private final Keys key;

    public static Invoke process( Invoke inner, ZkEvent annotation ) {
        if ( annotation.event().startsWith( "on" ) ) { // this is not definition of keyEvent
            return inner; // no decoration
        } else {
            return new KeyEventHandler( inner, annotation.event() );
        }
    }

    private KeyEventHandler( Invoke inner, String event ) {
        super( inner );

        final String[] haystack = event.split( "\\+" );
        ctrl = find( "CTRL", haystack );
        alt = find( "ALT", haystack );
        shift = find( "SHIFT", haystack );

        int ascii;
        Keys keys = null;
        try {
            keys = Keys.valueOf( haystack[haystack.length - 1] );
            ascii = keys.getCode();
        } catch ( IllegalArgumentException ex ) {
            ascii = haystack[haystack.length - 1].charAt( 0 );
        }
        key = keys;
        code = ascii;
    }

    @Override
    protected boolean doBefore(final Context invocationContext) {
        if ( !(invocationContext instanceof ZkEventContext) ) {
            LOGGER.error( "MethodInvoker is bound to @ZkEvent and takes ZkEventContext only." );
            throw new IllegalArgumentException( "MethodInvoker is bound to @ZkEvent and takes ZkEventContext only." );
        }
        
        final ZkEventContext context = ( ZkEventContext ) invocationContext;
        
        if ( code == -1 ) {
            return true;
        } else if ( context.getEvent() instanceof KeyEvent ) {
            final KeyEvent keyEvent = ( KeyEvent ) context.getEvent();
            // conditions check
            return keyEvent.isAltKey() == alt
                    && keyEvent.isCtrlKey() == ctrl
                    && keyEvent.isShiftKey() == shift
                    && keyEvent.getKeyCode() == code;
        }
        return false; // onKey filter but not onKey event
    }

    /**
     * Přeloží user-friendly syntaxi do syntaxe ZK
     * @return ZK syntaxe eventu
     */
    private String getKeys() {
        if ( code == -1 ) {
            return "";
        }
        final StringBuffer s = new StringBuffer( "" );
        if ( ctrl ) {
            s.append( '^' );
        }
        if ( alt ) {
            s.append( '@' );
        }
        if ( shift ) {
            s.append( '$' );
        }
        s.append( key == null ? ( char ) code : key.getName() );
        return s.toString().toLowerCase();
    }

    @Override
    public Component bind( Component master ) {
        Component component = super.bind( master );

        // adding hot key listener on component
        if ( component instanceof XulElement ) {
            XulElement target = ( XulElement ) component;
            String keys = target.getCtrlKeys() == null ? "" : target.getCtrlKeys();
            target.setCtrlKeys( keys + getKeys() );
        } else {
            throw new UiException( "CtrlKey event can be set only on XulElements." );
        }

        return component;
    }

    private boolean find( final String needle, final String[] haystack ) {
        for ( String string : haystack ) {
            if ( needle.equals( string ) ) {
                return true;
            }
        }
        return false;
    }
}
