package cz.datalite.zkdl.demo.spring.web;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import cz.datalite.helpers.ZKHelper;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.Controller;
import cz.datalite.zkdl.demo.spring.model.Todo;
import cz.datalite.zkdl.demo.spring.service.TodoService;
import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkConfirm;
import cz.datalite.zk.annotation.ZkController;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkModel;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.DLListboxCriteriaController;
import cz.datalite.zk.composer.DLComposer;
import org.zkoss.zk.ui.event.Events;


/**
 * Controller ke stránce todoPrehled.zul. <br />
 * Stránka slouží ...TODO
 *
 * @author Jiri Bubnik
 */
@Controller
public class TodoOverviewController extends DLComposer
{
    // what is my detail page?
    public static final String DETAIL_PAGE = "todoDetail.zul";

   // Spring service
    @Autowired TodoService todoService;

    // models
    // selected Todo item
    @ZkModel Todo todo;

    // controller for main list - load data from service, it automaticaly handles paging, sorting, searching, ...
    @ZkController DLListboxController<Todo> seznamCtl = new DLListboxCriteriaController<Todo>("TodoPrehledControllerList")
    {
        @Override
        protected DLResponse<Todo> loadData(DLSearch<Todo> search)
        {
            return todoService.searchAndCount(search);
        }
    };


    /******************************** UDÁLOSTI ********************************/

    /**
     * Refresh data in list (e.g. after detail is closed). 
     *
     * @param todo object to select from list.
     */
    @ZkEvent(event = ZkEvents.ON_REFRESH)
    @ZkBinding
    public void refresh(Todo todo)
    {
        seznamCtl.refreshDataModel();
        if (todo != null) seznamCtl.setSelectedItem(todo);
    }

    /**
     * Open detial form for new item or edit current.
     *
     * @param payload use to distingwish between events in method body
     */
    @ZkEvents(events = {
        @ZkEvent(id = "openDetailButton"),
        @ZkEvent(id = "listitem", event = Events.ON_DOUBLE_CLICK),
        @ZkEvent(id = "listitem", event = Events.ON_OK),
        @ZkEvent(id = "newButton", payload=ZkEvents.EVENT_NEW)
    })
    public void openDetail(int payload)
    {
       ZKHelper.openDetailWindow(self, DETAIL_PAGE, "todo",
               payload == ZkEvents.EVENT_NEW ? new Todo() : todo);
    }

    /**
     * Delete selected todo item.
     */
    @ZkEvent(id = "deleteButton")
    @ZkConfirm(message = "Opravdu chcete vybraný záznam smazat?", title = "Potvrzení")
    public void delete()
    {
        todoService.delete(todo);
        Events.postEvent(ZkEvents.ON_REFRESH, self, null);
    }

}