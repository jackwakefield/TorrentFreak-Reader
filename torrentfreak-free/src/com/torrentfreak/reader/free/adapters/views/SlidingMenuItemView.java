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

package com.torrentfreak.reader.free.adapters.views;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.torrentfreak.reader.free.adapters.items.SlidingMenuItem;
import com.torrentfreak.reader.free.R;

public class SlidingMenuItemView {
    /**
     * The item layout.
     */
    private final LinearLayout layout;

    /**
     * The menu title text view.
     */
    private final TextView titleView;

    /**
     * The menu icon image view.
     */
    private final ImageView iconView;

    /**
     * The current sliding menu item.
     */
    private SlidingMenuItem menuItem;

    /**
     * Determines whether the menu item has been selected.
     */
    private boolean selected;

    public SlidingMenuItemView(final LinearLayout layout, final TextView titleView,
        final ImageView iconView) {
        this.layout = layout;
        this.titleView = titleView;
        this.iconView = iconView;
    }

    public SlidingMenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(final SlidingMenuItem menuItem) {
        // set the menu item and select state
        this.menuItem = menuItem;
        selected = menuItem.isSelected();

        // determine whether the menu item has been selected and apply the appropriate background
        // colour to the layout
        if (selected) {
            layout.setBackgroundResource(R.color.sliding_menu_selected);
        } else {
            layout.setBackgroundResource(R.color.sliding_menu_normal);
        }

        titleView.setText(menuItem.getName());
        iconView.setImageResource(menuItem.getIcon());
    }

    public boolean isSelected() {
        return selected;
    }
}
