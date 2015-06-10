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
 * @author Karel Cemus
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

    /**
     * <p>Some applications are written localizable. Usually
     * the approach requires property file which contains
     * pairs key-value where key is text written in code
     * and value is localized message which is shown to user.
     * This annotation also contains some texts which can be localized.
     * This attribute says if the text in the annotation is text to be
     * directly shown to user or if it is only key into property file</p>
     *
     * <p>If the attribute is false, then the text is directly shown without
     * any modification. If the attirbute is true then there is conversion to
     * the value through 
     * {@link org.zkoss.util.resource.Labels#getLabel(java.lang.String)}. This
     * class reads all property files defined as language packs and provides
     * coorect translates.</p>
     * 
     * <p>Usage of this annotation attribute is highly reccomended for writing
     * some encapsulate component which can be distributed in stand-alone mode.
     * Otherwise <strong>this attribute is not comfortable for localization whole
     * application</strong>.</p>
     *
     * <h3>Project localization</h3>
     * <p>For localization of whole application the i18n can be activated
     * in configuration file. The disadvantage of this solution lies in
     * impossibility of localy disabling i18n and each message have to be
     * localized. Application configuration file <strong>zk.xml</strong>
     * allows definining of library properties. Write in this code
     * to enable complete localization of Zk annotations ,
     * {@link ZkConfirm}.
     * <pre>{@code
     * &lt;!-- Enables required localization of &#064;ZkException
     *      and &#064;ZkConfirm annotations. --&gt;
     *  &lt;library-property&gt;
     *      &lt;name&gt;zk-dl.annotation.i18n&lt;/name&gt;
     *          &lt;value&gt;true&lt;/value&gt;
     *  &lt;/library-property&gt;
     * }</pre>
     * </p>
     *
     * <h3>Note:</h3>
     * <p>In default the localization is disabled.</p>
     *
     */
    public boolean i18n() default false;

    /**
     * <p>The most of frameworks wraps thrown exception by
     * their own exception type but the real caught exception
     * should be some of the wrapped exceptions.</p>
     *
     * <h3>Example:</h3>
     * <p>DAO layer throws and exception UnsupportedFilterException.
     * The application uses framework what executes DAO API
     * and each thrown Exception wraps with SomeFrameworkException.</p>
     * <p>Now there is an GUI method which allows to some advanced
     * users to define their own filter. If a user makes a mistake then
     * the Exception is thrown. In the method we don't want to handle
     * every generated exception but only UnsupportedFilterException.</p>
     * <pre>{@code
     *  &#064;ZkException(
     *      title="Invalid filter"
     *      class=some.framework.SomeFrameworkException.class
     *  )
     *  public void onFilter() {
     *      // there is some code
     *  }
     * }</pre>
     * <p>This annotation handles each exception thrown from this framework
     * including a lot of undesired ones.</p>
     * <pre>{@code
     *  &#064;ZkException(
     *      title="Invalid filter"
     *      class=our.project.UnsupportedFilterException.class
     *      unwrap=true
     *  )
     *  public void onFilter() {
     *      // there is some code
     *  }
     * }</pre>
     * <p>This annotaion catches each thrown exception and tries to find out if there is
     * any cause with this UnsupportedFilterException class. If the cause is found then the
     * message is shown otherwise the exception is thrown through.</p>
     *
     * <h3>Project configuration</h3>
     * <p>In default configuration the automatical unwrapping is disabled because
     * enabling can have performance impact. If the application uses some framework
     * which is mediator between prezentation and service layer then there could be
     * desired to enable unwrapping for all ZkException annotations. The library offers
     * the way how to enable it in general. On the other hand general enabling doesn't
     * allow local disabling. General disabling allows local enabling.</p>
     *
     * <p>Project configuration file <strong>zk.xml</strong> allows to write in library
     * properties. Type in this code to generally enable exception unwrapping.
     * <pre>{@code
     *  &lt;!-- Enables automatical exception unwrapping for &#064;ZkException.
     *      This can have performance impact if there is a lot of &#064;ZkException
     *      with deep stack. --&gt;
     *  &lt;library-property&gt;
     *      &lt;name&gt;zk-dl.annotation.exception.unwrap&lt;/name&gt;
     *          &lt;value&gt;true&lt;/value&gt;
     *  &lt;/library-property&gt;
     * }</pre>
     * </p>
     *
     * <h3>Note:</h3>
     * <p>Default is <em>FALSE</em> for whole project.</p>
     */
    public boolean unwrap() default false;
}
