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
package cz.datalite.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Instance of this class is bound to a thread which is bound to a user.
 * Objects implementing {@link ZkCancellable} are considered to allow cancel
 * running operation in their thread. Methods on such objects are called from
 * another threads. Implementations of this interface should cancel executing
 * database query, interrupt long calculation etc.</p>
 *
 * <p>Instance of this interface is stored in thread local variable.</p>
 *
 * <p>If {@link #isSet() } is <strong>TRUE</strong> that means that some code
 * asks for option to allow to interrupt current thread. Everyone can check this
 * property, wrap set instance and using {@link #replace() } replace it with the
 * current instance. Usually the first instance has empty implementation and
 * exists just to be detectable as a request.</p>
 *
 * @author Karel Cemus
 */
public class ZkCancellable {

    /** instance of ZkCancellable bound to the current thread */
    protected static final ThreadLocal<ZkCancellable> CURRENT = new ThreadLocal<>();

    /** Collection of known cancel commands to be used to cancel thread */
    protected final Set<ZkCancelCommand> commands = new HashSet<>();

    /**
     * Request for interrupting execution of a bound thread. This method invokes
     * all known cancel commands
     */
    public void cancel() {
        // invoke all commands
        for (ZkCancelCommand command : commands) {
            command.cancel();
        }
    }

    /**
     * Stores the object to ThreadLocal
     *
     * @see #remove()
     */
    public void store() {
        if (CURRENT.get() == null) {
            CURRENT.set(this);
        } else {
            throw new IllegalStateException("Current thread has already defined ZkCancellable object!");
        }

    }

    /**
     * Removes value from current thread if it is this one.
     *
     * @see #clean()
     */
    public void remove() {
        if (CURRENT.get() == this) {
            CURRENT.remove();
        }
    }

    /** Registers cancel command */
    public void addCommand(ZkCancelCommand command) {
        commands.add(command);
    }

    /**
     * Returns if current thread has defined cancellable object
     *
     * @return is defined cancellable?
     */
    public static boolean isSet() {
        return CURRENT.get() != null;
    }

    /**
     * Returns instance of cancellable object or NULL if not set. Existing
     * instance is bound to current thread
     *
     * @return instance of ZkCancellable object if it is set
     *
     * @see #isSet()
     */
    public static ZkCancellable get() {
        return CURRENT.get();
    }

    /**
     * Removes any instance bound to the current thread.
     *
     * @see #remove()
     */
    public static void clean() {
        CURRENT.remove();
    }

    /**
     *  uses mechanism to detect cancellable thread based
     * on testing {@link #CURRENT} property. It has to be set to any non-null
     * value to signalize cancellable thread. This method sets empty
     * implementation of  class to signalize that.
     */
    public static void setCancellable() {
        // if not set
        if (CURRENT.get() == null) {
            // store emptry instance
            new ZkCancellable().store();
        }
    }
}
