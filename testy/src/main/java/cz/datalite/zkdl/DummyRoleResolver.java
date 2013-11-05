package cz.datalite.zkdl;

import cz.datalite.zk.annotation.processor.ZkRoleResolver;

/**
 *
 */
public class DummyRoleResolver implements ZkRoleResolver {

    public boolean isAnyGranted(String[] roles) {
        for (String role : roles)
            if (role.equals("ANY"))
                return true;

        return false;
    }


    public boolean isAllGranted(String[] roles) {
        for (String role : roles)
            if (role.equals("ALL"))
                return false;

        return true;

    }


    public boolean isNoneGranted(String[] roles) {
        for (String role : roles)
            if (role.equals("NONE"))
                return false;

        return true;
    }
}
