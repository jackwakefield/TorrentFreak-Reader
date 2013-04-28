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

package com.torrentfreak.reader.free.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.torrentfreak.reader.free.adapters.items.SlidingMenuItem;
import com.torrentfreak.reader.free.adapters.views.SlidingMenuItemView;
import com.torrentfreak.reader.free.categories.CategoryItem;
import com.torrentfreak.reader.free.R;

public class SlidingMenuAdapter extends ArrayAdapter<SlidingMenuItem> {
    private final LayoutInflater layoutInflater;

    public SlidingMenuAdapter(final Context context) {
        super(context, 0);

        // retrieve the layout inflator service
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSelectedCategory(final CategoryItem category) {
       final  String categoryName = category.getName();

        // iterate through each menu item setting the value determining whether the menu item is
        // selected based upon whether the menu items name matches the selected category name
        for (int i = 0; i < getCount(); i++) {
            final SlidingMenuItem menuItem = getItem(i);
            menuItem.setSelected(menuItem.getName().equals(categoryName));
        }

        // notify the data set has been changed to redraw the menu items
        notifyDataSetChanged();
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        SlidingMenuItemView view = null;

        // determine whether the view has previously been set
        if (convertView == null) {
            // inflate the sliding menu item view
            convertView = layoutInflater.inflate(R.layout.sliding_menu_item, null);

            // retrieve the menu item views
            final LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.layout);
            final TextView titleView = (TextView)convertView.findViewById(R.id.title);
            final ImageView iconView = (ImageView)convertView.findViewById(R.id.icon);

            // create the menu item view and set it to the view tag
            view = new SlidingMenuItemView(layout, titleView, iconView);
            convertView.setTag(view);
        } else {
            // retrieve the menu item view from the view tag
            view = (SlidingMenuItemView)convertView.getTag();
        }

        // retrieve the menu item from the given position
        final SlidingMenuItem menuItem = (SlidingMenuItem)getItem(position);

        // ensure the menu item exists, the current view menu item hasn't already been set or
        // whether the select state is different between the menu item and view
        if (menuItem != null && (menuItem != view.getMenuItem() ||
            menuItem.isSelected() != view.isSelected())) {
            view.setMenuItem(menuItem);
        }

        return convertView;
    }
}
