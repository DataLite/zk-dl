package cz.datalite.zk.components.button;

import cz.datalite.zk.help.DLI18n;

/**
 * ZK Toolbarbutton extensions.
 *
 * List of extensions:
 * <ul>
 *   <li>i18n</li>
 *   <li>readonly - depends on disabledOnReadonly disable the button if ZKHelper.setReadonly() is set</li>
 *   <li>load image from classpath</li>
 *   <li>autodisable default value = true</li>
 * </ul>
 *
 * @author Michal Pavlusek
 */
public class DLToolbarButton extends DLButton implements DLI18n, org.zkoss.zul.api.Toolbarbutton
{
    public DLToolbarButton() {
        setAutodisable("self");
    }

    public DLToolbarButton(String label) {
        super(label);
        setAutodisable("self");
    }

    public DLToolbarButton(String label, String image) {
        super(label, image);
        setAutodisable("self");
    }
}
