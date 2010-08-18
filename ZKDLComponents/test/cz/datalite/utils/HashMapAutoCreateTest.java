/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.datalite.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class HashMapAutoCreateTest {

    /**
     * Test of get method, of class HashMapAutoCreate.
     */
    @Test
    public void testGet() {
        final MapAutoCreate<String, StringBuffer> map = new HashMapAutoCreate<String, StringBuffer>( StringBuffer.class );

        assertNotNull( "Get nesmí vrátit null", map.get( "string1" ) );
        map.get( "string1" ).append( "test" );
        assertEquals( "Aktualizace záznamu v mapě se nezdařila", "test", map.get( "string1" ).toString() );

    }

    /**
     * Test of keySet method, of class HashMapAutoCreate.
     */
    @Test
    public void testkeySet() {

        final MapAutoCreate<String, StringBuffer> map = new HashMapAutoCreate<String, StringBuffer>( StringBuffer.class );
        assertEquals( "Žádný get ještě nebyl zavolán", map.keySet().size(), 0 );
        map.get( "a" );
        map.get( "b" );
        assertEquals( "Žádný get ještě nebyl zavolán", map.keySet().size(), 2 );
    }
}
