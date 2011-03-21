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

        themeDisplay.setUser(UserLocalServiceUtil.getUser(0l));
        themeDisplay.setCompany(CompanyLocalServiceUtil.getCompany(0l));
        themeDisplay.setScopeGroupId(GroupLocalServiceUtil.getGroup(0l).getGroupId());

        themeDisplay.setAccount(AccountLocalServiceUtil.getAccount(0l));
        themeDisplay.setContact(ContactLocalServiceUtil.getContact(0l));
        themeDisplay.setDoAsGroupId(GroupLocalServiceUtil.getGroup(0l).getGroupId());
        themeDisplay.setDoAsUserId(String.valueOf(UserLocalServiceUtil.getUser(0l).getUserId()));
        themeDisplay.setLocale(Locale.getDefault());
        themeDisplay.setPlid(1l);

        return themeDisplay;
    }
}
