/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.jpa;


import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.WebKeys;
import org.hibernate.Session;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenEntityManagerInViewWithLiferayFilter extends OpenEntityManagerInViewFilter {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenEntityManagerInViewWithLiferayFilter.class);
    
    /**
     * Create an entity manager and set the "LiferayFilter" values to companyId and groupId.
     */
    protected EntityManager createEntityManager(EntityManagerFactory emf, ServletRequest request) {
        EntityManager entityManager = super.createEntityManager(emf);
        org.hibernate.Session session = (Session) entityManager.getDelegate();

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        // in AJAX calls the portal attribute is not available, lookup session attribute - set by DLPortlet.
        if (themeDisplay == null)
        {
            // do NOT create new session EVER!
            // If you create new session here, it will colide with portal session (it may happen after timeout)
            // see http://liferay.datalite.cz/blog/-/blogs/create-session-v-zk-update-requestu
            HttpSession httpSession = ((HttpServletRequest) request).getSession(false);
            if (httpSession != null)
                themeDisplay = (ThemeDisplay) httpSession.getAttribute(WebKeys.THEME_DISPLAY);
        }

        // set Liferay filter
        if (themeDisplay != null)
        {
            session.enableFilter("LiferayFilter")
                .setParameter("companyId", themeDisplay.getCompanyId())
                .setParameter("groupId", themeDisplay.getScopeGroupId());
        }

        return entityManager;
    }

    /**
     * Same as original implemnetation, just need to add request parameter to createEntityManager() method.
     */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		EntityManagerFactory emf = lookupEntityManagerFactory(request);
		boolean participate = false;

		if (TransactionSynchronizationManager.hasResource(emf)) {
			// Do not modify the EntityManager: just set the participate flag.
			participate = true;
		}
		else {
			LOGGER.trace("Opening JPA EntityManager in OpenEntityManagerInViewFilter");
			try {
				EntityManager em = createEntityManager(emf, request);
				TransactionSynchronizationManager.bindResource(emf, new EntityManagerHolder(em));
			}
			catch (PersistenceException ex) {
				throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
			}
		}

		try {
			filterChain.doFilter(request, response);
		}

		finally {
			if (!participate) {
				EntityManagerHolder emHolder = (EntityManagerHolder)
						TransactionSynchronizationManager.unbindResource(emf);
				LOGGER.trace("Closing JPA EntityManager in OpenEntityManagerInViewFilter");
				EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
			}
		}
    }

	/**
	 * Look up the EntityManagerFactory that this filter should use.
	 * The default implementation looks for a bean with the specified name
	 * in Spring's root application context.
	 * @return the EntityManagerFactory to use
	 * @see #getEntityManagerFactoryBeanName
	 */
    @Override
	protected EntityManagerFactory lookupEntityManagerFactory() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Using EntityManagerFactory '{}' for OpenEntityManagerInViewFilter", getEntityManagerFactoryBeanName());
		}
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		return wac.getBean(getEntityManagerFactoryBeanName(), EntityManagerFactory.class);
	}
}
