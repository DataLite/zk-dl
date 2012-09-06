package cz.datalite.zkspring.monitor;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.EventInterceptor;

/**
 * ZK Event interceptor
 *
 * Intercepts each event befor start and after finish and add event data to request monitor tree
 * 
 * @author Jiri Bubnik
 */
public class ZKPerformanceEventInterceptor implements EventInterceptor
{
    long time;

    public Event beforeSendEvent(Event event) {
        return event;
    }

    public Event beforePostEvent(Event event) {
        return event;
    }

    public Event beforeProcessEvent(Event event) {
        ZKRequestMonitor reqStats = ZKPerformanceMeter.getCurrentRequestStatistics();

        if (reqStats != null)
        {
            String name = event.toString();
            if (event.getData() != null)
            {
                name += " -> " + event.getData();
            }
            ZKRequestMonitorMethod reqMethodParent = reqStats.getCurrentInvocationStack().peek();
            ZKRequestMonitorMethod reqMethod = new ZKRequestMonitorMethod(name);
            reqMethod.setStartTime(System.currentTimeMillis());

            reqMethodParent.getInvokationTree().add(reqMethod);
            reqStats.getCurrentInvocationStack().push(reqMethod);
        }

        return event;
    }

    public void afterProcessEvent(Event event) {
        ZKRequestMonitor reqStats = ZKPerformanceMeter.getCurrentRequestStatistics();

        if (reqStats != null)
        {
            ZKRequestMonitorMethod reqMethod = reqStats.getCurrentInvocationStack().pop();
            reqMethod.setDuration(System.currentTimeMillis() - reqMethod.getStartTime());
        }
    }

}
