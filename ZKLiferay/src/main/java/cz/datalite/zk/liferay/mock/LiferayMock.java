package cz.datalite.zk.liferay.mock;

import java.util.Arrays;

import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.User;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Group;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.dao.orm.OrderFactory;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.service.ClusterGroupLocalServiceUtil;
import com.liferay.portal.service.BrowserTrackerLocalService;
import com.liferay.portal.service.BrowserTrackerLocalServiceUtil;
import com.liferay.portal.service.ClusterGroupLocalService;
import com.liferay.portal.service.CountryService;
import com.liferay.portal.service.CountryServiceUtil;
import com.liferay.portal.service.EmailAddressLocalService;
import com.liferay.portal.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.service.ImageLocalService;
import com.liferay.portal.service.ImageLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalService;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.LayoutPrototypeLocalService;
import com.liferay.portal.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalService;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.service.LayoutTemplateLocalService;
import com.liferay.portal.service.LayoutTemplateLocalServiceUtil;
import com.liferay.portal.service.LockLocalService;
import com.liferay.portal.service.LockLocalServiceUtil;
import com.liferay.portal.service.MembershipRequestLocalService;
import com.liferay.portal.service.MembershipRequestLocalServiceUtil;
import com.liferay.portal.service.OrgLaborLocalService;
import com.liferay.portal.service.OrgLaborLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalService;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.PasswordPolicyLocalService;
import com.liferay.portal.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.service.PasswordPolicyRelLocalService;
import com.liferay.portal.service.PasswordPolicyRelLocalServiceUtil;
import com.liferay.portal.service.PasswordTrackerLocalService;
import com.liferay.portal.service.PasswordTrackerLocalServiceUtil;
import com.liferay.portal.service.PermissionLocalService;
import com.liferay.portal.service.PermissionLocalServiceUtil;
import com.liferay.portal.service.PhoneLocalService;
import com.liferay.portal.service.PhoneLocalServiceUtil;
import com.liferay.portal.service.PluginSettingLocalService;
import com.liferay.portal.service.PluginSettingLocalServiceUtil;
import com.liferay.portal.service.PortalLocalService;
import com.liferay.portal.service.PortalLocalServiceUtil;
import com.liferay.portal.service.PortletItemLocalService;
import com.liferay.portal.service.PortletItemLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalService;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.QuartzLocalService;
import com.liferay.portal.service.QuartzLocalServiceUtil;
import com.liferay.portal.service.ReleaseLocalService;
import com.liferay.portal.service.ReleaseLocalServiceUtil;
import com.liferay.portal.service.ResourceActionLocalService;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourceCodeLocalService;
import com.liferay.portal.service.ResourceCodeLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalService;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalService;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceComponentLocalService;
import com.liferay.portal.service.ServiceComponentLocalServiceUtil;
import com.liferay.portal.service.ShardLocalService;
import com.liferay.portal.service.ShardLocalServiceUtil;
import com.liferay.portal.service.SubscriptionLocalService;
import com.liferay.portal.service.SubscriptionLocalServiceUtil;
import com.liferay.portal.service.TeamLocalService;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.service.ThemeLocalService;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.service.TicketLocalService;
import com.liferay.portal.service.TicketLocalServiceUtil;
import com.liferay.portal.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalService;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserIdMapperLocalService;
import com.liferay.portal.service.UserIdMapperLocalServiceUtil;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserTrackerLocalService;
import com.liferay.portal.service.UserTrackerLocalServiceUtil;
import com.liferay.portal.service.UserTrackerPathLocalService;
import com.liferay.portal.service.UserTrackerPathLocalServiceUtil;
import com.liferay.portal.service.WebDAVPropsLocalService;
import com.liferay.portal.service.WebDAVPropsLocalServiceUtil;
import com.liferay.portal.service.WebsiteLocalService;
import com.liferay.portal.service.WebsiteLocalServiceUtil;
import com.liferay.portal.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.AddressLocalService;
import com.liferay.portal.service.AccountLocalService;
import com.liferay.portal.service.AccountLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalService;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.ContactLocalService;
import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.GroupLocalService;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalService;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactory;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactory;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactory;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;

import static org.mockito.Mockito.*;

/**
 *
 * @author Jiri Bubnik
 */
public class LiferayMock
{
    protected CompanyMockFactory companyMockFactory = new CompanyMockFactory();
    protected UserMockFactory userMockFactory = new UserMockFactory();


