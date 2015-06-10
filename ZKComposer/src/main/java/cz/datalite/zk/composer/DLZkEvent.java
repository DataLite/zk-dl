package cz.datalite.zk.composer;

import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkBindings;
import cz.datalite.zk.annotation.ZkConfirm;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.XulElement;

/**
 * DLComposer @ZkEvent annotation creates this event. Constructor will scan the method for other annotations like @ZkBinding or @ZkConfirm
 * and attach required behaviour to the event.
 *
 * @author Jiri Bubnik
 */
@Deprecated
public class DLZkEvent
{
    private final Method method;
    private final List<DLZkBindingCommand> bindings = new LinkedList<>();
    private final ZkEvent zkEvent;
    private final DLZkConfirmCommand confirm;
    private final DLComposer controller;
    private final DLKeyEvent keyEvent;

    /**
     * Register new event on the component.
     *
     * @param event the event definition
     * @param method method which sholud be called in case of event happens
     * @param component controller master component
     * @param ctl controller
     */
    public DLZkEvent(final ZkEvent event, final Method method, final Component component, final DLComposer ctl) {
        super();
        try {
            this.zkEvent = event;
            this.method = method;
            this.controller = ctl;

            // load @ZkConfirm annotation if set
            ZkConfirm confirmAnnot = null;
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation instanceof ZkConfirm) {
                    confirmAnnot = (ZkConfirm) annotation;
                    break;
                }
            }
            this.confirm = confirmAnnot == null ? new DLZkConfirmCommand() : new DLZkConfirmCommand(confirmAnnot);

            // load @ZkBinding annotation if set
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation instanceof ZkBinding) {
                    this.bindings.add(new DLZkBindingCommand((ZkBinding) annotation, component));
                    break;
                } else if (annotation instanceof ZkBindings) {
                    for (ZkBinding zkBinding : ((ZkBindings) annotation).bindings()) {
                        this.bindings.add(new DLZkBindingCommand(zkBinding, component));
                    }
                    break;
                }
            }

            // setup key events
            keyEvent = event.event().startsWith("on") ? new DLKeyEvent() : new DLKeyEvent(event.event());

            // load the component
            final Component source;
            if (event.id().length() == 0)
                source = component;
            else if (event.id().startsWith("/"))
            {
                source = Path.getComponent(event.id());
                if (source == null)
                    throw new ComponentNotFoundException(event.id());
            }
            else
               source = component.getFellow(event.id());

            if (source instanceof XulElement) {
                if (((XulElement) source).getCtrlKeys() == null) {
                    ((XulElement) source).setCtrlKeys(keyEvent.getCtrlKey());
                } else {
                    ((XulElement) source).setCtrlKeys(((XulElement) source).getCtrlKeys() + keyEvent.getCtrlKey());
                }
            }
            source.addEventListener(event.event().startsWith("on") ? event.event() : Events.ON_CTRL_KEY, new EventListener() {

                // register the event
                public void onEvent(final Event event) throws NoSuchMethodException, InvocationTargetException, InterruptedException {
                    DLZkEvent.this.keyEvent.invoke(DLZkEvent.this, event);
                }
            });
        } catch (ComponentNotFoundException ex) {
            throw new ComponentNotFoundException("ZkEvent could not be registered on component \"" + event.id() + "\" because component wasn\'t found.", ex);
        }
    }

    /**
     * Do invoke the event. This is invoked by ZK after ZK event fires.
     *
     * @param event ZK event
     * @throws NoSuchMethodException in case the method has invalid arguments.
     * @throws InvocationTargetException in case the method returns error
     */
    public void invoke(final Event event) throws NoSuchMethodException, InvocationTargetException {
        // unwrap forward event
        final Event originEvent = (event instanceof org.zkoss.zk.ui.event.ForwardEvent) ?
                            ((org.zkoss.zk.ui.event.ForwardEvent) event).getOrigin() : event;

        Vector args = new Vector();
        for (Class type : method.getParameterTypes())
        {
            if (type.isAssignableFrom(originEvent.getClass()))
                args.add(originEvent); // unwrapped event
            else if (type.isAssignableFrom(event.getClass()))
                args.add(event);       // forward event
            else if (type.getName().equals("int"))
                args.add(this.zkEvent.payload());      // payload has to be primitive int type
            else if (event.getTarget() != null && type.isAssignableFrom(event.getTarget().getClass()))
                args.add(event.getTarget());   // target component
            else if (originEvent.getData() != null && type.isAssignableFrom(originEvent.getData().getClass()))
                args.add(originEvent.getData());   // event data
            else if (event.getTarget() == null && type.isAssignableFrom(Component.class))
                args.add(null); // null target component
            else if (originEvent.getData() == null)
                args.add(null); // null data (can be of any type)
            else
                throw new NoSuchMethodException("No such method \"" + method.getName() + "\" with correct arguments for event " + event.toString() +
                        ". Unknown parameter type: " + type.getName());
        }

//        final Object[][] args = new Object[][]{
//                new Object[]{
//                        (event instanceof org.zkoss.zk.ui.event.ForwardEvent) ?
//                            ((org.zkoss.zk.ui.event.ForwardEvent) event).getOrigin() : event
//                },
//                new Object[]{event}, new Object[]{event.getTarget()},
//                new Object[]{event, event.getTarget()},
//                new Object[]{event.getData()},
//                new Object[]{}
//        };

        // save bindings
        for (DLZkBindingCommand command : bindings) {
            command.save();
        }

        try {
            method.invoke(controller, args.toArray());
        } catch (IllegalAccessException ex) {
            throw new NoSuchMethodException("Cannot access method \"" + method.getName() + "\". Error " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new NoSuchMethodException("Invalid arguments for method \"" + method.getName() + "\". Error " + ex.getMessage());
        }

        for (DLZkBindingCommand command : bindings) {
            try {
                command.load();
            } catch (Exception e) {
                throw new UiException("Invocation of @ZkBinding(loadAfer=true) on method \'" + method.toString() + "\' Error: " + e.getMessage(), e);
            }
        }
        return;
    }

    /**
     * Register all events on a method.
     *
     * @param method target method
     * @param component master component of the composer.
     * @param ctl composer
     */
    public static void registerEvents(final Method method, final Component component, final DLComposer ctl) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof ZkEvent) {
                new DLZkEvent((ZkEvent) annotation, method, component, ctl);
                break;
            } else if (annotation instanceof ZkEvents) {
                for (ZkEvent event : ((ZkEvents) annotation).events()) {
                    new DLZkEvent(event, method, component, ctl);
                }
                break;
            }
        }
    }

    void onKeyEventPass(final Event sourceEvent) throws NoSuchMethodException, InvocationTargetException, InterruptedException {
        DLZkEvent.this.confirm.invoke(DLZkEvent.this, sourceEvent);
    }

    void onConfirmPass(final Event sourceEvent) throws NoSuchMethodException, InvocationTargetException {
        invoke(sourceEvent);
    }
}
