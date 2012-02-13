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
package cz.datalite.zk.annotation.invoke;

import cz.datalite.zk.annotation.ZkBinding;
import cz.datalite.zk.annotation.ZkBindings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.DataBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Handles binding request before and after method invocation. For
 * all registered component executes load or safe based on annotation's
 * properties.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkBindingHandler extends Handler {

    /** Components to be saved before */
    private List<String> saveBefore;

    /** Components to be load after */
    private List<String> loadAfter;

    public static Invoke process( Invoke inner, ZkBinding annotation ) {
        List<String> loadAfter = annotation.loadAfter() ? Collections.singletonList( annotation.component() ) : Collections.EMPTY_LIST;
        List<String> saveBefore = annotation.saveBefore() ? Collections.singletonList( annotation.component() ) : Collections.EMPTY_LIST;

        return new ZkBindingHandler( inner, saveBefore, loadAfter );
    }

    public static Invoke process( Invoke inner, ZkBindings annotation ) {
        List<String> loadAfter = new ArrayList<String>();
        List<String> saveBefore = new ArrayList<String>();
        for ( ZkBinding binding : annotation.bindings() ) {
            // load all components defined in binding annotation
            if (binding.loadAfter())
                loadAfter.add( binding.component() );

            if (binding.saveBefore())
                saveBefore.add(binding.component());
        }
        return new ZkBindingHandler( inner, saveBefore, loadAfter );
    }

    public ZkBindingHandler( Invoke inner, List<String> saveBefore, List<String> loadAfter ) {
        super( inner );
        this.saveBefore = saveBefore;
        this.loadAfter = loadAfter;
    }

    @Override
    protected boolean doBeforeInvoke( Event event, Component master, Object controller ) {
        for ( String id : saveBefore ) {
            Component component = getComponent( id, master );
            getBinder( component ).saveComponent( component );
        }
        return true;
    }

    @Override
    protected void doAfterInvoke( Event event, Component master, Object controller ) {
        for ( String id : loadAfter ) {
            Component component = getComponent( id, master );
            getBinder( component ).loadComponent( component );
        }
    }

    private Component getComponent( String id, Component master ) {
        try {
            return id.length() == 0 ? master : master.getFellow( id );
        } catch ( ComponentNotFoundException ex ) {
            throw new ComponentNotFoundException( "ZkBinding could not be registered on component \"" + id + "\" because component wasn\'t found.", ex );
        }
    }

    /**
     * Vraci odkaz na binder
     * @param comp komponenta podle ktere ho urci
     * @return odkaz na binder
     */
    private static DataBinder getBinder( Component comp ) {
        return ( DataBinder ) comp.getAttributeOrFellow( "binder", true );
    }
}
