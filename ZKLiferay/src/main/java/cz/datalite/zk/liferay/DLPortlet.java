package cz.datalite.zk.liferay;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.DHtmlLayoutPortlet;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.sys.SessionsCtrl;

/**
 *
 * @author Jiri Bubnik
 */
public class DLPortlet extends DHtmlLayoutPortlet
{
    // set title
    public static final String TITLE = "cz.datalite.portlet.title";

    // use title from attribute
    @Override
    protected String getTitle(RenderRequest request)
    {
        Session session;
        try {
            session = getSession(request);
        } catch (PortletException ex) {
            throw new RuntimeException("Misconfigured DL ZK portlet.", ex);
        }

        if (session != null && session.hasAttribute(TITLE)) {
            return String.valueOf(session.getAttribute(TITLE));
        } else {
            return super.getTitle(request);
        }
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
