package cz.datalite.zk.liferay.mock;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * Check if Liferay is available and if not, init main services with mock.
 *
 * @author Jiri Bubnik
 */
public class LiferayMock
{
    protected CompanyMockFactory companyMockFactory;
    protected UserMockFactory userMockFactory;
    protected ThemeDisplayMockFactory themeDisplayMockFactory;

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
        if (isLiferayRunning())
            return;

        PortalMockFactory portalMockFactory = new PortalMockFactory(companyMockFactory, userMockFactory);
        portalMockFactory.initLiferay();
    }
}
