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

package com.torrentfreak.reader.free;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.torrentfreak.reader.free.adapters.LicenseFragmentAdapter;
import com.viewpagerindicator.TitlePageIndicator;
import roboguice.inject.InjectView;

public class LicensesActivity extends RoboSherlockFragmentActivity {
    /**
     * The view pager enabling the user to switch between licenses.
     */
    @InjectView(R.id.view_pager)
    private ViewPager viewPager;

    /**
     * The title page indicator to display the license names.
     */
    @InjectView(R.id.title_page_indicator)
    private TitlePageIndicator titleIndicator;

    /**
     * The license fragment adapter used to add the display individual licenses uesd by the app
     * and the components used.
     */
    private LicenseFragmentAdapter fragmentAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_licenses);

        // create the license fragment adapter, adding each license used by components of the
        // application
        fragmentAdapter = new LicenseFragmentAdapter(getSupportFragmentManager(), this);
        fragmentAdapter.add(getString(R.string.gplv3_license), "gpl-3.html");
        fragmentAdapter.add(getString(R.string.ccbync3_license), "cc-by-nc-3.0.html");
        fragmentAdapter.add(getString(R.string.ccby3_license), "cc-by-3.0.html");
        fragmentAdapter.add(getString(R.string.apache2_license), "apache-2.html");
        fragmentAdapter.add(getString(R.string.mit_license), "mit.html");

        // setup the view pager and title indicator
        viewPager.setAdapter(fragmentAdapter);
        titleIndicator.setViewPager(viewPager);

        // setup the action bar, setting the title and enabling the home button to close the
        // activity
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.spaced_logo);
        actionBar.setTitle(R.string.licenses_activity_title);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // close the activity if the home button is pressed
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
