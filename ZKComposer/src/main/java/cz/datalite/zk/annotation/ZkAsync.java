/*
 * Copyright (c) 2012, DataLite. All rights reserved.
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
 * <p>This event is excepected to be long running. Use this annotation to
 * prevent user from sending another action, repeated invokation and to inform
 * him about long running operation. Targeting method will be invoked in an
 * another thread without user's desktop so <strong> do not work with components
 * in the method annotated with {@link ZkAsync}</strong>. This method is
 * considered to be used to load data from database or to do difficult
 * calculation. To update user interface use {@link #doAfter} event, which is
 * fired a usual way.</p>
 *
 * <p>Framework ensures to show an user a localizable busy message and disables
 * screen to prevent other action.</p>
 *
 * <p>For some long running actions we want to allow to users to change their
 * mind so the long running operation can be cancelable. In such case there is
 * shown localizable button to cancel running operation. Default value is
 * <b>TRUE</b> to allow cancel it.</p>
 *
 * <p>Do <strong>NOT</strong> use this annotation with {@link ZkBlocking}. Both
 * annotations are considered to provide similar effect in different ways.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZkAsync {

    /**
     * Message for user to be informed
     *
     * @return message
     *
     */
    public String message() default "zkcomposer.bePatient";

    /**
     * <p>Some applications are written localizable. Usually the approach
     * requires property file which contains pairs key-value where key is text
     * written in code and value is localized message which is shown to user.
     * This annotation also contains some texts which can be localized. This
     * attribute says if the text in the annotation is text to be directly shown
     * to user or if it is only key into property file</p>
     *
     * <p>If the attribute is false, then the text is directly shown without any
     * modification. If the attirbute is true then there is conversion to the
     * value through
     * {@link org.zkoss.util.resource.Labels#getLabel(java.lang.String)}. This
     * class reads all property files defined as language packs and provides
     * coorect translates.</p>
     *
     * <p>Usage of this annotation attribute is highly reccomended for writing
     * some encapsulate component which can be distributed in stand-alone mode.
     * Otherwise <strong>this attribute is not comfortable for localization
     * whole application</strong>.</p>
     *
     * <h3>Project localization</h3> <p>For localization of whole application
     * the i18n can be activated in configuration file. The disadvantage of this
     * solution lies in impossibility of localy disabling i18n and each message
     * have to be localized. Application configuration file
     * <strong>zk.xml</strong> allows definining of library properties. Write in
     * this code to enable complete localization of Zk annotations {@link ZkException},
     * {@link ZkConfirm}.
     * <pre><code>
     * &lt;!-- Enables required localization of &#064;ZkException
     *      and &#064;ZkConfirm annotations. --&gt;
     *  &lt;library-property&gt;
     *      &lt;name&gt;zk-dl.annotation.i18n&lt;/name&gt;
     *          &lt;value&gt;true&lt;/value&gt;
     *  &lt;/library-property&gt;
     * </code></pre> </p>
     *
     * <h3>Note:</h3> <p>In default the localization is disabled.</p>
     *
     */
    public boolean i18n() default false;

    /**
     * If user is allowed to abort a running operation. Default is true
     *
     * Cancellable operations are executed in <strong>asynchronnous</strong>
     * event which prevents from working with desktop bound object. Look at {@link #doAfter
     * } to let things done correctly.
     *
     * @return cancellable operation
     */
    public boolean cancellable() default true;

    /**
     * Asynchronnous operations are executed in an async event, which doesn't
     * allow to work with components and objects bound to user's session and
     * desktop. Such things has to be done in a synchronous event. To let things
     * be done properly (including binding etc.), after finishing the async
     * operation another event is fired. The default name of such event is
     * <code>onLongOperationAfter</code> but this attribute defines it. Such
     * following event is free to be annotated by all standard annotations
     * including this one.
     *
     * @return a name of the event fired after completing async operation
     */
    public String doAfter() default ZkEvents.ON_ASYNC_FINISHED;

    /**
     * Identifier of component to be blocked. User won't be able to invoke any
     * actions on this component.
     *
     * @return id of component, otherwise is blocked whole page
     */
    public String component() default "";
}
