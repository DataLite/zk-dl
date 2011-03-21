/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.web;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import cz.datalite.dao.DLSort;
import cz.datalite.helpers.ZKHelper;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.Controller;
import cz.datalite.task.model.Category;
import cz.datalite.task.model.Priority;
import cz.datalite.task.model.Task;
import cz.datalite.task.service.CategoryService;
import cz.datalite.task.service.TaskService;
import cz.datalite.zk.annotation.*;
import cz.datalite.zk.components.list.DLFilter;
import cz.datalite.zk.components.list.DLListboxCriteriaController;
import cz.datalite.zk.components.list.DLListboxSimpleController;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.lovbox.DLLovboxController;
import cz.datalite.zk.components.lovbox.DLLovboxGeneralController;
import cz.datalite.zk.composer.DLComposer;
import org.zkoss.zk.ui.event.Events;

import java.util.Arrays;
import java.util.List;


/**
 * Controller for the page taskDetail.zul. <br />
 *
 * @author Jiri Bubnik
 */
@Controller
public class TaskDetailController extends DLComposer {
    // Spring services
    @Autowired TaskService taskService;
    @Autowired CategoryService categoryService;

    // Selected item in ZUL
    @ZkModel Task task;

    
    @ZkController
    DLLovboxController<Category> lovboxCategoryCtl = new DLLovboxGeneralController<Category>(
            new DLListboxCriteriaController<Category>(Category.class.getName() + "#lovboxCategoryCtl") {

        @Override
        protected DLResponse<Category> loadData(final DLSearch<Category> search) {
            return categoryService.searchAndCount(search);
        }
    });

    @ZkController
    DLLovboxController<Priority> lovboxPriorityCtl = new DLLovboxGeneralController<Priority>(
            new DLListboxSimpleController<Priority>(Priority.class.getName() + "#lovboxPriorityCtl") {
                @Override
                protected DLResponse<Priority> loadData(List<NormalFilterUnitModel> filter, int firstRow, int rowCount, List<DLSort> sorts) {
                    return DLFilter.filterAndCount(filter, Arrays.asList(Priority.values()), firstRow, rowCount, sorts);
                }
            }
    );

    
    /**
     * Set task according to parameter (or create new entity) <br />
     *
     * @param task object from the ZK parameter
     */
    @ZkParameter(createIfNull = true)
    public void setTask(Task task) {
        if (task.getIdTask() != null)
            this.task = taskService.get(task.getIdTask());
        else
            this.task = task;
    }

    /**
     * Save the item.
     */
    @ZkEvents(events = {
            @ZkEvent(id = "saveTaskButton"),
            @ZkEvent(event = Events.ON_OK)
    })
    @ZkBinding(saveBefore = true, loadAfter = false)
    public void save() {
        taskService.save(task);
        ZKHelper.closeDetailWindow(self, true, task);
    }

    /**
     * Close the window
     */
    @ZkEvents(events = {
            @ZkEvent(id = "cancelTaskButton"),
            @ZkEvent(event = Events.ON_CANCEL)
    })
    public void cancel() {
        ZKHelper.closeDetailWindow(self);
    }
}