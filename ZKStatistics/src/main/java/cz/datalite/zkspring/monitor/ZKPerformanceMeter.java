package cz.datalite.zkspring.monitor;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.PerformanceMeter;

import java.util.HashMap;
import java.util.Map;

/**
 * Performance utility class
 *
 * http://www.zkoss.org/smalltalks/zk3.5/  - Performance monitor
 *
 * It binds session performance monitor object to the session and collects statistics across one execution.
 *
 * @author Jiri Bubnik
 */
public class ZKPerformanceMeter implements PerformanceMeter
{
    private static ZKPerformanceEventInterceptor zkPerformanceEventInterceptor  = new ZKPerformanceEventInterceptor();

    /**
     * Returns current request statistics object set by ZK performance meter
     *
     * This method is utilized by other performance monitor (e.g. Spring AOP method interceptor) to add statistics to the request
     *
     * Returns null, if no request statistics object is availabele
     */
    public static ZKRequestMonitor getCurrentRequestStatistics()
    {
        return (Executions.getCurrent()  == null) ? null :
              (ZKRequestMonitor) Executions.getCurrent().getDesktop().getSession().getAttribute("RequestStatisticsCurrentObject");
    }

    /**
     * Returns actual statistics object identified by exection id
     * At first statistics call in request new statistics object is created and set into RequestStatisticsMap session variable. Upon subsequent
     * calls in request the same object is returned.
     *
     * This object is set to RequestStatisticsCurrentObject session variable as well for use of other statistics methods. Use getCurrentRequestStatistics()
     * static method to use this session variable.
     *
     * Spring AOP is utilized to get method invocation tree of this request (see ZKSpringPerformanceInterceptor)
     *
     * @param requestId request identifier
     * @param exec ZK exekuce
     * @return objekt statistik
     */
    public static ZKRequestMonitor getCurrentRequestStatistics(String requestId, Execution exec)
    {
        ZKRequestMonitor ret;

        Map<String, ZKRequestMonitor> map = (Map<String, ZKRequestMonitor>) exec.getDesktop().getSession().getAttribute("RequestStatisticsMap");
        
        if (map == null)
        {
            map = new HashMap<String, ZKRequestMonitor>();
            exec.getDesktop().getSession().setAttribute("RequestStatisticsMap", map);
        }

        if (map.containsKey(requestId))
        {
            ret = map.get(requestId);
        }
        else
        {
            ret = new ZKRequestMonitor(requestId, exec.getDesktop().getId(), exec.getDesktop().getRequestPath());
            map.put(requestId, ret);

            exec.getDesktop().removeListener(zkPerformanceEventInterceptor);
            exec.getDesktop().addListener(zkPerformanceEventInterceptor);
        }

        exec.getDesktop().getSession().setAttribute("RequestStatisticsCurrentObject", ret);

        return ret;
    }

    public void requestStartAtClient(String requestId, Execution exec, long time) {
        if (isStatisticsEnabled(exec)) {
            getCurrentRequestStatistics(requestId, exec).setTimeStartAtClient(time);
        }
    }

    public void requestCompleteAtClient(String requestId, Execution exec, long time) {
        if (isStatisticsEnabled(exec)) {
            getCurrentRequestStatistics(requestId, exec).setTimeCompleteAtClient(time);
        }
    }

    public void requestReceiveAtClient(String requestId, Execution exec, long time) {
        if (isStatisticsEnabled(exec)) {
            getCurrentRequestStatistics(requestId, exec).setTimeRecieveAtClient(time);
        }
    }

    public void requestStartAtServer(String requestId, Execution exec, long time) {
        if (isStatisticsEnabled(exec)) {
            getCurrentRequestStatistics(requestId, exec).setTimeStartAtServer(time);
        }
    }

    public void requestCompleteAtServer(String requestId, Execution exec, long time) {
        if (isStatisticsEnabled(exec)) 
        {
            ZKRequestMonitor stats = getCurrentRequestStatistics(requestId, exec);
            stats.setTimeCompleteAtServer(time);
            long methodInvocationDuration = 0;
            for (ZKRequestMonitorMethod method : stats.getParentInvocation().getInvokationTree())
            {
                methodInvocationDuration += method.getDuration();
            }
            stats.getParentInvocation().setDuration(methodInvocationDuration);
        }
    }

    /**
     * Returns true if statistics collection is enabled
     *
     * @param exec Current execution
     */
    private boolean isStatisticsEnabled(Execution exec)
    {
        return 
                exec.getDesktop().getSession().getAttribute("RequestStatisticsEnabled") != null &&
                !exec.getDesktop().getRequestPath().contains("ZKMonitor") ;
    }
}
