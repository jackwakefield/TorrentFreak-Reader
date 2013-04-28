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

package com.torrentfreak.reader.free.adapters.items;

public class SlidingMenuItem {
    /**
     * The sliding menu item name.
     */
    private final String name;

    /**
     * The sliding menu item icon.
     */
    private final int icon;

    /**
     * Determines whether the item has been selected.
     */
    private boolean selected;

    public SlidingMenuItem(final String name, final int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
