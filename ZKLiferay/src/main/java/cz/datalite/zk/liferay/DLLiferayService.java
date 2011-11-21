/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.liferay.mock.LiferayMock;
import cz.datalite.zk.liferay.security.ZulRolesHelper;
import org.hibernate.SQLQuery;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.zkoss.zk.ui.Executions;

import javax.portlet.PortletRequest;
import java.util.Date;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.portlet.PortletPreferences;

/**
 * Connect to Liferay.
 */
public class DLLiferayService {

    // set the mock if running in mocked mode
    private LiferayMock liferayMock;

    /**
     * Mock dependency (for mocked mode)
     *
     * @param liferayMock the mock
     */
    public void setLiferayMock(LiferayMock liferayMock) {
        this.liferayMock = liferayMock;
    }

    // if running in mocked mode, hold in this variable the mocked theme display
    private ThemeDisplay mockedThemeDisplay;

    // check if the mocked theme display is already created and if not create one
    protected ThemeDisplay getMockedThemeDisplay() {
        if (mockedThemeDisplay == null) {
            try {
                mockedThemeDisplay = liferayMock.getThemeDisplayMockFactory().createThemeDisplay();
            } catch (SystemException e) {
                throw new LiferayException(e);
            } catch (PortalException e) {
                throw new LiferayException(e);
            }
        }

        return mockedThemeDisplay;
    }

    /**
     * Check if Liferay is really running (not in mocked mode)
     * @return true if we are in liferay portal
     */
    public boolean isLiferayRunning()
    {
        return liferayMock.isLiferayRunning();
    }

    /**
     * Company Id aka Portal instance - current portal instance.
     *
     * @return portal comapny id
     */
    public long getCompanyId() {
        return getThemeDisplay().getCompanyId();
    }

    /**
     * Company object aka Portal instance - current portal instance.
     *
     * @return portal comapny id
     */
    public Company getCompany() {
        return getThemeDisplay().getCompany();
    }

    /**
     * Current group Id (or scope group id).
     * It represents the current organization or control panel or user layout.
     *
     * @return group id
     */
    public long getGroupId() {
        return getThemeDisplay().getScopeGroupId();
    }

    /**
     * Current group (or scope group).
     * It represents the current organization or control panel or user layout.
     *
     * @return liferay group
     */
    public Group getGroup() {
        return getThemeDisplay().getScopeGroup();
    }

    /**
     * Returns the topmost group in parent/child group organization starting with current scope group.
     *
     * @return root group
     */
    public Group getRootGroup() {
        Group group = getThemeDisplay().getScopeGroup();
        long parentGroupId;
        while((parentGroupId = group.getParentGroupId()) != 0)
        {
            try {
                group = GroupLocalServiceUtil.getGroup(parentGroupId);
            } catch (PortalException e) {
                throw new LiferayException("Error while finding parent group: " + parentGroupId, e);
            } catch (SystemException e) {
                throw new LiferayException("Error while finding parent group: " + parentGroupId, e);
            }
        }

        return group;
    }


    /**
     * Returns current user id (logged in the portal)
     *
     * @return current user id
     */
    public Long getUserId() {
        return getThemeDisplay().getUserId();
    }

    /**
     * Returns current user (logged in the portal)
     *
     * @return current user
     */
    public User getUser() {
        return getThemeDisplay().getUser();
    }

    /**
     * Theme display is object with current setup (group  id, scope id, ...)
     *
     * @return theme display
     */
    public ThemeDisplay getThemeDisplay() {
        if (liferayMock != null && liferayMock.isLiferayRunning())
            if (Executions.getCurrent() != null) // ZK is running
                return (ThemeDisplay) Executions.getCurrent().getSession().getAttribute(WebKeys.THEME_DISPLAY);
            else if (RequestContextHolder.getRequestAttributes() != null) // Spring context listener
                return (ThemeDisplay) RequestContextHolder.getRequestAttributes().
                        getAttribute(WebKeys.THEME_DISPLAY, RequestAttributes.SCOPE_REQUEST);
            else // do not know where to get ThemeDisplay
                throw new LiferayException("Unable to get ThemeDisplay - DLLiferayService can be used only in ZK or Spring context.");

        else
            return getMockedThemeDisplay();
    }

    /**
     * Service context holds current request parameters (user id, company id).
     *
     * @return service context initialized with basic values (check implementation).
     */
    public ServiceContext getServiceContext() {
        ServiceContext serviceContext = new ServiceContext();

        serviceContext.setCompanyId(getCompanyId());
        serviceContext.setAddGuestPermissions(true);
        serviceContext.setCreateDate(new Date());
        serviceContext.setScopeGroupId(getGroupId());
        serviceContext.setUserId(getUserId());

        return serviceContext;
    }

    /**
     * Returns true, if the user has at least one role from the list.
     *
     * @param authorities comma seperated role list
     */
    public boolean isAnyGranted(String authorities) {
        for (String role : authorities.split(",")) {
            if (isUserInRole(role))
                return true;

        }

        return false;
    }

