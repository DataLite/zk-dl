package cz.datalite.zkspring.monitor;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

/**
 * Richlet for ZK monitor.
 *
 * This richlet only loads zul page from classpath.
 *
 * @author Jiri Bubnik
 */
public class ZKMonitorRichlet extends GenericRichlet
{
    /**
     * Use richlet to load monitor page from library
     * @param page
     */
    public void service(Page page) {
        try {
            page.setTitle("ZK Monitor");
            java.io.InputStream zulInput = ZKMonitorRichlet.class.getClassLoader().getResourceAsStream("cz/datalite/zkspring/monitor/monitor.zul");
            Reader zulReader = new InputStreamReader(zulInput);
            final Window w = (Window) Executions.createComponentsDirectly(zulReader,"zul",null,new HashMap());
            w.setPage(page);
        } catch (IOException ex) {
             throw new Error("Unable to load monitor zul page.", ex);
        }
    }
}
