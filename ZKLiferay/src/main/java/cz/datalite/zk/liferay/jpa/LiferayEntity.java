/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.jpa;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import cz.datalite.zk.liferay.LiferayException;
import org.hibernate.annotations.Filter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

@MappedSuperclass
@Filter(name = "LiferayFilter")
public abstract class LiferayEntity implements Serializable {

    /** Identifikátor Liferay Group ID */
    private Long GROUPID;

    /** Identifikátor Liferay Company ID */
    private Long COMPANYID;


    public Long getGROUPID() {
        return GROUPID;
    }

    public void setGROUPID(Long GROUPID) {
        this.GROUPID = GROUPID;
    }

    public Long getCOMPANYID() {
        return COMPANYID;
    }

    public void setCOMPANYID(Long COMPANYID) {
        this.COMPANYID = COMPANYID;
    }

    public Company getCOMPANY()
    {
        try
        {
            Long id = getCOMPANYID();
            return id == null ? null : CompanyLocalServiceUtil.getCompany(id);
        }
        catch (PortalException ex)
        {
            throw new LiferayException(ex);
        }
        catch (SystemException ex)
        {
            throw new LiferayException(ex);
        }
    }

    public void setCOMPANY(final Company COMPANY)
    {
        setCOMPANYID(COMPANY == null ? null : COMPANY.getPrimaryKey());
    }

    public Group getGROUP()
    {
        try
        {
            Long id = getGROUPID();
            return id == null ? null : GroupLocalServiceUtil.getGroup(id);
        }
        catch (PortalException ex)
        {
            throw new LiferayException(ex);
        }
        catch (SystemException ex)
        {
            throw new LiferayException(ex);
        }
    }

    public void setGROUP(final Group GROUP)
    {
        setGROUPID(GROUP == null ? null : GROUP.getPrimaryKey());
    }

    @Transient
    public boolean isPortalWide()
    {
        return getCOMPANYID() == null;
    }
}
