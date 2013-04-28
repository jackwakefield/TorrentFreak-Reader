/*
 * Copyright (C) 2013 Jack Wakefield
 *
 * This file is part of TorrentFreak Reader.
 *
 * TorrentFreak Reader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TorrentFreak Reader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TorrentFreak Reader.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.torrentfreak.reader.free.helpers;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

public class WeakReferenceHelper {
    public static <T> void removeReference(final List<WeakReference<T>> list, final T reference) {
        // loop through each weak reference
        for (final Iterator<WeakReference<T>> iterator = list.iterator(); iterator.hasNext();) {
            final WeakReference<T> weakReference = iterator.next();

            // determine whether the reference matches the specified reference
            if (weakReference.get() == reference) {
                // remove the weak reference entry and break out of the loop
                iterator.remove();
                break;
            }
        }
    }

    public static <T> boolean containsReference(final List<WeakReference<T>> list,
        final T reference) {
        // loop through each weak reference
        for (final WeakReference<T> weakReference : list) {
            // determine whether the reference matches the specified reference
            if (weakReference.get() == reference) {
                return true;
            }
        }

        return false;
    }
}
