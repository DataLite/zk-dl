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

import cz.datalite.zk.annotation.invoke.Invoke;
import java.lang.reflect.Method;
import java.util.List;
import org.zkoss.zk.ui.Component;

/**
 * <p>Processor interfaces provides basic method of each 
 * annotation processor which can process Zk annotations
 * using decorator pattern and {@link Invoke} interface.</p>
 *
 * <p>The main method processes given annotation and adds
 * another decorator to given set of {@link Invoke} objects.
 * The role of decorator is based on annotaion point.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface Processor<T> {

    /**
     * <p>The basic method of each annotation processor. 
     * There is given annotation which have to be processed.
     * Processing lies in decorating given set of {@link Invoke}
     * objects with annotation specific functionality
     * which provides desired effect.</p>
     * @param annotation processed annotation
     * @param inner set of inner object to be decorated
     * @param method annotated method
     * @param master master component bound to controller
     * @param controller the controller, owner of methods
     * @return set of decorated invokers
     */
    List<Invoke> process( T annotation, List<Invoke> inner, Method method, Component master, Object controller );
}
