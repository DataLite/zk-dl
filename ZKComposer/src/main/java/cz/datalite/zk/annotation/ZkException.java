/*
 * Copyright (c) 2011, DataLite. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package cz.datalite.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation declares that method 
 * throws an exception. Exception handling 
 * mechanism catches the exception of desired
 * type and displays the error messagebox to the 
 * user. The message can be defined in an
 * attribute or the exception message is used</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ZkException {

    /**
     * Title of message box is required attribute
     * @return title of window
     **/
    public String title();

    /**
     * Error message. If not defined the message from the
     * exception is use instead.
     * @return Error message
     **/
    public String message() default "";

    /**
     * Type of caught exception
     * @return type of exception
     **/
    public Class type();
}
