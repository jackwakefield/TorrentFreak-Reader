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

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.torrentfreak.reader.free.adapters.items.LicenseItem;
import com.torrentfreak.reader.free.fragments.LicenseFragment;

public class LicenseFragmentAdapter extends FragmentPagerAdapter {
    /**
     * The license list.
     */
    private final ArrayList<LicenseItem> licenses;

    public LicenseFragmentAdapter(final FragmentManager fragmentManager, final Context context) {
        super(fragmentManager);

        licenses = new ArrayList<LicenseItem>();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        // ensure the position is within the item range
        if (position >= 0 && position < licenses.size()) {
            // retrieve the specified license and return the license title as the page title
            final LicenseItem item = licenses.get(position);
            return item.getTitle();
        }

        return null;
    }

    @Override
    public Fragment getItem(final int position) {
        // ensure the position is within the item range
        if (position >= 0 && position < licenses.size()) {
            final LicenseItem item = licenses.get(position);

            // create the bundle to pass to the fragment specifying the license title and file name
            final Bundle bundle = new Bundle();
            bundle.putString(LicenseFragment.EXTRA_TITLE, item.getTitle());
            bundle.putString(LicenseFragment.EXTRA_FILE_NAME, item.getFileName());

            // create the license fragment specifying the bundle as the argument
            final LicenseFragment fragment = new LicenseFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return licenses.size();
    }

    public void add(final String title, final String fileName) {
        // add the license item to the list and notify the underlying adapter the data set has
        // changed
        licenses.add(new LicenseItem(title, fileName));
        notifyDataSetChanged();
    }
}
