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
import org.zkoss.zk.ui.event.Event;

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

    /** source object of invocation */
    protected Invoke source;

    public Handler(Invoke inner) {
        this.inner = inner;
    }

    public final boolean doBeforeInvoke(Event event, Component master, Object controller) {
        // if local doBefore failed, then do not continue, return false and break invocation
        return doBefore(event, master, controller) && inner.doBeforeInvoke(event, master, controller);
    }

    public final void doAfterInvoke(Event event, Component master, Object controller) {
        inner.doAfterInvoke(event, master, controller);
        doAfter(event, master, controller);
    }

    public boolean invoke(Event event, Component master, Object controller) throws Exception {
        return inner.invoke(event, master, controller);
    }

    public String getEvent() {
        return inner.getEvent();
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
    protected boolean doBefore(Event event, Component master, Object controller) {
        return true;
    }

    /**
     * Additional functionality appended after method invocation.
     *
     * @param event Source event
     */
    protected void doAfter(Event event, Component master, Object controller) {
    }

    /**
     * When doBeforeInvoke was interrupted, this method resumes proper execution
     * of rest of doBefore, full invoke and full doAfterInvoke.
     *
     * @throws Exception something went wrong
     */
    protected void resumeBeforeInvoke(Event event, Component master, Object controller) throws Exception {
        // resumeBeforeInvoke initialization
        if (inner.doBeforeInvoke(event, master, controller)) {
            // full invocation
            if (source.invoke(event, master, controller)) {
                // full do after
                source.doAfterInvoke(event, master, controller);
            }
        }
    }

    /**
     * When doBeforeInvoke was interrupted, this method resumes proper execution
     * of rest of doBefore, full invoke and full doAfterInvoke.
     *
     * @throws Exception something went wrong
     */
    protected void resumeInvoke(Event event, Component master, Object controller) throws Exception {
        // resume invocation
        if (inner.invoke(event, master, controller)) {
            // full do after
            source.doAfterInvoke(event, master, controller);
        }
    }

    public void setSource(Invoke source) {
        this.source = source;
        inner.setSource(source);
    }
}
