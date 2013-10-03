/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay;


import cz.datalite.helpers.ZKHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Script;

import java.util.List;

/**
 * Helper methods for Liferay with ZK
 */
public abstract class ZKLiferayHelper {

    /**
     * Overide client resize method to use current position in page and setup height as to
     * span the resto of the window up to bottom. Useful e.g. for control panel.
     *
     * @param component component to resize
     */
    public static void clientCalculateComponentHeigthToWindowBottom(Component component)
    {
        component.setWidgetOverride("_resize",
                "   function (value) {\n" +
                        "       var top = jq(this).position().top;\n" +
                        "       var windowHeight = jq(window).height();\n" +
                        "       var height = windowHeight - top - 20;\n" +
                        "       this.setHeight(height + 'px');\n" +
                        "       this.$_resize(value);\n" +
                        "   }");
    }


    /**
     * Resize height of body element via jQuery. Use this function to set size of ZK page
     * in IFrame (usefull for configuration dialog).
     *
     * @param component component to get page of javascript
     * @param size new in pixels or body
     * @param percent true if size is in percent to screen height
     */
    public static void clientResizeBodyHeight(Component component, int size, boolean percent) {
        StringBuilder js = new StringBuilder(" jq(function() {\n");
        js.append("   var body = jq('body'); body.css('padding', 0);\n");
        if (percent)
        {
            js.append("   var windowHeight = jq(window.top).height();\n");
            js.append("   body.height(windowHeight*");
            js.append(size);
            js.append("/100);\n");
        }
        else
        {
            js.append("   body.height(");
            js.append(size);
            js.append(");\n");
        }
        js.append(" });");

        Script script = new Script();
        script.setContent(js.toString());
        script.setPage(component.getPage());
    }

    /**
     * Resize body of page according to portlet body element. Use to resize Lifray configuration
     * dialog iframe to actual page size.
     *
     * @param component component
     */
    public static void clientResizeBodyHeightAuto(Component component)
    {
        Script script = new Script();
        script.setContent(
                "zk.afterMount(function () {\n" +
                "  var body = jq('body'); body.css('padding', 0);\n" +
                "  var height = jq('.portlet-body').last().height();\n" +
                "  body.height(height);" +
                "});");
        script.setPage(component.getPage());
    }

    /**
     * Typical use of ZK administration portlet in Liferay 6.1.
     *   If the portlet is in control panel, it should span height up to the bottom of the window.
     *   If the portlet is in normal page, we need to set exact size.
     *
     * @param component root component - we find borderlayout by type
     * @param service liferay service to access layout type
     */
    public static void clientResizeBorderlayoutControllPanelOrPortlet(Component component, DLLiferayService service)
    {
        Borderlayout borderlayout = ZKHelper.findChildByClass(component, Borderlayout.class);
        if (borderlayout == null)
            throw new IllegalStateException("Borderlayout component not found.");

        if (service.getThemeDisplay().getLayout().isTypeControlPanel())
            ZKLiferayHelper.clientCalculateComponentHeigthToWindowBottom(borderlayout);
        else
            borderlayout.setHeight("500px");
    }
}
