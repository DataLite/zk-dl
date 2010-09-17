package cz.datalite.zk.components;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class LabelLocator implements WebAppInit {

    public void init( final WebApp wapp ) {
        Labels.register( new org.zkoss.util.resource.LabelLocator() {

            public URL locate( final Locale locale ) throws MalformedURLException {
                final String url = "/metainfo/zk/zkdlcomponents" + (locale == null ? "" : "_" + locale.toString()) + ".properties";
                Logger.getLogger( "cz.datalite.zk.components" ).debug( "Loading property file at: " + url );
                return this.getClass().getResource( url );
            }
        } );
    }
}
