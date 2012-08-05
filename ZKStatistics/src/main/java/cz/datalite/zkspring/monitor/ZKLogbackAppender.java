package cz.datalite.zkspring.monitor;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Custom appender to use with ZK Request Statics Add log to request invocation
 * tree
 *
 * Just declare it in log4j.properties:
 * log4j.appender.ZKAppender=cz.datalite.zkspring.monitor.ZKLog4jAppender
 *
 * @author Jiri Bubnik
 */
public class ZKLogbackAppender<T extends LoggingEvent> extends AppenderBase<T> {

    @Override
    protected void append(T loggingEvent) {
        ZKRequestMonitor reqStats = ZKPerformanceMeter.getCurrentRequestStatistics();

        if (reqStats != null) {
            StringBuilder bufferName = new StringBuilder();
            bufferName.append(getName());
            bufferName.append(' ');
            bufferName.append('[');
            bufferName.append(loggingEvent.getLevel().toString());
            bufferName.append(']');
            bufferName.append(' ');
            bufferName.append(loggingEvent.getFormattedMessage());

            ZKRequestMonitorMethod reqMethodParent = reqStats.getCurrentInvocationStack().peek();
            ZKRequestMonitorMethod reqMethod = new ZKRequestMonitorMethod(bufferName.toString());
            reqMethod.setStartTime(loggingEvent.getTimeStamp());
            reqMethodParent.getInvokationTree().add(reqMethod);
        }
    }
}
