package cz.datalite.zk.composer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

/**
 * Utility class providing the functionality of {@link SelectorComposer } to {@link DLBinder}
 * class and to its derivates.
 *
 *
 * @author Karel Cemus
 */
public class SelectorUtils {

    /**
     * Wires the properies with annotations {@link Listen} and {@link Wire}
     * 
     * Look at http://books.zkoss.org/wiki/Small_Talks/2012/January/MVVM_Extension:_Access_UI_Components_Inside_ViewModel
     *
     * @param composer view-model with annotated properties
     * @param self master view component
     */
    public static void wire( final Object composer, final Component self ) {
        // wire component @Wire
        Selectors.wireComponents( self, composer, false );
        // first event listener wiring @Listen
        Selectors.wireEventListeners( self, composer );

        // register event to wire variables just before component onCreate
        self.addEventListener( 1000, "onCreate", new BeforeCreateWireListener( composer, self ) );
        self.addEventListener( "onCreate", new AfterCreateWireListener( composer, self ) );
    }

    /** copy - paste from {@link SelectorComposer} line 130 */
    private static class BeforeCreateWireListener implements SerializableEventListener<Event> {

        private static final long serialVersionUID = 1L;
        // brought from GenericAutowireComposer

        private final Object composer;

        private final Component self;

        public BeforeCreateWireListener( Object composer, Component self ) {
            this.composer = composer;
            this.self = self;
        }

        public void onEvent( Event event ) throws Exception {
            // wire components again so some late created object can be wired in (e.g. DataBinder)
            Selectors.wireComponents( event.getTarget(), composer, true );
            self.removeEventListener( "onCreate", this ); // called only once
        }
    }

    /** copy - paste from {@link SelectorComposer} line 140 */
    private static class AfterCreateWireListener implements SerializableEventListener<Event> {

        private static final long serialVersionUID = 1L;

        private final Object composer;

        private final Component self;

        public AfterCreateWireListener( Object composer, Component self ) {
            this.composer = composer;
            this.self = self;
        }

        public void onEvent( Event event ) throws Exception {
            // second event listener wiring, for components created since doAfterCompose()
            Selectors.wireEventListeners( self, composer );
            self.removeEventListener( "onCreate", this ); // called only once
        }
    }
}
