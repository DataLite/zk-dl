package cz.datalite.zk.composer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 *
 * @author Karel Cemus
 */
public class LabelLocator implements WebAppInit {

    protected final static Logger LOGGER = LoggerFactory.getLogger( LabelLocator.class );
     
    public void init( final WebApp wapp ) {
        Labels.register( new org.zkoss.util.resource.LabelLocator() {

            public URL locate( final Locale locale ) throws MalformedURLException {
                final String url = "/metainfo/i3-label" + (locale == null ? "" : "_" + locale.toString()) + ".properties";
                LOGGER.debug( "Loading property file at: '{}'.", url );
                return this.getClass().getResource( url );
            }
        } );
    }
}
