/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.web;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import cz.datalite.helpers.ZKHelper;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.Controller;
import cz.datalite.task.model.Priority;
import cz.datalite.task.model.Task;
import cz.datalite.task.service.TaskService;
import cz.datalite.zk.annotation.*;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.DLListboxCriteriaController;
import cz.datalite.zk.components.list.filter.compilers.EnumCriterionCompiler;
import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.zk.components.list.filter.components.EnumFilterComponent;
import cz.datalite.zk.components.list.filter.components.FilterComponent;
import cz.datalite.zk.composer.DLComposer;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Textbox;

import java.util.Set;


/**
 * Controller for the page taskOverview.zul. <br />
 *
 * @author Jiri Bubnik
 */
@Controller
public class TaskOverviewController extends DLComposer {
    public static final String DETAIL_PAGE = "taskDetail.zul";

    // Spring services
    @Autowired TaskService taskService;

    // Selected item in ZUL
    @ZkModel Task task;

    // Selected items in ZUL
    @ZkModel Set<Task> tasks;

    @ZkComponent
    Textbox textboxSelected;

    // Controller for the main list
    @ZkController
    DLListboxController<Task> listCtl = new DLListboxCriteriaController<Task>("TaskList") {
        @Override
        protected DLResponse<Task> loadData(DLSearch<Task> search) {
            return taskService.searchAndCount(search);
        }
    };

    // Filter component is used in normal filter for the user to enter value.
    // Component for enumeration is combobox with it's values, second parametr is property name to show
    @ZkController
    FilterComponent priorityFilterComponent = new EnumFilterComponent(Priority.values(), "name");

    @ZkController
    FilterCompiler priorityFilterCompiler = new EnumCriterionCompiler(Priority.values(), "name");

    @ZkEvent(id="taskList", event = Events.ON_SELECT)
    public void select()
    {
        StringBuilder selectedText = new StringBuilder();
        for (Task selectedTask : listCtl.getSelectedItems())
        {
            selectedText.append(selectedTask.getName());
            selectedText.append(", ");
        }
        textboxSelected.setValue(selectedText.toString());
    }


    /**
     * Refresh data in the main list.
     *
     * @param task select this object in list (mainly after detail window is closed)
     */
    @ZkEvent(event = ZkEvents.ON_REFRESH)
    @ZkBinding
    public void refresh(Task task) {
        listCtl.refreshDataModel();
        if (task != null) listCtl.setSelectedItem(task);
    }

    /**
     * Open detail window.
     *
     * @param payload current event index
     */
    @ZkEvents(events = {
            @ZkEvent(id = "openDetailButton"),
            @ZkEvent(id = "listitem", event = Events.ON_DOUBLE_CLICK),
            @ZkEvent(id = "listitem", event = Events.ON_OK),
            @ZkEvent(id = "newButton", payload = ZkEvents.EVENT_NEW)
    })
    public void openDetail(int payload) {
        ZKHelper.openDetailWindow(self, DETAIL_PAGE, "task",
                payload == ZkEvents.EVENT_NEW ? new Task() : task);
    }

    /**
     * Delete selected object task.
     */
    @ZkEvent(id = "deleteButton")
    @ZkConfirm(message = "Are you sure?", title = "Please confirm the action")
    public void delete() {
        taskService.delete(task);
        Events.postEvent(ZkEvents.ON_REFRESH, self, null);
    }

    /**
     * Basic example of Data -> UI Type Converter
     * @param done databinder data (see ZUL)
     * @return converted value. Here the style property based on data
     */
    public String coerceDone(boolean done)
    {
        if (done)
            return "color: gray; text-decoration: line-through;";
        else
           return "";
    }

    /**
     * More advanced use of Data -> UI Type Converter.
     *
     * @param priority databinder data (see ZUL).
     * @param cell - you can add second parameter, which must be exactly the component on
     *          which the converter is set
     * @return String value. In this example, there is a side efect on the component
     */
    public String coercePriority(Priority priority, Listcell cell)
    {
        if (Priority.HIGH == priority)
            cell.setStyle("background-color: coral;");

        return priority.getName();
    }


}