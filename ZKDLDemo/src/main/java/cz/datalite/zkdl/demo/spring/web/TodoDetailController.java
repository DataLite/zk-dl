package cz.datalite.zkdl.demo.spring.web;

import cz.datalite.helpers.ZKHelper;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.Controller;
import cz.datalite.zkdl.demo.spring.model.Todo;
import cz.datalite.zkdl.demo.spring.service.TodoService;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkModel;
import cz.datalite.zk.annotation.ZkParameter;
import cz.datalite.zk.composer.DLComposer;
import org.zkoss.zk.ui.event.Events;


/**
 * Controller for todoDetail.zul.<br />
 * Gets data from Todo parameter and sets it to the model. We use spring service to save changes to the database.
 *
 * @author Jiri Bubnik
 */
@Controller
public class TodoDetailController extends DLComposer
{
    // Spring service
    @Autowired TodoService todoService;

    // current todo detail
    @ZkModel Todo todo;

    /**
     * Parameter can be on metod or property. This will pick up todo attriibute
     * and set here. If attribute doeas not exists, it will create new item
     *
     * @param todo objekt to set
     */
    @ZkParameter(createIfNull=true)
    public void setTodo(Todo todo)
    {
        if (todo.getIdTodo() != null)
            this.todo = todoService.get(todo.getIdTodo()); // this is because of lazy loading in Hibernate (not acctualy needed in this example, but...)
        else
            this.todo = todo;
    }



    /******************************** Events ********************************/

    /**
     * Save to database
     */
     @ZkEvents(events = {
        @ZkEvent(id = "saveButton"),
        @ZkEvent(event = Events.ON_OK)
    })
    public void save()
    {
        todoService.save(todo);

        // this method closes detail window and send refresh envent to master window - with new item as event data.
        ZKHelper.closeDetailWindow(self, true, todo);
    }

    /**
     * Discard changes.
     */
    @ZkEvents(events = {
        @ZkEvent(id = "cancelButton"),
        @ZkEvent(event = Events.ON_CANCEL)
    })
    public void cancel()
    {
        ZKHelper.closeDetailWindow(self);
    }


}