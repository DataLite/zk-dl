package cz.datalite.zkspring.monitor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.zul.DefaultTreeNode;

/**
 * Request invocation tree
 *
 * @author Jiri Bubnik
 */
public class ZKRequestMonitorMethod extends DefaultTreeNode implements Serializable {
    String name;

    long startTime;
    long duration;

    public ZKRequestMonitorMethod(String name)
    {
        super(name, new LinkedList());
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<ZKRequestMonitorMethod> getInvokationTree() {
        return this.getChildren();
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}
