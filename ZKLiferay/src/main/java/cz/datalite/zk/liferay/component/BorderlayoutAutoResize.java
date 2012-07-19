/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.component;

import org.zkoss.zul.Borderlayout;

/**
 * Borderlayout with widget overide to set width/height according to enclosing window.
 * It is needed to behave correctly inside iframe, otherwise the layout has zero height.
 */
public class BorderlayoutAutoResize extends Borderlayout {
    public BorderlayoutAutoResize() {
        setWidgetOverride("_resize",
                "function (value) {\n" +
                "  this.setWidth(jq(window).width() + 'px');\n" +
                "  this.setHeight((jq(window).height()) + 'px');\n" +
                "  this.$_resize(value);\n" +
                "}"
        );
    }
}
