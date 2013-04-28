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
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import roboguice.inject.InjectView;

public class AboutActivity extends RoboSherlockActivity {
    /**
     * The text view for the credits related to the app.
     */
    @InjectView(R.id.app_credits)
    private TextView appCredits;

    /**
     * The text view for the credits related to the content.
     */
    @InjectView(R.id.content_credits)
    private TextView contentCredits;

    /**
     * The text view for the credits related to the backend libraries.
     */
    @InjectView(R.id.backend_credits)
    private TextView backendCredits;

    /**
     * The text view for the credits related to the generated styles.
     */
    @InjectView(R.id.style_credits)
    private TextView styleCredits;

    /**
     * The text view for the credits related to the user interface libraries.
     */
    @InjectView(R.id.interface_credits)
    private TextView interfaceCredits;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        // apply the link movement method to all appropriate text views to allow the user to click
        // any anchors
        final MovementMethod movementMethod = LinkMovementMethod.getInstance();
        appCredits.setMovementMethod(movementMethod);
        contentCredits.setMovementMethod(movementMethod);
        styleCredits.setMovementMethod(movementMethod);
        backendCredits.setMovementMethod(movementMethod);
        interfaceCredits.setMovementMethod(movementMethod);

        // setup the action bar, setting the logo and title and enabling the home button
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.spaced_logo);
        actionBar.setTitle(R.string.about_activity_title);
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
