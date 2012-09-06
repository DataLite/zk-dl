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

/**
 * <p>Instance of this class is bound to a thread which is bound to a user.
 * Objects implementing {@link ZkCancellable} are considered to allow cancel
 * running operation in their thread. Methods on such objects are called from
 * another threads. Implementations of this interface should cancel executing
 * database query, interrupt long calculation etc.</p>
 *
 *
 * <p>This command interrupts the thread to be bound with.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface ZkCancelCommand {

    /**
     * Cancel running thread which it is bound with.
     */
    void cancel();
}
