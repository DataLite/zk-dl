package cz.datalite.zk.composer;

import cz.datalite.zk.composer.listener.DLDetailController;
import cz.datalite.zk.composer.listener.DLMainModel;
import cz.datalite.zk.composer.listener.DLMasterController;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * Utils for initialization and using of the Master-Detail window composition.
 *
 * @author Karel Cemus
 */
public final class MasterDetailUtils {

    private MasterDetailUtils() {
        // library class
    }

    /**
     * <p>Check masterController variable in attributes / arguments /
     * parameterMap.</p>
     *
     * <p>Usage:<br/> &lt;include src="xx" masterController="${ctl}"&gt;<br/>
     * Include child page into master page and set masterController to this
     * variable. This method will connect master/detail automatically. </p>
     * <p>If this property is defined, but not correctly set up - throws an
     * exception.</p>
     */
    public static DLMasterController setupMasterController( final Object controller, final Component self ) {
        // try to find masterController in attributes (e.g. <include src="xx" masterController="${ctl}">)
        Object ctl = Executions.getCurrent().getAttribute( "masterController" );
        if ( ctl == null )
            // try to find masterController in arguments (e.g. <macroComponent masterController="${ctl}">)
        {
            ctl = Executions.getCurrent().getArg().get("masterController");
        }
        if ( ctl == null )
            // try to find masterController in params (e.g. Executions.createComponents(target,centerPlaceholder,Map("masterController" -> ctl)); )
        {
            ctl = Executions.getCurrent().getParameterMap().get("masterController");
        }

        if ( ctl != null ) {
            if ( !( ctl instanceof DLMasterController ) ) {
                throw new UiException("Attribute masterController has to be of type cz.datalite.composer.DLMasterController, got " + ctl);
            }

            registerToMasterController( controller, self, ( DLMasterController ) ctl );
        }

        return ( DLMasterController ) ctl;
    }

    /**
     * <p>Setup master controller (used by detail).</p>
     *
     * <p>If this class implements DLDetailController, it want to recieve master
     * change events. Method will add this as a child into master controller.
     * Nastaví novou hodnotu pro Master Controller a pokud jsem instanceof
     * DLDetailController, přidá se mu jako detail.</p>
     *
     * <p>Typically this is set in doBeforeCompose from
     * execution.getAttribute("masterController") automatically. Use only in
     * special case./<p>
     *
     * @param masterController new value for master controller
     */
    private static void registerToMasterController( final Object controller, final Component self, final DLMasterController masterController ) {
        if ( controller instanceof DLDetailController ) {
            masterController.addChildController( ( DLDetailController ) controller );

            // and register event on close to automatically remove from master controller
            final DLDetailController thisAsDetailController = ( DLDetailController ) controller;
            self.addEventListener( Events.ON_CLOSE, new EventListener() {

                public void onEvent( Event event ) throws Exception {
                    masterController.removeChildController( thisAsDetailController );
                }
            } );
        }
    }

    /** Detail has changed, notify the followers */
    public static <S extends DLMainModel> void onDetailChanged( List<DLDetailController> detailControllers, S model ) {
        try {
            for ( DLDetailController detail : detailControllers ) {
                detail.onMasterChanged( model );
            }
        } finally {
            // we want to clear flags in all cases, because if a flag triggers an error, we need to
            // clear it to recover from it.
            model.clearRefreshFlags();
        }
    }
}
