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
import java.util.logging.Logger;

public class LiferayRoleMapper extends HashMap<String, String> {

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
            Logger.getLogger(getClass().getName()).warning("Unable to parse WEB-INF/liferay-portlet.xml, role mapper will not be available");
        }
    }
}
