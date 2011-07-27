package cz.datalite.zk.liferay.mock;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.model.impl.OrganizationImpl;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import static org.mockito.Mockito.*;

/**
 *
 * @author Jiri Bubnik
 */
public class CompanyMockFactory
{
    // we run in this company (portal instance)
    public static final long DEFAULT_COMPANY_ID = 1;
        
    // classname from table CLASSNAME_
    public static final long GROUP_CLASSNAME_ID = 10012;  // com.liferay.portal.model.Group
    public static final long ORGANIZATION_CLASSNAME_ID = 10024; // com.liferay.portal.model.Organization
    public static final long USER_CLASSNAME_ID = 10046;  // com.liferay.portal.model.User

    // group type
    public static final int GROUP_TYPE_PUBLIC = 0;
    public static final int GROUP_TYPE_COMUNITY = 1;
    public static final int GROUP_TYPE_PRIVATE = 2;
    public static final int GROUP_TYPE_CONTROL_PANEL = 3;

    // ID from default installation
    public static final long GROUP_ID = 18;
    public static final long MAIN_USER_ID = 2;

    /**
     * Company / Portal Instance, Only one instance with ID DEFAULT_COMPANY_ID should be created for mocking.
     */
    public Company createCompanyImpl(String prefix, long companyId) throws PortalException, SystemException
    {
        Company company = spy(new CompanyImpl());
        company.setNew(false);
        company.setCompanyId(companyId);

        company.setWebId(prefix + " WebId");
        company.setHomeURL(prefix + ".home.url");
        company.setMaxUsers(200);
        company.setSystem(true);

        when(CompanyLocalServiceUtil.getService().getCompany(companyId)).thenReturn(company);
        when(CompanyLocalServiceUtil.getService().getCompanyById(companyId)).thenReturn(company);

        return company;
    }

    /**
     * Portal class names changes by installation - setup basic classes
     */
    public void setupClassNames()
    {
        doReturn("com.liferay.portal.model.Group").when(PortalUtil.getPortal()).getClassName(GROUP_CLASSNAME_ID);
        doReturn("com.liferay.portal.model.Organization").when(PortalUtil.getPortal()).getClassName(ORGANIZATION_CLASSNAME_ID);
        doReturn("com.liferay.portal.model.User").when(PortalUtil.getPortal()).getClassName(USER_CLASSNAME_ID);
    }
    
    /**
     * Groups are split into Comunities, Organizations and User pages
     */
    public Group createGroupImpl(String prefix, long groupId) throws PortalException, SystemException
    {
        Group group = spy(new GroupImpl());
        group.setNew(false);
        group.setPrimaryKey(groupId);
        group.setCompanyId(DEFAULT_COMPANY_ID);

        group.setActive(true);
        group.setDescription(prefix + " Description");
        group.setFriendlyURL(prefix + "/friendly/url");
        group.setName(prefix + " Name");
        group.setClassNameId(ORGANIZATION_CLASSNAME_ID);
        group.setType(GROUP_TYPE_PUBLIC);

        when(GroupLocalServiceUtil.getService().getGroup(groupId)).thenReturn(group);
        when(GroupLocalServiceUtil.getService().getGroup(DEFAULT_COMPANY_ID, group.getName())).thenReturn(group);

        return group;
    }

    /**
     * Create an organization.
     */
    public Organization createOrganizationImpl(String prefix, long orgId) throws PortalException, SystemException
    {
        Organization organization = spy(new OrganizationImpl());
        organization.setNew(false);
        organization.setPrimaryKey(orgId);
        organization.setCompanyId(DEFAULT_COMPANY_ID);

        organization.setComments(prefix + " Comments");
        organization.setName(prefix + " Name");

        when(OrganizationLocalServiceUtil.getService().getOrganization(orgId)).thenReturn(organization);

        return organization;
    }

    /**
     * Setup group class name and primary key by organization. Add to GroupLocalServiceUtil, although I'm not sure, what the reason is.
     */
    public void addOrganizationToGroup(Organization organization, Group group) throws PortalException, SystemException
    {
        when(GroupLocalServiceUtil.getService().getOrganizationGroup(organization.getCompanyId(), organization.getPrimaryKey())).thenReturn(group);
        group.setClassNameId(ORGANIZATION_CLASSNAME_ID);
        group.setClassPK(organization.getPrimaryKey());
    }

}
