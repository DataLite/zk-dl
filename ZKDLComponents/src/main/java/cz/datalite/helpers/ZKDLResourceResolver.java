package cz.datalite.helpers;

import org.zkoss.lang.Library;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Resolve resources such as images and css from path pro theme.
 */
public class ZKDLResourceResolver {

    private ZKDLResourceResolver() {
    }

    /** configuration key in property files */
    private static final String CONFIG = "zk-dl.themeDir";

    protected static final String CONST_DEFAULT_ICON_PATH = "~./js/dlzklib/img/";
    protected static final String CONST_DEFAULT_RESOURCE_PATH = "~./js/dlzklib/resource/";

    /** caching is enabled / disabled */
    private static String themeDir = null;

    static {
        themeDir = Library.getProperty(CONFIG);
        // normalize
        if (themeDir != null) {
            if (!themeDir.endsWith("/"))
                themeDir = themeDir + "/";
            if (!themeDir.startsWith("~./"))
                themeDir = "~./js/" + themeDir;
        }
    }


    /**
     * Resolve an image to a theme path. If theme library property zk-dl.themeDir is not set, use
     * default images included in library jar file.
     *
     * @param image image name (e.g. menu_items_big.png)
     * @return full path (e.g. /theme/dt_gray/dlzklib/js/img/menu_items_big.png)
     */
    public static String resolveImage(String image) {
        if (themeDir == null)
            return CONST_DEFAULT_ICON_PATH + image;
        else
            return ServletFns.resolveThemeURL(themeDir + "img/" + image);
    }

    /**
     * Resolve resource from a theme. If theme library property zk-dl.themeDir is not set, or
     * resource does not exist in the theme, use resources included in jar file.
     *
     * @param resource resource name (e.g.
     * @return
     */
    public static InputStream resolveResource(String resource) {

        String fullResourcePath = null;

        if (themeDir != null) {
            fullResourcePath = ServletFns.resolveThemeURL(themeDir + "resource/" + resource);
        }

        // locate in webapp (servlet context) and
        WebApp webApp = Executions.getCurrent().getDesktop().getWebApp();

        // try theme resource, if not found use library resource
        InputStream result = null;
        if (fullResourcePath != null)
            result = webApp.getResourceAsStream(fullResourcePath);

        if (result == null)
            result = webApp.getResourceAsStream(CONST_DEFAULT_RESOURCE_PATH + resource);

        return result;
    }

    /**
     * Convenience method to resolve page and create components via Executions.createComponentsDirectly.
     * @param page page to resolve
     * @param args args for component creation
     * @return root component
     */
    public static Component resolveAndCreateComponents(String page, Component parent, Map args) {
        try {
            return org.zkoss.zk.ui.Executions.createComponentsDirectly(
                new InputStreamReader(ZKDLResourceResolver.resolveResource(page)), "zul", parent, args);
        } catch (IOException e) {
            throw new IllegalArgumentException("Page not found "+ page);
        }
    }
}
