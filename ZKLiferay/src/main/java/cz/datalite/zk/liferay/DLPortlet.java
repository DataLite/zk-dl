package cz.datalite.zk.liferay;

import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.WebKeys;
import cz.datalite.helpers.StringHelper;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.DHtmlLayoutPortlet;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.sys.SessionsCtrl;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 *
 * @author Jiri Bubnik
 */
public class DLPortlet extends DHtmlLayoutPortlet
{
    /**
     * Use ZK's session attributes to set custom portlet title.
     *  -> session.setAttribute(DLPortlet.TITLE, "My dynamic portlet title");
     */
    public static final String TITLE = "cz.datalite.portlet.title";

    /**
     * Session attribute to save map for portlet roles (roleName -> roleLink) to translate
     * portlet roles to Liferay roles.
     */
    public static final String ROLE_MAPPERS = "cz.datalite.portlet.roleMappers";

    /**
     * Process a ZK page as a portlet.
     *
     * First setup some session parameters with Liferay info - to be able access it in runtime
     * {@inheritDoc}
     */
    @Override
    protected boolean process(Session sess, RenderRequest request, RenderResponse response, String path, boolean bRichlet) throws PortletException, IOException {

        setupSessionParameters(sess, request);

        boolean result = super.process(sess, request, response, path, bRichlet);


        // overwrite portlet title with TITLE attribute from session (if set)
        if (result && sess.hasAttribute(TITLE))
            response.setTitle(String.valueOf(sess.getAttribute(TITLE)));

        return result;
    }

    /**
     * Setup Liferay parameters to the session
     * <ul>
     * <li> WebKeys.THEME_DISPLAY - all basic Liferay display parameters (including current user) </li>
     * <li> ROLE_MAPPERS - list of mapped role (to provide isUserInRole even in servlet requests)</li>
     * </ul>
     * @param sess
     * @param request
     */
    protected void setupSessionParameters(Session sess, RenderRequest request)
    {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        sess.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

        String portletId = null;
        if (themeDisplay != null && themeDisplay.getPortletDisplay() != null)
            portletId = themeDisplay.getPortletDisplay().getId();
        if (StringHelper.isNull(portletId))
            portletId = (String) request.getAttribute(WebKeys.PORTLET_ID);

        Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);
        sess.setAttribute(ROLE_MAPPERS, portlet.getRoleMappers());
    }

    private Session getSession(RenderRequest request)
    throws PortletException {
            final WebManager webman = (WebManager)getPortletContext().getAttribute("javax.zkoss.zk.ui.WebManager");
            final WebApp wapp = webman.getWebApp();
            final PortletSession psess = request.getPortletSession();
            final Session sess = SessionsCtrl.getSession(wapp, psess);
            return sess != null ? sess:
                    SessionsCtrl.newSession(wapp, psess, request);
    }

}
