/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.config;

import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.Controller;
import cz.datalite.zk.annotation.ZkModel;
import cz.datalite.zk.annotation.ZkParameter;
import cz.datalite.zk.composer.DLComposer;
import cz.datalite.zk.composer.listener.DLMainModel;
import cz.datalite.zk.liferay.DLLiferayService;
import cz.datalite.zk.liferay.LiferayException;
import cz.datalite.zk.liferay.ZKLiferayHelper;
import cz.datalite.zk.liferay.jpa.LiferayOperationDenied;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Convenient superclass for portlet configuration.
 *
 * @author Jiri Bubnik
 */
@Controller
public class ZkConfigutationController<T extends DLMainModel> extends DLComposer<T> {

    @Autowired
    DLLiferayService dlLiferayService;

    protected DateFormat dateFormat = DateFormat.getInstance();

    // required - should be true, but set to false for convenient testing outside portal
    @ZkParameter(name = "portletResource", required = false)
    protected String portletResource;

    // required - should be true, but set to false for convenient testing outside portal
    @ZkParameter(name = "layoutId", required = false)
    protected String layoutId;

    // preferences to update
    protected PortletPreferences portletPreferences; // = new PortletPreferencesImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        // find preferences on layout page (it can be different to configuration page in case of
        // ZkConfig on hidden page)
        Layout layout = LayoutLocalServiceUtil.getLayout(Long.valueOf(layoutId));
        portletPreferences =
                PortletPreferencesFactoryUtil.getPortletSetup(layout, portletResource, StringPool.BLANK);

        setupWindowSize(comp);
    }

    /**
     * Defualt implementation is to change popup window according to ZK window size.
     * You can override this method and use methods like ZKLiferayHelper.clientCalculateComponent90Percent(comp);
     * to resize borderlayout windows according to acctual window size.
     *
     * ZK usually behaves like an application - fit into screen. We need to set size on client.
     * @param comp component to resize
     */
    protected void setupWindowSize(Component comp) {
        ZKLiferayHelper.clientResizeBodyHeightAuto(comp);
    }


    /** String values with escaped HTML. When you need html then use getPreferences().setValue().*/
    @ZkModel
    protected Map<String, String> preferencesString = new HashMap<String, String>()
    {
        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public String get(Object key) {
            return getPreferences().getValue(String.valueOf(key), null);
        }

        /** Insert string value to preferences with escaped HTML. */
        @Override
        public String put(String key, String value) {
            try {
                getPreferences().setValue(key, HtmlUtil.escape(value));
            } catch (ReadOnlyException e) {
                throw new LiferayOperationDenied("Preferences are read only, cannot reset.", e);
            }
            return null;
        }
    };
    
    @ZkModel
    protected Map<String, Integer> preferencesInteger = new HashMap<String, Integer>()
    {
        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public Integer get(Object key) {
            String value = getPreferences().getValue((String) key, null);
            return value == null ? null : Integer.valueOf(value);
        }

        @Override
        public Integer put(String key, Integer value) {
            try {
                getPreferences().setValue(key, value == null ? null : String.valueOf(value));
            } catch (ReadOnlyException e) {
                throw new LiferayException("Unable to store preferences: " + e.getMessage());
            }
            return null;
        }
    };

    @ZkModel
    protected Map<String, Long> preferencesLong = new HashMap<String, Long>()
    {
        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public Long get(Object key) {
            String value = getPreferences().getValue((String) key, null);
            return value == null ? null : Long.valueOf(value);
        }

        @Override
        public Long put(String key, Long value) {
            try {
                getPreferences().setValue(key, value == null ? null : String.valueOf(value));
            } catch (ReadOnlyException e) {
                throw new LiferayException("Unable to store preferences: " + e.getMessage());
            }
            return null;
        }
    };

    @ZkModel
    protected Map<String, Double> preferencesDouble = new HashMap<String, Double>()
    {
        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public Double get(Object key) {
            String value = getPreferences().getValue((String) key, null);
            return value == null ? null : Double.valueOf(value);
        }

        @Override
        public Double put(String key, Double value) {
            try {
                getPreferences().setValue(key, value == null ? null : String.valueOf(value));
            } catch (ReadOnlyException e) {
                throw new LiferayException("Unable to store preferences: " + e.getMessage());
            }
            return null;
        }
    };

    @ZkModel
    protected Map<String, Boolean> preferencesBoolean = new HashMap<String, Boolean>()
    {
        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public Boolean get(Object key) {
            String value = getPreferences().getValue((String) key, null);
            return value == null ? null : Boolean.valueOf(value);
        }

        @Override
        public Boolean put(String key, Boolean value) {
            try {
                getPreferences().setValue(key, value == null ? null : String.valueOf(value));
            } catch (ReadOnlyException e) {
                throw new LiferayException("Unable to store preferences: " + e.getMessage());
            }
            return null;
        }
    };

    @ZkModel
    protected Map<String, Date> preferencesDate = new HashMap<String, Date>()
    {
        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public Date get(Object key) {

            try {
                String value = getPreferences().getValue((String) key, null);
                return value == null ? null : dateFormat.parse(value);
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        public Date put(String key, Date value) {
            try {
                getPreferences().setValue(key, value == null ? null : dateFormat.format(value));
            } catch (ReadOnlyException e) {
                throw new LiferayException("Unable to store preferences: " + e.getMessage());
            }
            return null;
        }
    };

    /**
     * Returns current portlet preferences.
     * 
     * @return portlet preferences
     */
    public PortletPreferences getPreferences()
    {
        return portletPreferences;
    }

    /**
     * Reset all elements.
     */
    public void reset()
    {
        try {
            Enumeration<String> keys = getPreferences().getNames();
            List<String> keysToReset = new ArrayList<String>();
            while(keys.hasMoreElements())
                keysToReset.add(keys.nextElement());
            
            for (String key : keysToReset)
                getPreferences().reset(key);


            // load all bindings
            ZKBinderHelper.getBinder(self).loadAll();
            
        } catch (ReadOnlyException e) {
            throw new LiferayOperationDenied("Preferences are read only, cannot reset.", e);
        }        
    }

    /**
     * Store new preferences
     */
    public void save()
    {
        try {
            getPreferences().store();
        } catch (IOException e) {
            throw new LiferayException("Unable to store preferences: " + e.getMessage());
        } catch (ValidatorException e) {
            throw new LiferayException("Unable to store preferences: " + e.getMessage());
        }
    }

    /**
     * Refresh portlet via javascript.
     */
    public void refreshPortlet()
    {
        String portletBoundary = "#p_p_id_" + portletResource + "_";
        Clients.evalJavaScript("window.parent.Liferay.Portlet.refresh('" + portletBoundary + "')");
    }
    
    /**
     * Close config dialog via javascript (without refresh).
     */
    public void closeDialog()
    {
        Clients.evalJavaScript("var closeButton = top.document.getElementById('closethick');$(closeButton).click();");
    }
}