    /**
     * Initialize all mockes for Liferay.
     *
     * If you need refresh basic mock configuration, just call this method - it sets all services
     * with a fresh instance and then it initializes basic values.
     */
    public void initLiferay()
    {
        try {
            // classloader is the web app
            PortalClassLoaderUtil.setClassLoader(LiferayMock.class.getClassLoader());

            mockServices();
            mockDynamicQuery();
            fillTestData();

        } catch (PortalException ex) {
            throw new RuntimeException(ex);
        } catch (SystemException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Normaly Liferay regsiters service in utility class via Spring.
     *
     * For each utility class, we create a mock service with RETURNS_MOCKS (create not null objects for all functions).
     * We try to avoid as much NPE as possible.
     *
     * When some service call needs to be implemented, just add something like:
     *   when(CompanyLocalServiceUtil.getService().getCompany(companyId)).thenReturn(company);
     */
    public void mockServices()
    {
        new AccountLocalServiceUtil().setService(mock(AccountLocalService.class, RETURNS_MOCKS));
        new AddressLocalServiceUtil().setService(mock(AddressLocalService.class, RETURNS_MOCKS));
        new BrowserTrackerLocalServiceUtil().setService(mock(BrowserTrackerLocalService.class, RETURNS_MOCKS));
        new ClusterGroupLocalServiceUtil().setService(mock(ClusterGroupLocalService.class, RETURNS_MOCKS));
        new CompanyLocalServiceUtil().setService(mock(CompanyLocalService.class, RETURNS_MOCKS));
        new ContactLocalServiceUtil().setService(mock(ContactLocalService.class, RETURNS_MOCKS));
        new CountryServiceUtil().setService(mock(CountryService.class, RETURNS_MOCKS));
        new EmailAddressLocalServiceUtil().setService(mock(EmailAddressLocalService.class, RETURNS_MOCKS));
        new GroupLocalServiceUtil().setService(mock(GroupLocalService.class, RETURNS_MOCKS));
        new ImageLocalServiceUtil().setService(mock(ImageLocalService.class, RETURNS_MOCKS));
        new LayoutLocalServiceUtil().setService(mock(LayoutLocalService.class, RETURNS_MOCKS));
        new LayoutPrototypeLocalServiceUtil().setService(mock(LayoutPrototypeLocalService.class, RETURNS_MOCKS));
        new LayoutSetLocalServiceUtil().setService(mock(LayoutSetLocalService.class, RETURNS_MOCKS));
        new LayoutSetPrototypeLocalServiceUtil().setService(mock(LayoutSetPrototypeLocalService.class, RETURNS_MOCKS));
        new LayoutTemplateLocalServiceUtil().setService(mock(LayoutTemplateLocalService.class, RETURNS_MOCKS));
        new LockLocalServiceUtil().setService(mock(LockLocalService.class, RETURNS_MOCKS));
        new MembershipRequestLocalServiceUtil().setService(mock(MembershipRequestLocalService.class, RETURNS_MOCKS));
        new OrgLaborLocalServiceUtil().setService(mock(OrgLaborLocalService.class, RETURNS_MOCKS));
        new OrganizationLocalServiceUtil().setService(mock(OrganizationLocalService.class, RETURNS_MOCKS));
        new PasswordPolicyLocalServiceUtil().setService(mock(PasswordPolicyLocalService.class, RETURNS_MOCKS));
        new PasswordPolicyRelLocalServiceUtil().setService(mock(PasswordPolicyRelLocalService.class, RETURNS_MOCKS));
        new PasswordTrackerLocalServiceUtil().setService(mock(PasswordTrackerLocalService.class, RETURNS_MOCKS));
        new PermissionLocalServiceUtil().setService(mock(PermissionLocalService.class, RETURNS_MOCKS));
        new PhoneLocalServiceUtil().setService(mock(PhoneLocalService.class, RETURNS_MOCKS));
        new PluginSettingLocalServiceUtil().setService(mock(PluginSettingLocalService.class, RETURNS_MOCKS));
        new PortalLocalServiceUtil().setService(mock(PortalLocalService.class, RETURNS_MOCKS));
        new PortletItemLocalServiceUtil().setService(mock(PortletItemLocalService.class, RETURNS_MOCKS));
        new PortletPreferencesLocalServiceUtil().setService(mock(PortletPreferencesLocalService.class, RETURNS_MOCKS));
        new QuartzLocalServiceUtil().setService(mock(QuartzLocalService.class, RETURNS_MOCKS));
        new ReleaseLocalServiceUtil().setService(mock(ReleaseLocalService.class, RETURNS_MOCKS));
        new ResourceActionLocalServiceUtil().setService(mock(ResourceActionLocalService.class, RETURNS_MOCKS));
        new ResourceCodeLocalServiceUtil().setService(mock(ResourceCodeLocalService.class, RETURNS_MOCKS));
        new ResourcePermissionLocalServiceUtil().setService(mock(ResourcePermissionLocalService.class, RETURNS_MOCKS));
        new RoleLocalServiceUtil().setService(mock(RoleLocalService.class, RETURNS_MOCKS));
        new ServiceComponentLocalServiceUtil().setService(mock(ServiceComponentLocalService.class, RETURNS_MOCKS));
        new ShardLocalServiceUtil().setService(mock(ShardLocalService.class, RETURNS_MOCKS));
        new SubscriptionLocalServiceUtil().setService(mock(SubscriptionLocalService.class, RETURNS_MOCKS));
        new TeamLocalServiceUtil().setService(mock(TeamLocalService.class, RETURNS_MOCKS));
        new ThemeLocalServiceUtil().setService(mock(ThemeLocalService.class, RETURNS_MOCKS));
        new TicketLocalServiceUtil().setService(mock(TicketLocalService.class, RETURNS_MOCKS));
        new UserGroupGroupRoleLocalServiceUtil().setService(mock(UserGroupGroupRoleLocalService.class, RETURNS_MOCKS));
        new UserGroupLocalServiceUtil().setService(mock(UserGroupLocalService.class, RETURNS_MOCKS));
        new UserGroupRoleLocalServiceUtil().setService(mock(UserGroupRoleLocalService.class, RETURNS_MOCKS));
        new UserIdMapperLocalServiceUtil().setService(mock(UserIdMapperLocalService.class, RETURNS_MOCKS));
        new UserLocalServiceUtil().setService(mock(UserLocalService.class, RETURNS_MOCKS));
        new UserTrackerLocalServiceUtil().setService(mock(UserTrackerLocalService.class, RETURNS_MOCKS));
        new UserTrackerPathLocalServiceUtil().setService(mock(UserTrackerPathLocalService.class, RETURNS_MOCKS));
        new WebDAVPropsLocalServiceUtil().setService(mock(WebDAVPropsLocalService.class, RETURNS_MOCKS));
        new WebsiteLocalServiceUtil().setService(mock(WebsiteLocalService.class, RETURNS_MOCKS));
        new WorkflowDefinitionLinkLocalServiceUtil().setService(mock(WorkflowDefinitionLinkLocalService.class, RETURNS_MOCKS));
        new WorkflowInstanceLinkLocalServiceUtil().setService(mock(WorkflowInstanceLinkLocalService.class, RETURNS_MOCKS));

        new PortalUtil().setPortal(mock(Portal.class));
        new DigesterUtil().setDigester(mock(Digester.class));

        companyMockFactory.setupClassNames();
    }

    /**
     * Setup basic classes for DynamicQuery Liferay API with dummy implementation.
     *
     * We will not actually support dynamic queries - the only implementation is done by RETURNS_MOCKS to avoid NPE.
     * What we need is to avoid method in constructing queries. This method contains mockup of:
     * <ul>
     *   <li>PortalClassLoaderUtil - dynamic class loader (use classloader of this class instead)</li>
     *   <li>DynamicQueryFactoryUtil - return dummy implementation of DynamicQueryFactory</li>
     *   <li>RestrictionsFactoryUtil - RestrictionsFactory with dummy implementation</li>
     *   <li>ProjectionFactoryUtil - ProjectionFactory with dummy implementation</li>
     * </ul>
     */
    public void mockDynamicQuery()
    {
        new DynamicQueryFactoryUtil().setDynamicQueryFactory(mock(DynamicQueryFactory.class, RETURNS_MOCKS));
        new RestrictionsFactoryUtil().setRestrictionsFactory(mock(RestrictionsFactory.class, RETURNS_MOCKS));
        new ProjectionFactoryUtil().setProjectionFactory(mock(ProjectionFactory.class, RETURNS_MOCKS));
        new OrderFactoryUtil().setOrderFactory(mock(OrderFactory.class, RETURNS_MOCKS));
    }

    private void fillTestData() throws PortalException, SystemException
    {
        // default and the only one company (portal instance)
        companyMockFactory.createCompanyImpl("company", CompanyMockFactory.DEFAULT_COMPANY_ID);

        // main group
        Group group = companyMockFactory.createGroupImpl("group", 1);

        // with serveral organizations
        Organization orgDefault = companyMockFactory.createOrganizationImpl("organization", 1);
        companyMockFactory.addOrganizationToGroup(orgDefault, group);

        Organization orgDatalite = companyMockFactory.createOrganizationImpl("datalite", 1);
        companyMockFactory.addOrganizationToGroup(orgDatalite, group);

        // and several users
        User admin = userMockFactory.createUserImpl("admin", 1);
        Contact adminContact = userMockFactory.createContactImpl("admin", 1);
        Address adminAddress = userMockFactory.createAddressImpl("admin", 1);
        userMockFactory.addContactToUser(adminContact, admin);
        userMockFactory.addAddressToContact(adminAddress, adminContact);

        User edudant = userMockFactory.createUserImpl("edudant", 2);
        Contact edudantContact = userMockFactory.createContactImpl("edudant", 2);
        Address edudantAddress = userMockFactory.createAddressImpl("edudant", 2);
        userMockFactory.addContactToUser(edudantContact, edudant);
        userMockFactory.addAddressToContact(edudantAddress, edudantContact);

        User francimor = userMockFactory.createUserImpl("francimor", 3);
        Contact francimorContact = userMockFactory.createContactImpl("francimor", 3);
        Address francimorAddress = userMockFactory.createAddressImpl("francimor", 3);
        userMockFactory.addContactToUser(francimorContact, francimor);
        userMockFactory.addAddressToContact(francimorAddress, francimorContact);

        userMockFactory.createUserQuery( Arrays.asList(admin, edudant, francimor) );

    }

    public void setCompanyMockFactory(CompanyMockFactory companyMockFactory) {
        this.companyMockFactory = companyMockFactory;
    }

    public void setUserMockFactory(UserMockFactory userMockFactory) {
        this.userMockFactory = userMockFactory;
    }


}
