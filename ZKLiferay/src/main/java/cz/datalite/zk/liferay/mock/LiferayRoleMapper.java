/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.mock;

import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.InputStream;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiferayRoleMapper extends HashMap<String, String> {

    /** Logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger( LiferayRoleMapper.class );
    
    public LiferayRoleMapper(InputStream liferayPortletXml)
    {
        Document document = null;
        try {
            document = SAXReaderUtil.read(liferayPortletXml, true);

            Element rootElement = document.getRootElement();

            for (Element roleMapperElement : rootElement.elements("role-mapper")) {
                String roleName = roleMapperElement.elementText("role-name");
                String roleLink = roleMapperElement.elementText("role-link");

                put(roleName, roleLink);
            }

        } catch (DocumentException e) {
            LOGGER.warn( "Unable to parse WEB-INF/liferay-portlet.xml, role mapper will not be available" );
        }
    }
}
