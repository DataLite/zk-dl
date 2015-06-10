package cz.datalite.zkspring.monitor;

import java.io.Serializable;
import java.util.Stack;

/**
 * ZK Statistics object for request statistics via ZK Performance monitor (to get overall request time) and Spring AOP method interceptor
 * (to get method invocation tree and method execution time)
 *
 * @see ZKPerformanceMeter
 * @see ZKSpringPerformanceInterceptor
 *
 * @author Jiri Bubnik
 */
public class ZKRequestMonitor implements Serializable {

    private String contextPath;
    private String desktopId;
    private String requestId;

    private long timeStartAtClient;
    private long timeStartAtServer;
    private long timeCompleteAtServer;
    private long timeRecieveAtClient;
    private long timeCompleteAtClient;

    private ZKRequestMonitorMethod parentInvocation;
    private Stack<ZKRequestMonitorMethod> currentInvocationStack;

    ZKRequestMonitor(String requestId, String desktopId, String contextPath) {
        this.requestId = requestId;
        this.desktopId = desktopId;
        this.contextPath = contextPath;

        parentInvocation = new ZKRequestMonitorMethod("Server request");
        parentInvocation.setStartTime(System.currentTimeMillis());
        currentInvocationStack = new Stack<>();
        currentInvocationStack.add(parentInvocation);
    }

    /**
     * Overall request duration (estimated if not all data are set)
     *
     * @return the time
     */
    public Long getOverallDuration()
    {
        if (timeStartAtClient == 0 || timeCompleteAtClient == 0)
        {
            if (timeStartAtServer != 0 && timeCompleteAtServer != 0)
            {
                return timeCompleteAtServer - timeStartAtServer;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return timeCompleteAtClient - timeStartAtClient;
        }
    }

    /**
     * Client time and server time may not be synchronized, caluclate as overall minus client
     *
     * @return the time
     */
    public Long getNetworkLatency()
    {
        if (timeStartAtClient == 0 || timeCompleteAtClient == 0 || timeStartAtServer == 0 || timeCompleteAtServer == 0)
        {
            return null;
        }
        else
        {
            return (timeCompleteAtClient - timeStartAtClient) - (timeCompleteAtServer - timeStartAtServer);
        }

    }

    /**
     * Browser javascript execution
     *
     * @return Browser javascript execution
     */
    public Long getBrowserExecution()
    {
        if (timeRecieveAtClient == 0 || timeCompleteAtClient == 0)
        {
            return null;
        }
        else
        {
            return (timeCompleteAtClient - timeRecieveAtClient);
        }
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the timeStartAtClient
     */
    public long getTimeStartAtClient() {
        return timeStartAtClient;
    }

    /**
     * @param timeStartAtClient the timeStartAtClient to set
     */
    public void setTimeStartAtClient(long timeStartAtClient) {
        this.timeStartAtClient = timeStartAtClient;
    }

    /**
     * @return the timeStartAtServer
     */
    public long getTimeStartAtServer() {
        return timeStartAtServer;
    }

    /**
     * @param timeStartAtServer the timeStartAtServer to set
     */
    public void setTimeStartAtServer(long timeStartAtServer) {
        this.timeStartAtServer = timeStartAtServer;
    }

    /**
     * @return the timeCompleteAtServer
     */
    public long getTimeCompleteAtServer() {
        return timeCompleteAtServer;
    }

    /**
     * @param timeCompleteAtServer the timeCompleteAtServer to set
     */
    public void setTimeCompleteAtServer(long timeCompleteAtServer) {
        this.timeCompleteAtServer = timeCompleteAtServer;
    }

    /**
     * @return the timeRecieveAtClient
     */
    public long getTimeRecieveAtClient() {
        return timeRecieveAtClient;
    }

    /**
     * @param timeRecieveAtClient the timeRecieveAtClient to set
     */
    public void setTimeRecieveAtClient(long timeRecieveAtClient) {
        this.timeRecieveAtClient = timeRecieveAtClient;
    }

    /**
     * @return the timeCompleteAtClient
     */
    public long getTimeCompleteAtClient() {
        return timeCompleteAtClient;
    }

    /**
     * @param timeCompleteAtClient the timeCompleteAtClient to set
     */
    public void setTimeCompleteAtClient(long timeCompleteAtClient) {
        this.timeCompleteAtClient = timeCompleteAtClient;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getDesktopId() {
        return desktopId;
    }

    public void setDesktopId(String desktopId) {
        this.desktopId = desktopId;
    }

    public ZKRequestMonitorMethod getParentInvocation() {
        return parentInvocation;
    }

    public Stack<ZKRequestMonitorMethod> getCurrentInvocationStack() {
        return currentInvocationStack;
    }

}
