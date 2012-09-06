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
import cz.datalite.task.model.Category;
import cz.datalite.task.service.CategoryService;
import cz.datalite.zk.annotation.*;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.DLListboxCriteriaController;
import cz.datalite.zk.composer.DLComposer;
import org.zkoss.zk.ui.event.Events;


/**
 * Controller for the page categoryOverview.zul. <br />
 *
 * @author Jiri Bubnik
 */
@Controller
public class CategoryOverviewController extends DLComposer {
    public static final String DETAIL_PAGE = "categoryDetail.zul";

    // Spring services
    @Autowired CategoryService categoryService;

    // Selected item in ZUL
    @ZkModel Category category;

    // Controller for the main list
    @ZkController
    DLListboxController<Category> listCtl = new DLListboxCriteriaController<Category>("CategoryList") {
        @Override
        protected DLResponse<Category> loadData(DLSearch<Category> search) {
            return categoryService.searchAndCount(search);
        }
    };

    /**
     * Refresh data in the main list.
     *
     * @param category select this object in list (mainly after detail window is closed)
     */
    @ZkEvent(event = ZkEvents.ON_REFRESH)
    @ZkBinding
    public void refresh(Category category) {
        listCtl.refreshDataModel();
        if (category != null) listCtl.setSelectedItem(category);
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
        ZKHelper.openDetailWindow(self, DETAIL_PAGE, "category",
                payload == ZkEvents.EVENT_NEW ? new Category() : category);
    }

    /**
     * Delete selected object category.
     */
    @ZkEvent(id = "deleteButton")
    @ZkConfirm(message = "Are you sure?", title = "Please confirm the action")
    public void delete() {
        categoryService.delete(category);
        Events.postEvent(ZkEvents.ON_REFRESH, self, null);
    }

}