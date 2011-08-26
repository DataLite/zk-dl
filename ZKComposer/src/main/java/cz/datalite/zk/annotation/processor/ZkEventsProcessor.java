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
package cz.datalite.zk.annotation.processor;

import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.invoke.Invoke;
import cz.datalite.zk.annotation.invoke.MethodInvoker;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.zk.ui.Component;

/**
 * <p>Annotation processor which handles 
 * processing of {@link ZkEvents}.</p>
 *
 * <p>Result of processing is collection
 * of {@link MethodInvoker}. That is the basic 
 * element of hierarchy of decorated invokers.
 * MethodInvoker provides the crucial functionality
 * - invoking of desired method.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ZkEventsProcessor implements Processor<ZkEvents> {

    public static final ZkEventsProcessor INSTANCE = new ZkEventsProcessor();

    public List<Invoke> process( ZkEvents annotations, List<Invoke> inner, Method method, Component target, Object controller ) {
        List<Invoke> invokes = new ArrayList<Invoke>();
        for ( ZkEvent annotation : annotations.events() ) {
            invokes.addAll( ZkEventProcessor.INSTANCE.process( annotation, inner, method, target, controller ) );
        }
        return invokes;
    }
}
