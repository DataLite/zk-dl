/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.jpa;

import cz.datalite.dao.impl.GenericDAOImpl;
import cz.datalite.stereotype.Autowired;
import cz.datalite.zk.liferay.DLLiferayService;

import java.io.Serializable;

/**
 * Based on GenericDAOImpl - hibernate generic dao implementation with DLSearch/DLResponse extensions.
 *
 * This create special behaviour for entities of type LiferayEntity and operations makePersistent/makeTransient.
 *
 * As a normal user, if entity does not have companyId/groupId, it is set before saving to the database. You will
 * also not be allowed to delete entities witch are portal-wide (meaning without companyId/groupId).
 *
 * With a special role portal-wide (mapped in liferay-portlet.xml), you will ba allowed to create/delete entities
 * without companyId/groupId set.
 *
 *
 * @param <T> Enity class
 * @param <ID> Primary key type
 */
public abstract class GenericLiferayDAOImpl<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

    // Liferay role (before mapping), when the user wants edit portal-wide entites (i.e. with companyId and groupId set to null)
    public static final String PORTAL_WIDE_ROLE = "portal-wide";

    // The Liferay service (either based on real Liferay or the mock)
    @Autowired
    DLLiferayService dlLiferayService;

    /**
     * Save the entity to the database.
     *
     * As a normal user, if entity does not have companyId/groupId, it is set before saving to the database.
     *
     * With a special role portal-wide (mapped in liferay-portlet.xml), the id's are not set.
     *
     * {@inheritDoc}
     */
    @Override
    public T makePersistent(T entity) {
        if (entity instanceof LiferayEntity && !dlLiferayService.isUserInRole(PORTAL_WIDE_ROLE))
        {
            LiferayEntity liferayEntity = (LiferayEntity) entity;

            if (liferayEntity.getCOMPANYID() == null)
                liferayEntity.setCOMPANYID(dlLiferayService.getCompanyId());
            if (liferayEntity.getGROUPID() == null)
                liferayEntity.setGROUPID(dlLiferayService.getGroupId());
        }

        return super.makePersistent(entity);
    }

    /**
     * Save the entity to the database.
     *
     * Normal user is not allowed to delete entities, witch are portal-wide (meaning without companyId/groupId). We use
     * liferayEntity.isPortalWide() to check, if the entity is portal wide or not.
     *
     * With a special role portal-wide (mapped in liferay-portlet.xml), it is Ok to delete all entities.
     *
     * {@inheritDoc}
     * @throws LiferayOperationDenied Not alled to delete the entity.
     */
    @Override
    public void makeTransient(T entity) {
        if (entity instanceof LiferayEntity)
        {
            LiferayEntity liferayEntity = (LiferayEntity) entity;

            if (liferayEntity.isPortalWide() && !dlLiferayService.isUserInRole(PORTAL_WIDE_ROLE))
            {
                throw new LiferayOperationDenied("You don't have the required role to delete a portal-wide entity.");
            }
        }

        super.makeTransient(entity);
    }
}
