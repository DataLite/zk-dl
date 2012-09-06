/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.mock;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.*;
import com.liferay.portal.theme.ThemeDisplay;

import java.util.Locale;

public class ThemeDisplayMockFactory
{
    /**
     * Create ThemeDisplay object to access current portal layout objects.
     *
     * It can be called only after other mocks (user, group, ...) are set up.
     */
    public ThemeDisplay createThemeDisplay() throws SystemException, PortalException {
        ThemeDisplay themeDisplay =  new ThemeDisplay();

        // setup values by default objects from LocalServiceUtil mocks.

        themeDisplay.setUser(UserLocalServiceUtil.getUser(CompanyMockFactory.MAIN_USER_ID));
        themeDisplay.setCompany(CompanyLocalServiceUtil.getCompany(CompanyMockFactory.DEFAULT_COMPANY_ID));
        themeDisplay.setScopeGroupId(GroupLocalServiceUtil.getGroup(CompanyMockFactory.GROUP_ID).getGroupId());

        themeDisplay.setAccount(AccountLocalServiceUtil.getAccount(CompanyMockFactory.MAIN_USER_ID));
        themeDisplay.setContact(ContactLocalServiceUtil.getContact(CompanyMockFactory.MAIN_USER_ID));
        themeDisplay.setDoAsGroupId(GroupLocalServiceUtil.getGroup(CompanyMockFactory.GROUP_ID).getGroupId());
        themeDisplay.setDoAsUserId(String.valueOf(UserLocalServiceUtil.getUser(CompanyMockFactory.MAIN_USER_ID).getUserId()));
        themeDisplay.setLocale(Locale.getDefault());
        themeDisplay.setPlid(1l);

        return themeDisplay;
    }
}
