/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.web;

import cz.datalite.helpers.ZKHelper;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.Controller;
import cz.datalite.task.model.Category;
import cz.datalite.task.service.CategoryService;
import cz.datalite.zk.annotation.*;
import cz.datalite.zk.composer.DLComposer;
import org.zkoss.zk.ui.event.Events;


/**
 * Controller for the page ${Zul_Name.substring(0,1).toLowercase()}ategoryDetail.zul. <br />
 *
 * @author Jiri Bubnik
 */
@Controller
public class CategoryDetailController extends DLComposer {
    // Spring services
    @Autowired CategoryService categoryService;

    // Selected item in ZUL
    @ZkModel Category category;

    /**
     * Set category according to parameter (or create new entity) <br />
     *
     * @param category object from the ZK parameter
     */
    @ZkParameter(createIfNull = true)
    public void setCategory(Category category) {
        if (category.getIdCategory() != null)
            this.category = categoryService.get(category.getIdCategory());
        else
            this.category = category;
    }

    /**
     * Save the item.
     */
    @ZkEvents(events = {
            @ZkEvent(id = "saveCategoryButton"),
            @ZkEvent(event = Events.ON_OK)
    })
    @ZkBinding(saveBefore = true, loadAfter = false)
    public void save() {
        categoryService.save(category);
        ZKHelper.closeDetailWindow(self, true, category);
    }

    /**
     * Close the window
     */
    @ZkEvents(events = {
            @ZkEvent(id = "cancelCategoryButton"),
            @ZkEvent(event = Events.ON_CANCEL)
    })
    public void cancel() {
        ZKHelper.closeDetailWindow(self);
    }
}