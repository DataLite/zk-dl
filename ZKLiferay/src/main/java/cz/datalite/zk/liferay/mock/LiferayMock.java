package cz.datalite.zk.liferay.mock;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

/**
 * Check if Liferay is available and if not, init main services with mock.
 *
 * @author Jiri Bubnik
 */
public class LiferayMock implements ServletContextAware
{
    /** Logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger( LiferayMock.class );
     
    protected ServletContext servletContext;

    protected CompanyMockFactory companyMockFactory;
    protected UserMockFactory userMockFactory;
    protected ThemeDisplayMockFactory themeDisplayMockFactory;
    protected Map<String, String> liferayRoleMapper;


    /**
     * Init mock to the default implementations
     */
    public LiferayMock()
    {
        if (!isLiferayRunning())
        {
            companyMockFactory = new CompanyMockFactory();
            userMockFactory = new UserMockFactory();
            themeDisplayMockFactory = new ThemeDisplayMockFactory();
        }
    }

    public void setCompanyMockFactory(CompanyMockFactory companyMockFactory) {
        this.companyMockFactory = companyMockFactory;
    }

    public void setUserMockFactory(UserMockFactory userMockFactory) {
        this.userMockFactory = userMockFactory;
    }

    public ThemeDisplayMockFactory getThemeDisplayMockFactory() {
        return themeDisplayMockFactory;
    }

    public void setThemeDisplayMockFactory(ThemeDisplayMockFactory themeDisplayMockFactory) {
        this.themeDisplayMockFactory = themeDisplayMockFactory;
    }

    /**
     * Returns a role mapper based on liferay-portlet.xml
     */
    public Map<String, String> getLiferayRoleMapper() {
        return liferayRoleMapper;
    }

    /**
     * Returns true if Liferay is really running (not only a mock)
     *
     * @return true if Liferay is really running
     */
    public boolean isLiferayRunning()
    {
        // this property must not be mocked - to return correct value even after mocking is finished
        return PortalBeanLocatorUtil.getBeanLocator() != null;
    }

    /**
     * Initialize mocks for liferay (only if Liferay Portal is not available)
     */
    public void initLiferay()
    {
        // check if Liferay is available and skip mocking
        if (isLiferayRunning()) {
            return;
        }

        PortalMockFactory portalMockFactory = new PortalMockFactory(companyMockFactory, userMockFactory);
        portalMockFactory.initLiferay();

        InputStream liferayPortletXml = servletContext.getResourceAsStream("/WEB-INF/liferay-portlet.xml");
        if (liferayPortletXml == null)
        {
            LOGGER.warn( "The file /WEB-INF/liferay-portlet.xml does not exists, role mapper will not be available" );
            liferayRoleMapper = new HashMap<String, String>();
        }
        else {
            liferayRoleMapper = new LiferayRoleMapper(liferayPortletXml);
        }
    }

    // remember the servlet context (injected via Spring)
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
