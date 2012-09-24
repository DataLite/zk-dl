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
import cz.datalite.zk.bind.ZKBinderHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;

/**
 * <p>Handles binding request before and after method invocation. For
 * all registered component executes load or safe based on annotation's
 * properties.</p>
 * 
 * <p><strong>Usable with databinding v1.0 only</strong></p>
 *
 * @author Karel Cemus
 */
public class ZkBindingHandler extends Handler {

    /** Components to be saved before */
    private final List<String> saveBefore;

    /** Components to be load after */
    private final List<String> loadAfter;

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
        this.saveBefore = Collections.unmodifiableList( saveBefore );
        this.loadAfter = Collections.unmodifiableList( loadAfter );
    }

    @Override
    protected boolean doBefore( final Context context ) {
        boolean success = true;
        
        for ( String id : saveBefore ) {
            Component component = getComponent( id, context.getRoot() );
            success &= ZKBinderHelper.saveComponent( component );
        }
        return success;
    }

    @Override
    protected void doAfter( final Context context ) {
        for ( String id : loadAfter ) {
            Component component = getComponent( id, context.getRoot() );
            ZKBinderHelper.loadComponent( component );
        }
    }

    private Component getComponent( String id, Component master ) {
        try {
            return id.length() == 0 ? master : master.getFellow( id );
        } catch ( ComponentNotFoundException ex ) {
            throw new ComponentNotFoundException( "ZkBinding could not be registered on component \"" + id + "\" because component wasn\'t found.", ex );
        }
    }
}