    /**
     * Returns true, if the user has all roles from the list.
     *
     * @param authorities comma seperated role list
     */
    public boolean isAllGranted(String authorities) {
        for (String role : authorities.split(",")) {
            if (!isUserInRole(role))
                return false;

        }

        return true;
    }

    /**
     * Returns true, if the user has no role from the list.
     *
     * @param authorities comma seperated role list
     */
    public boolean isNoneGranted(String authorities) {
        for (String role : authorities.split(",")) {
            if (isUserInRole(role))
                return false;

        }

        return true;
    }

    /**
     * Check if the user is in role (user role mapper from liferay-portlet.xml).
     *
     * @param role name of mapped role (if not found, check directly Liferay roles.
     *
     * @return true, if the user is in the role
     */
    public boolean isUserInRole(String role) {

        if (!isLiferayRunning())
            return isUserInRoleMock(role);
        else
            return isUserInRoleLiferay(role);
    }


    // check Liferay role based on a mapper
    @SuppressWarnings({"unchecked"})
    protected boolean isUserInRoleLiferay(String role) {

        if (Executions.getCurrent() == null)
        {
            // not in ZK, try to use spring
            return getRequestSpring().isUserInRole(role);
        }


        // else get roles from session (set in DLPortlet)

        User user = getUser();
        long companyId = getCompanyId();

        // not active user,
        if (user == null)
            return false;

        Map<String, String> roleMappers = (Map<String, String>) Executions.getCurrent().getSession().getAttribute(DLPortlet.ROLE_MAPPERS);

        if (roleMappers == null)
            throw new LiferayException("Session attribute DLPortlet.ROLE_MAPPERS not found. Do you use DLPortlet in your portlet.xml configuration?");


        // translate the role
        String roleLink = roleMappers.get(role);

        try
        {
            // if translated, check the role
            if (!StringHelper.isNull(roleLink))
                return RoleLocalServiceUtil.hasUserRole(user.getUserId(), companyId, roleLink, true);
            else // use Liferay original role directly
                return RoleLocalServiceUtil.hasUserRole(user.getUserId(), companyId, role, true);
        } catch (PortalException e) {
            throw new LiferayException(e);
        } catch (SystemException e) {
            throw new LiferayException(e);
        }
    }

    // in a mock check the link - if not found, return false, otherwise return true unless it is mapped to NOBODY
    protected boolean isUserInRoleMock(String role) {
        Map<String, String> roleMappers = liferayMock.getLiferayRoleMapper();
        String roleLink = roleMappers.get(role);
        if (StringHelper.isNull(roleLink))
            return false;
        else if ("NOBODY".equals(roleLink))
            return false;
        else
            return true;
    }


    /**
     * Support for ZUL syntax in EL.
     * Usage:
     *      ${dlLiferayService.roles.anyGranted['ROLE_NAME']}
     *      ${dlLiferayService.roles.allGranted['ROLE_NAME']}
     *      ${dlLiferayService.roles.noneGranted['ROLE_NAME']}
     *      ${dlLiferayService.roles.userInRole['ROLE_NAME']}
     *
     * (this mehod is needed, EL can not call method - dlLiferayService.isUserInRole("ROLE_NAME"));
     *
     * @return true if role requirement is satisfied
     */
    public ZulRolesHelper getRoles()
    {
        return zulRolesHelper;
    }

    // artifical map as helper for ZUL map access (see getRoles() )
    private final ZulRolesHelper zulRolesHelper = new ZulRolesHelper(this);


    private PortletRequest getRequestSpring()
    {
        return (PortletRequest) RequestContextHolder.currentRequestAttributes()
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
    }

    /**
     * Vrátí SQL (použití pouze s native SQL, ne criteria)
     * pro zařazení do where podmínky nad tabulkami s COMPANY_ID a GROUP_ID.
     *
     * Používat společně s  setLiferayQuerySQLParams pro vlastní nastavení parametrů
     *
     * @return ( (COMPANYID is null or COMPANYID = :companyId) and (GROUPID is null or GROUPID = :groupId) )
     */
    public String getLiferayQuerySQL() {
        return " ( (COMPANYID is null or COMPANYID = :companyId) and (GROUPID is null or GROUPID = :groupId) ) ";
    }

    /**
     * Nastaví parametry pro where podmínku přidanou metodou getLiferayQuerySQL().
     *
     * @param query Hibernate SQL query ke kterému se má nastavit
     */
    public void setLiferayQuerySQLParams(SQLQuery query) {
        query.setLong("companyId", getCompanyId());
        query.setLong("groupId", getGroupId());
    }
    
    /**
     * Method return portlet preference.
     * @return portlet preference.
     */
    public PortletPreferences getPortletPreferences()
    {
        return this.getThemeDisplay().getPortletDisplay().getPortletSetup();
    }
}
