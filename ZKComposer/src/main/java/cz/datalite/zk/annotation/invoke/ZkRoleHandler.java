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
package cz.datalite.zk.annotation.invoke;

import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.annotation.ZkRole;
import cz.datalite.zk.annotation.processor.ZkRoleResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Handles binding request before and after method invocation. For all
 * registered component executes load or safe based on annotation's
 * properties.</p>
 *
 * @author Jiri Bubnik
 */
public class ZkRoleHandler extends Handler {

    /** Logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger( ZkRoleHandler.class );

    /** state of general property */
    private final static boolean localizeAll;

    /** has at least one role of this comma seperated list */
    private final String[] anyGranted;

    /** has all roles of this comma seperated list */
    private final String[] allGranted;

    /** has no role of this comma seperated list */
    private final String[] noneGranted;

    /** message title to be shown if access is denied */
    private final String title;

    /** message to be shown if access is denied */
    private final String message;

    /** Role resolver */
    private static ZkRoleResolver roleResolver = null;

    /** Component ids to disable, special value is COMMAND constant. */
    private String[] disableComponents;

    /** Component ids to disable, special value is COMMAND constant. */
    private String[] hideComponents;

    static {
        /** Reads default configuration for library */
        localizeAll = Boolean.parseBoolean(Library.getProperty("zk-dl.annotation.i18n", "false"));

        String roleResolverClass = Library.getProperty("zk-dl.annotation.roleResolver");

        // try to instantiate the only one instance of role resolver. If null, throw error only if @ZkRole is used.
        if (!StringHelper.isNull(roleResolverClass)) {
            try {
                roleResolver = (ZkRoleResolver) Class.forName(roleResolverClass).newInstance();
            } catch (InstantiationException e) {
                throw new IllegalStateException("Unable to instantiate class '" + roleResolverClass + "'. Check zk.xml parameter zk-dl.annotation.roleResolver.", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to instantiate class '" + roleResolverClass + "'. Check zk.xml parameter zk-dl.annotation.roleResolver.", e);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class not found: '" + roleResolverClass + "'. Check zk.xml parameter zk-dl.annotation.roleResolver.", e);
            }
        }
    }

    public static Invoke process(Invoke inner, ZkRole annotation) {
        String message = annotation.message();
        // check for default localized message
        boolean i18n = localizeAll || message.startsWith("zkcomposer.") || annotation.i18n();

        // return instance of handler
        return new ZkRoleHandler(inner, message, i18n, annotation.value(), annotation.allGranted(), annotation.noneGranted(),
                annotation.disableComponents(), annotation.hideComponents());
    }

    public ZkRoleHandler(Invoke inner, final String message, final boolean i18n,
                         String[] anyGranted, String[] allGranted, String[] noneGranted,
                         String[] disableComponents, String[] hideComponents) {
        super(inner);
        this.title = Labels.getLabel("zkcomposer.accessDenied.title");
        this.message = i18n ? Labels.getLabel(message) : message;

        this.anyGranted = anyGranted;
        this.allGranted = allGranted;
        this.noneGranted = noneGranted;

        this.disableComponents = disableComponents;
        this.hideComponents = hideComponents;
    }

    @Override
    public boolean invoke(final Context context) throws Exception {

        if (!canAccess()) {
            Messagebox.show(message, title, Messagebox.ABORT, Messagebox.EXCLAMATION);
            return false;
        } else {
            // continue with invocation
            return super.invoke(context);
        }
    }

    @Override
    public Component bind(Component parent) {
        if (disableComponents.length > 0 || hideComponents.length > 0) {
            if (!canAccess()) {
                for (Component comp : resolveComponents(parent, disableComponents)) {
                    doDisableComponent(comp);
                }
                for (Component comp : resolveComponents(parent, hideComponents)) {
                    doHideComponent(comp);
                }
            }
        }

        return super.bind(parent);
    }

    protected void doDisableComponent(Component comp) {
        if (comp instanceof Button) {
            ((Button) comp).setDisabled(true);
        }
    }

    protected void doHideComponent(Component comp) {
        if (comp instanceof Button) {
            comp.setVisible(false);
        }

    }

    protected List<Component> resolveComponents(Component parent, String[] ids) {
        List<Component> ret = new ArrayList<Component>();
        for (String id : ids) {
            if (!StringHelper.isNull(id)) {
                // resolve by Id
                Component comp = parent.getFellowIfAny(id);
                if (comp == null) {
                    throw new IllegalArgumentException("@ZkRole annotation - component in disableComponents or hideComponents not found: id='" + id + "'.");
                }
                ret.add(comp);
            }
        }

        return ret;
    }


    /**
     * Return true if all required roles are granted.
     */
    public boolean canAccess() {
        if (roleResolver == null) {
            throw new IllegalStateException("ZkRoleReslover class is not defined. Use 'zk-dl.annotation.roleResolver' parameter in zk.xml to define resolver class.");
        }

        boolean canAccess = true;
        if (anyGranted.length > 0 && !roleResolver.isAnyGranted(anyGranted)) {
            canAccess = false;
        }
        if (allGranted.length > 0 && !roleResolver.isAllGranted(allGranted)) {
            canAccess = false;
        }
        if (noneGranted.length > 0 && !roleResolver.isNoneGranted(noneGranted)) {
            canAccess = false;
        }
        return canAccess;
    }
}