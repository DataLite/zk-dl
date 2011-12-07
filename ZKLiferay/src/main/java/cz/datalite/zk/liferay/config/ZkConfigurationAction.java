/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.config;

import com.liferay.portal.kernel.portlet.BaseConfigurationAction;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import cz.datalite.zk.liferay.LiferayException;

import javax.portlet.*;

/**
 * Liferay Config Action (configured by <configuration-action-class/>).
 *
 * Create a ZK portlet to handle the configuration and setup zkConfigPortlet and zkConfigPortletPath
 * init parameters accordingly. The zul page recieves  zkConfigPortletPreferences attribute, which contains
 * preferences to set.
 */
public class ZkConfigurationAction extends BaseConfigurationAction {

    @Override
    public String render(PortletConfig portletConfig, RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
        // original portlet
        ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
        String portletResource = ParamUtil.getString(renderRequest, "portletResource");
        Portlet selPortlet = PortletLocalServiceUtil.getPortletById(themeDisplay.getCompanyId(), portletResource);

        String zkConfigPortlet = getZkConfigPortlet(selPortlet);
        String zkConfigPortletPath = getZkConfigPortletPath(selPortlet);

        // portlet preferences are passed to ZUL
        PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(renderRequest, portletResource);

        // set attributes
        renderRequest.setAttribute("zkConfigPortlet", zkConfigPortlet );
        renderRequest.setAttribute("zkConfigPortletPath", zkConfigPortletPath);
        renderRequest.setAttribute("zkConfigPortletPreferences", preferences);
        renderRequest.setAttribute("zkConfigPortletResource", portletResource);

        // do not return actual JSP page - the hook will use the attributes instead
        return "-- This message is produced by ZkConfigurationAction and indicates misconfiguration. " +
                "It can be used only together with ZkConfigurationHook, which overrides configuration portlet and especially edit_configuration.jsp page. --";
    }

    /**
     * Path to a ZUL file in a config portlet.
     *
     * @param selPortlet portlet to congfigure
     * @return path to a ZUL file.
     */
    protected String getZkConfigPortletPath(Portlet selPortlet) {
        // config portlet name
        String zkConfigPortletPath = selPortlet.getInitParams().get("zkConfigPortletPath");
        if (zkConfigPortletPath == null)
            throw new LiferayException("For portlet configuration action '<configuration-action-class>cz.datalite.liferay.ZkConfigurationAction</configuration-action-class>' you need to set" +
                    " also 'zkConfigPortletPath' portlet init parameter with path to a ZUL page! " +
                    " (it is than passed as 'zk_page' parameter to the configuration portlet - default ZkConfig " +
                    "or you can change the name of the portlet with zkConfigPortlet init param)");
        return zkConfigPortletPath;
    }

    /**
     * Config portlet - default "ZkConfig" but can be overriden by zkConfigPortlet init param
     * @param selPortlet portlet to configure
     * @return name of config portlet (full path)
     */
    protected String getZkConfigPortlet(Portlet selPortlet) {
        // get portlet package from current portlet id by removeing portlet name
        String portletPackage = selPortlet.getPortletId();
        portletPackage = portletPackage.replaceFirst(selPortlet.getPortletName() , "");
        portletPackage = portletPackage.replaceAll("_INSTANCE.*$" , "");

        // name of the config portlet from configuration
        String zkConfigPortletName = selPortlet.getInitParams().get("zkConfigPortlet");
        if (zkConfigPortletName == null)
            zkConfigPortletName = "ZkConfig";

        return zkConfigPortletName + portletPackage;
    }
}
