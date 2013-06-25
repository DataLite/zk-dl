package cz.datalite.zk.annotation.processor;

/**
 *
 */
public interface ZkRoleResolver {

    /**
     * Return true, if current user has at least one of the roles.
     *
     * @param roles role list
     *
     * @return true to allow access
     */
    boolean isAnyGranted(String[] roles);

    /**
     * Return true, if current user has all of the roles.
     *
     * @param roles role list
     *
     * @return true to allow access
     */
    boolean isAllGranted(String[] roles);

    /**
     * Return true, if current user has none of the roles.
     *
     * @param roles role list
     *
     * @return true to allow access
     */
    boolean isNoneGranted(String[] roles);
}
