package cz.datalite.zkdl.demo.zkComposer;

import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkComponent;
import cz.datalite.zk.annotation.ZkConfirm;
import cz.datalite.zk.annotation.ZkController;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkModel;
import cz.datalite.zk.annotation.ZkParameter;
import cz.datalite.zk.composer.DLComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

/**
 * This example is a preview of ZkXxx annotations. It does not have any actual use, for more real-life usage see examples
 * with data-driven components (listbox, opening of detail, ...).
 *
 * Modules used: DLHelpers, ZKComposer
 *
 * @see http://zk.datalite.cz/wiki/-/wiki/Main/DLComposer++-+MVC+Controller
 *
 * @author Jiri Bubnik
 */
// Use this variable only if you want to change the name of the variable, under which will be @ZkController property accessible
// from ZUL file. The default value is "ctl" - usually you will use ${ctl.listboxController}, but with this annotation it is
// renamed to ${controller.listboxController}.
@ZkController(name="controller")
// The same as @ZkController, name under which will be @ZkModel property avilable (default is "ctl")
// - usually you will use just @{ctl.modelProperty}, but with this annotation it becomes @{model.modelProperty}
// note the difference - while ${ctl.listboxController} is EL expression (we want to set it only once in page composition,
// @{model.modelProperty} is bound via databinding - we might to set/get it to/from another value later via databindig.
@ZkModel(name="model")
public class ZkComposerController extends DLComposer
{
    // declare a model. The model will be available in ZUL by @{model.myModel}
    // of course you can declare this on setter and/or getter
    @ZkModel String myModel;

    // you can rename it for ZUL usage @{model.myOtherModel}
    @ZkModel(name="myOtherModel") String otherModel;

    // another model, we will use it to show event information
    @ZkModel String lastEvent;

    // because this example is so simple, no component with it's own controller is actually used.
    // you will be able to use it from ZUL by ${controller.controlSomeComponent}
    // the implementation is similar to ZkModel - but this property is readonly
    @ZkController String controlSomeComponent;

    // wire a component from ZUL by it's ID. If you skip the id attribute, property name is used
    @ZkComponent(id="myOtherTextbox") Textbox otherTextbox;

    /**
     *
     * @param value
     */
    @ZkParameter(name="myParam", required=false)
    void setModelValue(String value)
    {
        myModel = value;
    }


    /**
     * Basic usage of event - bind it to a button with ID (default event is onClick).
     *
     * To make it more interesting, we use wired component otherTextbox to alter some visual style.
     */
    @ZkEvent(id = "helloButton")
    public void hello() throws InterruptedException
    {
        otherTextbox.setStyle("color: red;");
        Messagebox.show("Hello World");
    }

    /**
     * Another basic usage, now with binding and confirmation.
     *
     * Method content works with model only, we need to tell ZK to update binding. @ZkBinding without arguments
     * does load of binindg on all components after method execution. It will be better to enumerate components
     * to bind by name, but there is only a small overhead by binding all components. If the bound value is not changed,
     * the binder does not set the new value anyway.
     *
     * @ZkConfirm annotation is handy for confirmation questions - it basicly shows messagebox and executes method only if
     * the user agrees (you can change messagebox buttons with additional parameters).
     *
     * Note that @ZkBinding and @ZkConfirm can be used only with @ZkEvent annotation (i.e. when they are called by the library)
     */
    @ZkEvent(id = "clearButton")
    @ZkBinding
    @ZkConfirm(title="Are you sure?", message="Do you really want to clear all fields?")
    public void clearAll()
    {
        otherTextbox.setStyle("");
        myModel = null;
        otherModel = null;
    }

    /**
     * Another usage of @ZkEvent, it can by registered on any event and component (it is the same as Events.addEventListener()).
     *
     * Not usage of ZkBindig - now we referesh only one component, because we know which data are changed.
     * With databinding you should always have in your mind, that there are two values - one in model and other in UI component.
     * ZK cannot automaticaly know, when to use model and bound it to a component and vice versa. Use @ZkBinding with saveBefore
     * or loadAfter attributes to do this mapping. Look at ZKBinderHelper class for details.
     */
    @ZkEvent(id = "textbox", event=Events.ON_CHANGE)
    @ZkBinding(component="myOtherTextbox")
    public void textboxChanged()
    {
        otherModel = myModel;
    }

    /**
     * You can register multiple events on a component. Use payload to distinguish beween events in the method body.
     *
     * Method called by @ZkEvent can have multiple parameters (you can skip any of them, any order).
     *
     * @param event the event that caused this invocation. There is hierarchy of events associated (ForwardEvent, MouseEvent, Event), use the
     *          most secific one
     * @param target the component that caused this invocation
     * @param data any data associated with the event (see ZK documentation for the event)
     * @param payload integer number, use to distinguish the events 
     */
    @ZkEvents(events = {
        @ZkEvent(id = "helloButton"),
        @ZkEvent(id = "clearButton", payload=ZkEvents.EVENT_DELETE),
        @ZkEvent(id = "textbox", event=Events.ON_CHANGE, payload=ZkEvents.EVENT_EDIT)
    })
    @ZkBinding
    public void catchEverything(Event event, Component target, Object data, int payload)
    {
        StringBuilder log = new StringBuilder()
            .append("Event: ")
            .append(event.getName())
            .append(", target: ")
            .append(target)
            .append(", data: ")
            .append(data)
            .append(", payload: ")
            .append(payload == ZkEvents.EVENT_EDIT ? "edit" : (payload == ZkEvents.EVENT_DELETE ? "delete" : "default"))
        ;

        lastEvent = log.toString();
    }

}
