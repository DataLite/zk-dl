package cz.datalite.zkdl.demo.liferay;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import cz.datalite.dao.DLResponse;
import cz.datalite.helpers.ZKHelper;
import cz.datalite.stereotype.Controller;
import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkConfirm;
import cz.datalite.zk.annotation.ZkController;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkModel;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.composer.DLComposer;
import cz.datalite.zk.liferay.DLListboxLiferayController;
import org.zkoss.zk.ui.event.Events;


/**
 * Controller ke stránce liferayUserOverview.zul. <br />
 * Stránka slouží ...TODO
 *
 * @author Jiri Bubnik
 */
@Controller
public class LiferayUserOverviewController extends DLComposer
{
    // konstanty \\
    /** Konstanta s názvem detailní stránky. */
    public static final String DETAIL_PAGE = "liferayUserOverview.zul";

    // modely \\
    /** Zde se vede aktuálně vybraný objekt LiferayUserOverviewController. */
    @ZkModel User user;

    // seznamy \\
    @ZkController DLListboxController<User> seznamCtl = new DLListboxLiferayController<User>("LiferayUserOverviewControllerList")
    {
        @Override
        protected DLResponse<User> loadData(DynamicQuery dynamicQuery) throws SystemException
        {
            dynamicQuery.add(RestrictionsFactoryUtil.ne("emailAddress", "default@liferay.com"));
           return new DLResponse<User>(UserLocalServiceUtil.dynamicQuery(dynamicQuery), (int) UserLocalServiceUtil.dynamicQueryCount(dynamicQuery));
        }
    };


    /******************************** UDÁLOSTI ********************************/

    /**
     * Metoda aktualizuje data v seznamu (např. při uložení detailu).
     * @param user objekt, který se má automaticky vybrat v seznamu.
     */
    @ZkEvent(event = ZkEvents.ON_REFRESH)
    @ZkBinding
    public void refresh(User user)
    {
        seznamCtl.refreshDataModel();
        if (user != null) seznamCtl.setSelectedItem(user);
    }

    /**
     * Metoda otevře aktuálně vybraný objekt LiferayUserOverviewController pro editaci / detail
     * nebo prázdný formulář pro vytvoření nového.
     *
     * @param payload říká, zda chceme vytvořit zcela nový objekt nebo pouze
     * upravit vybraný.
     */
    @ZkEvents(events = {
        @ZkEvent(id = "openDetailButton"),
        @ZkEvent(id = "listitem", event = Events.ON_DOUBLE_CLICK),
        @ZkEvent(id = "listitem", event = Events.ON_OK),
        @ZkEvent(id = "newButton", payload=ZkEvents.EVENT_NEW)
    })
    public void openDetail(int payload)
    {
       ZKHelper.openDetailWindow(self, DETAIL_PAGE, "liferayUserOverviewController",
               payload == ZkEvents.EVENT_NEW ? UserLocalServiceUtil.createUser(0) : user);
    }

    /**
     * Metoda smaže aktuálně vybraný objekt LiferayUserOverviewController.
     */
    @ZkEvent(id = "deleteButton")
    @ZkConfirm(message = "Opravdu chcete vybraný záznam smazat?", title = "Potvrzení")
    public void delete() throws SystemException
    {
        UserLocalServiceUtil.deleteUser(user);
        Events.postEvent(ZkEvents.ON_REFRESH, self, null);
    }

}