package cz.datalite.zkspring.monitor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts calls to methods via. Spring AOP and binds call invocation tree and duration to ZK request performance monitor
 * 
 * @see ZKPerformanceMeter
 * @see ZKRequestMonitor
 * @see ZKRequestMonitorMethod
 *
 * Example Spring setup:
 *  <!-- ZK Spring performance interceptor -->
 *  <bean name="springPerformanceInterceptor" class="cz.datalite.zkspring.monitor.ZKSpringPerformanceInterceptor" />
 *
 *  <!-- Create the proxy bean that returns AOP'd varieties of required layeres -->
 *   <bean name="proxyCreator" class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
 *     	<property name="beanNames" value="*Controller, *Service, *DAO"/>
 *     	<property name="interceptorNames">
 *     		<list>
 *     			<value>springPerformanceInterceptor</value>
 *     		</list>
 *     	</property>
 *   </bean>
 *
 *
 * @author Jiri Bubnik
 */
public class ZKSpringPerformanceInterceptor implements MethodInterceptor  {

    /**
     * Builds invocation tree in current request statistics object and set statistics
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();        

        ZKRequestMonitor reqStats = ZKPerformanceMeter.getCurrentRequestStatistics();
        ZKRequestMonitorMethod reqMethod = null;
        if (reqStats != null)
        {
            String name = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
            ZKRequestMonitorMethod reqMethodParent = reqStats.getCurrentInvocationStack().peek();
            reqMethod = new ZKRequestMonitorMethod(name);
            reqMethod.setStartTime(start);

            reqMethodParent.getInvokationTree().add(reqMethod);
            reqStats.getCurrentInvocationStack().push(reqMethod);
        }

        try
        {
            return invocation.proceed();
        }
        finally
        {
            if (reqStats != null)
            {
                reqStats.getCurrentInvocationStack().pop();
                reqMethod.setDuration(System.currentTimeMillis() - start);
            }
        }
    }
}
