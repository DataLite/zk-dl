/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.jpa;

public class LiferayOperationDenied extends Error {

    public LiferayOperationDenied(String message) {
        super(message);
    }

    public LiferayOperationDenied(String message, Throwable cause) {
        super(message, cause);
    }
}
