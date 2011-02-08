/**
* <p>A library for monitoring session runtime information (active desktops, recent requests, timing, call stack) for your ZK application.
* It is based on ZK PerformanceMeter listener, but provides integration with other frameworks as well.</p>
*
* After monitor is enabled for a session, it collects performance and detail call stack for each request:
* <ul>
* <li>ZK Monitor - ZK PerformanceMeter listener (registered in zk.xml) - it provides basic request timing (client, server, network, javascript on browser) PerformanceMonitor description</li>
* <li>ZK Event listener - intercepts every ZK event and adds event information to call stack with event duration</li>
* <li>Spring AOP interceptor - call stack of spring method invocation (around invoke) with invocation duration</li>
* <li>Toplink essentials logger - adds toplink logs into call stack tree - you can see SQL statements with timing and correct location (e.g. ZK event or Spring bean method)</li>
* <li>LOG4J logger - log appender, you can see any LOG4J log with timing and correct location</li>
* <li>... you can add your own </li>
* </ul>
*
* You don't need to use all these frameworks - only ZK Monitor is mandatory and provides nice view of ZK PerformanceMeter data.
* However to leverage full strength and discover performance bottleneck you may find other frameworks usefull as well.
* <br/><br/>
* For more information go to <a href="http://docs.zkoss.org/wiki/Performance_Monitoring_of_ZK_Applicaiton">ZK Wiki</a>.
*/
package cz.datalite.zkspring.monitor;

