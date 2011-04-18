package cz.datalite.zk.liferay.mock;

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portlet.*;
import cz.datalite.zk.liferay.DLLiferayService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.portlet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet filter to mock portal/portlet class (not used).
 *
 * The reason behind this class was to allow usage of liferay tags in JSP in mocked mode. Albait, this
 * attempt was unsuccessful, there is too much programming to do.
 *
 * When you use this filter, Liferay tags do not throw NPE, but there are unresloved problems with CSS,
 * portal address resolution etc. If anybody makes this work, please shere it back to me :-).
 *
 *   Add this filter in web.xml
 *    <filter>
 *        <filter-name>LiferayMockFilter</filter-name>
 *        <filter-class>cz.datalite.zk.liferay.mock.LiferayMockFilter</filter-class>
 *    </filter>
 *    <filter-mapping>
 *        <filter-name>LiferayMockFilter</filter-name>
 *        <url-pattern>/*</url-pattern>
 *    </filter-mapping>
 *
 */
public class LiferayMockFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    javax.servlet.FilterChain filterChain) throws ServletException, IOException {

        final DLLiferayService dlLiferayService = lookupLiferayService();

        request.setAttribute(WebKeys.THEME_DISPLAY, dlLiferayService.getThemeDisplay());
        request.setAttribute(PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE);

        final Portlet portlet = new PortletImpl();
        portlet.setPortletName("MockPortlet");
        portlet.setPortletId("MockPortlet");
        portlet.setPortletApp(new PortletAppImpl("servletContext"));
        final PortletContext portletContext = new PortletContextImpl(portlet, getServletContext());
        PortletConfig portletConfig = new PortletConfigImpl(portlet, portletContext);
        request.setAttribute(JavaConstants.JAVAX_PORTLET_CONFIG, portletConfig);

        final PortletPreferences preferences = new PortletPreferencesImpl();
        final InvokerPortlet invokerPortlet = new InvokerPortletImpl();

        final PortletRequestImpl portletRequest = new RenderRequestImpl() {{
             init(request, portlet, invokerPortlet, portletContext, WindowState.NORMAL, PortletMode.VIEW,
                     preferences, 0l);
        }};
        request.setAttribute(JavaConstants.JAVAX_PORTLET_REQUEST, portletRequest);

        PortletResponse portletResponse = new RenderResponseImpl() {{
            init(portletRequest, response, portlet.getPortletName(), dlLiferayService.getCompanyId(), 0l);
        }};
        request.setAttribute(JavaConstants.JAVAX_PORTLET_RESPONSE, portletResponse);

        filterChain.doFilter(request, response);
    }

    /**
     * Look up the EntityManagerFactory that this filter should use.
     * The default implementation looks for a bean with the DLLiferayService class
     * in Spring's root application context.
     *
     * @return the Liferay Service to use
     */
    protected DLLiferayService lookupLiferayService() {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        return wac.getBean(DLLiferayService.class);
    }

}

// Example maven pom.xml to set up dependencies and jetty plugin
//    <dependencies>
//        <dependency>
//            <!-- docasne pro testovani nove verze ZKLiferay -->
//            <groupId>cz.datalite.zk-dl</groupId>
//            <artifactId>ZKLiferay</artifactId>
//            <version>1.1-SNAPSHOT</version>
//        </dependency>
//
//        <dependency>
//            <groupId>javax.portlet</groupId>
//            <artifactId>portlet-api</artifactId>
//            <version>2.0</version>
//        </dependency>
//
//        <dependency>
//             <groupId>javax.servlet</groupId>
//             <artifactId>jstl</artifactId>
//             <version>1.1.2</version>
//         </dependency>
//
//         <dependency>
//             <groupId>taglibs</groupId>
//             <artifactId>standard</artifactId>
//             <version>1.1.2</version>
//         </dependency>
//
//         <dependency>
//             <groupId>displaytag</groupId>
//             <artifactId>displaytag</artifactId>
//             <version>1.2</version>
//          </dependency>
//
//
//         <dependency>
//             <groupId>struts</groupId>
//             <artifactId>struts</artifactId>
//             <version>1.2.9</version>
//             <exclusions>
//                 <exclusion>
//                     <groupId>antlr</groupId>
//                     <artifactId>antlr</artifactId>
//                 </exclusion>
//             </exclusions>
//          </dependency>
//
//        <dependency>
//            <groupId>javax.ccpp</groupId>
//            <artifactId>ccpp</artifactId>
//            <version>1.0</version>
//        </dependency>
//
//        <dependency>
//            <groupId>commons-fileupload</groupId>
//            <artifactId>commons-fileupload</artifactId>
//            <version>1.2.1</version>
//        </dependency>
//
//        <dependency>
//            <groupId>commons-math</groupId>
//            <artifactId>commons-math</artifactId>
//            <version>1.2</version>
//        </dependency>
//
//    </dependencies>
//
//    <build>
//        <plugins>
//                <plugin>
//                <groupId>org.mortbay.jetty</groupId>
//                <artifactId>maven-jetty-plugin</artifactId>
//                <version>6.1.12</version>
//                <configuration>
//                    <stopKey>${artifactId}</stopKey>
//                    <stopPort>9091</stopPort>
//
//                    <webAppConfig>
//                        <baseResource implementation="org.mortbay.resource.ResourceCollection">
//                          <!--resources>src/main/webapp,c:\liferay\bundles\tomcat-6.0.29\webapps\ROOT\</resources-->
//                            <resources>src/main/webapp</resources>
//                        </baseResource>
//                        <tempDirectory>${project.build.directory}/work</tempDirectory>
//
//                    </webAppConfig>
//                    <connectors>
//                        <connector implementation="org.mortbay.jetty.nio.BlockingChannelConnector">
//                            <port>9092</port>
//                            <maxIdleTime>60000</maxIdleTime>
//                        </connector>
//                    </connectors>
//                </configuration>
//            </plugin>
//        </plugins>
//    </build>
