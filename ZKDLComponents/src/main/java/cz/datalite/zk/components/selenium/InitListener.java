package cz.datalite.zk.components.selenium;

import cz.datalite.zk.components.list.view.DLListbox;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zul.Label;

/**
 *
 * @author Karel Cemus
 */
public class InitListener implements org.zkoss.zk.ui.util.DesktopInit {

    public void init( final Desktop desktop, final Object request ) {
        desktop.addListener( new AuService() {

            public boolean service( final AuRequest auRequest, final boolean everError ) {
                final String cmd = auRequest.getCommand();
                if ( "onDataliteTestService".equals( cmd ) ) {
                    final String request = ( String ) auRequest.getData().get( "" );
                    final Page page = ( Page ) desktop.getPages().iterator().next();

                    Label label = ( Label ) page.getFellowIfAny( "seleniumResponse" );

                    if ( label == null ) { // if label not exists yet
                        label = new Label();
                        label.setPage( page );
                        label.setId( "seleniumResponse" );
                        label.setSclass( "selenium-response" );
                        label.setStyle( "position: absolute; top: 0; left: 0; height: 0; width: 0; display: block; overflow: hidden;" );
                    }
                    label.setValue( execute( request ) );

                    return true;
                } else {
                    return false;
                }
            }

            protected String execute( final String request ) {
                final String[] params = request.split( ":" );
                final String command = params[0];
                final String value = params[1];

                if ( "IdToUuid".equalsIgnoreCase( command ) ) {
                    return idToUuid( value );
                } else if ( "uuidToId".equalsIgnoreCase( command ) ) {
                    return uuidToId( value );
                } else if ( "idToUuidInContext".equalsIgnoreCase( command ) ) {
                    return idToUuidInContext( value );
                } else if ( "listboxComponents".equalsIgnoreCase( command ) ) {
                    return listboxComponentsFor( value );
                } //
                //else if( "".equalsIgnoreCase( command ) ) {
                //}
                else {
                    throw new IllegalArgumentException( "Unsupported function" );
                }


            }

            protected String idToUuid( final String id ) {
                final Component component = Path.getComponent( id );
                return component == null ? "" : component.getUuid();
            }

            protected String uuidToId( final String uuid ) {
                final Component component = desktop.getComponentByUuidIfAny( uuid );
                return component == null ? "" : component.getId();
            }

            protected Component uuidToComponent( final String uuid ) {
                return desktop.getComponentByUuidIfAny( uuid );
            }

            protected String idToUuidInContext( final String value ) {
                final String[] params = value.split( "," );
                final String parentUuid = params[0];
                final String id = params[1];
                final Component parent = uuidToComponent( parentUuid );
                if ( parent == null ) {
                    throw new IllegalStateException( "Server component tree is corrupted." );
                }
                final Component component = parent.getFellowIfAny( id );
                return component == null ? "" : component.getUuid();
            }

            protected String listboxComponentsFor( final String uuid ) {
                final DLListbox listbox = ( DLListbox ) uuidToComponent( uuid );
                if ( listbox == null ) {
                    throw new IllegalArgumentException( "Component with uuid " + uuid + " is not instance of DLListbox." );
                }
                return listbox.getController() == null ? listbox.getUuid() + ",," : listbox.getController().getUuidsForTest();
            }
        } );
    }
}
