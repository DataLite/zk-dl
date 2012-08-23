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
 * <p>Abstract class handler simplifies the implementation of {@link Invoke}
 * interface. Handler serves as template for mathryoska class. The class
 * provides basic functionality like delegation getters and keeping the the
 * order of invocated methods.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public abstract class Handler implements Invoke {

    /** Inner (wrapped) object */
    protected final Invoke inner;

    public Handler(Invoke inner) {
        this.inner = inner;
    }

    public final boolean doBeforeInvoke(final Context context) {
        // if local doBefore failed, then do not continue, return false and break invocation
        return doBefore(context) && inner.doBeforeInvoke(context);
    }

    public final void doAfterInvoke(final Context context) {
        inner.doAfterInvoke(context);
        doAfter(context);
    }

    public boolean invoke(final Context context) throws Exception {
        return inner.invoke(context);
    }

    public Component bind(Component component) {
        return inner.bind(component);
    }

    /**
     * Additional functionality appended before method invocation
     *
     * @param event source event
     *
     * @return TRUE if continue invoking, FALSE for stop propagation
     */
    protected boolean doBefore(final Context context) {
        return true;
    }

    /**
     * Additional functionality appended after method invocation.
     *
     * @param event Source event
     */
    protected void doAfter(final Context context) {
    }

    /**
     * When doBeforeInvoke was interrupted, this method resumes proper execution
     * of rest of doBefore, full invoke and full doAfterInvoke.
     *
     * @throws Exception something went wrong
     */
    protected void resumeBeforeInvoke(final Context context) throws Exception {
        // resumeBeforeInvoke initialization
        if (inner.doBeforeInvoke(context)) {
            // full invocation
            if (context.getInvoker().invoke(context)) {
                // full do after
                context.getInvoker().doAfterInvoke(context);
            }
        }
    }

    /**
     * When doBeforeInvoke was interrupted, this method resumes proper execution
     * of rest of doBefore, full invoke and full doAfterInvoke.
     *
     * @throws Exception something went wrong
     */
    protected void resumeInvoke(final Context context) throws Exception {
        // resume invocation
        if (inner.invoke(context)) {
            // full do after
            context.getInvoker().doAfterInvoke(context);
        }
    }
}
