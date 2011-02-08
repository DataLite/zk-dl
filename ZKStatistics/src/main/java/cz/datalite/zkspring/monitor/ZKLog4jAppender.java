package cz.datalite.zkspring.monitor;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Custom appender to use with ZK Request Statics
 * Add log to request invocation tree
 *
 * Just declare it in log4j.properties:
 * log4j.appender.ZKAppender=cz.datalite.zkspring.monitor.ZKLog4jAppender
 *
 * @author Jiri Bubnik
 */
public class ZKLog4jAppender extends AppenderSkeleton
{
   protected void append(LoggingEvent loggingEvent) {
        ZKRequestMonitor reqStats = ZKPerformanceMeter.getCurrentRequestStatistics();

        if (reqStats != null)
        {
            StringBuffer bufferName = new StringBuffer();
            bufferName.append(getName());
            bufferName.append(' ');
            bufferName.append('[');
            bufferName.append(loggingEvent.getLevel().toString());
            bufferName.append(']');
            bufferName.append(' ');
            bufferName.append(loggingEvent.getRenderedMessage());

            ZKRequestMonitorMethod reqMethodParent = reqStats.getCurrentInvocationStack().peek();
            ZKRequestMonitorMethod reqMethod = new ZKRequestMonitorMethod(bufferName.toString());
            reqMethod.setStartTime(loggingEvent.timeStamp);
            reqMethodParent.getInvokationTree().add(reqMethod);
        }
   }

   public boolean requiresLayout() {
       return false;
   }
   public void close() {
   }
}
