/**
 * Copyright 28.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.security;

import cz.datalite.zk.liferay.DLLiferayService;

import java.util.HashMap;
import java.util.Map;

/**
 * Artifical class for role support from ZUL via EL.
 *
 * EL can not evaluate methods with parameters, so we simulate this behaviour via Map interface.
 *
 * @see cz.datalite.zk.liferay.DLLiferayService#getRoles()
 */
public class ZulRolesHelper
{
    // the liferay service (which will eventualy resolve access rights)
    private DLLiferayService liferayService;

    /**
     * Create a new Helper class for the Liferay Service (shold be spring bean)
     *
     * @param liferayService the liferay service (which will eventualy resolve access rights)
     */
    public ZulRolesHelper(DLLiferayService liferayService)
    {
        this.liferayService = liferayService;
    }

    /**
     * Returns true, if the user has at least one role from the list.
     *
     * Use get("ROLE_NAME,ROLE_OTHER") - comma seperated role list
     */
    public Map<String, Boolean> getAnyGranted()
    {
        return anyGranted;
    }

    private final Map<String, Boolean> anyGranted = new HashMap<String, Boolean>()
    {
        @Override
        public Boolean get(Object key) {

            return liferayService.isAnyGranted(String.valueOf(key));
        }
    };

    /**
     * Returns true, if the user has all roles from the list.
     *
     * Use get("ROLE_NAME,ROLE_OTHER") - comma seperated role list
     */
    public Map<String, Boolean> getAllGranted()
    {
        return allGranted;
    }

    private final Map<String, Boolean> allGranted = new HashMap<String, Boolean>()
    {
        @Override
        public Boolean get(Object key) {

            return liferayService.isAllGranted(String.valueOf(key));
        }
    };

    /**
     * Returns true, if the user does not have any role from the list.
     *
     * Use get("ROLE_NAME,ROLE_OTHER") - comma seperated role list
     */
    public Map<String, Boolean> getNoneGranted()
    {
        return noneGranted;
    }

    private final Map<String, Boolean> noneGranted = new HashMap<String, Boolean>()
    {
        @Override
        public Boolean get(Object key) {

            return liferayService.isNoneGranted(String.valueOf(key));
        }
    };

    /**
     * Returns true, if the user has the role.
     *
     * Use get("ROLE_NAME") - role name
     */
    public Map<String, Boolean> getUserInRole()
    {
        return userInRole;
    }


    private final Map<String, Boolean> userInRole = new HashMap<String, Boolean>()
    {
        @Override
        public Boolean get(Object key) {

            return liferayService.isUserInRole(String.valueOf(key));
        }
    };


}
