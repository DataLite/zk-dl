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

import org.zkoss.zk.ui.Component;

/**
 * <p>Interface defines functionality of invokable object. These objects wrap
 * target method and provides additional functionality usually based on targeted
 * method's annotations.</p>
 *
 * <p>The main concept of {@link Invoke} interface lies in design pattern
 * <strong>decorator</strong>. Each object which wrappes the method should
 * provide one additional functionality and can be used as a part of many object
 * like in matryoshka doll. The order of composed object is important, the
 * functionality could be different with different order. The similar example is
 * interface
 * {@link java.io.InputStream}</p>
 *
 * @author Karel Cemus
 */
public interface Invoke {

    /**
     * Additional functionality appended before method invocation
     *
     * @param context context of invocation
     *
     * @return TRUE if continue invoking, FALSE for stop propagation
     */
    boolean doBeforeInvoke(Context context);

    /**
     * Invoke the desired method including additional functionality
     *
     * @param context context of invocation
     *
     * @return TRUE if continue invoking, FALSE for stop propagation
     *
     * @throws Exception Any exception has occured.
     */
    boolean invoke(Context context) throws Exception;

    /**
     * Additional functionality appended after method invocation.
     *
     * @param context context of invocation
     */
    void doAfterInvoke(Context context);

    /**
     * Binds to the component. Sets up component's properties according to the
     * handler's requirements
     *
     * @param root root component
     * 
     * @return observed component
     */
    Component bind(Component root);
}
