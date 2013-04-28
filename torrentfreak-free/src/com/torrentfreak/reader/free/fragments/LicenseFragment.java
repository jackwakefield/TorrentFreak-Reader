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

package com.torrentfreak.reader.free.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.torrentfreak.reader.free.R;
import roboguice.inject.InjectView;

public class LicenseFragment extends RoboSherlockFragment {
    /**
     * The extra key used to pass the license title to the fragment.
     */
    public static final String EXTRA_TITLE =
        "com.torrentfreak.reader.free.fragments.LicenseFragment.TITLE";

    /**
     * The extra key used to pass the license file name to the fragment.
     */
    public static final String EXTRA_FILE_NAME =
        "com.torrentfreak.reader.free.fragments.LicenseFragment.FILE_NAME";

    /**
     * The saved state key used to retain the licenses title.
     */
    private static final String SAVED_STATE_TITLE = "title";

    /**
     * The saved state key used to retain the licenses file name.
     */
    private static final String SAVED_STATE_FILE_NAME = "file_name";

    /**
     * The format of the license file paths.
     */
    private static final String FILE_PATH_FORMAT = "file:///android_asset/licenses/%s";

    /**
     * The web view used to display the license contents.
     */
    @InjectView(R.id.web_view)
    private WebView webView;

    /**
     * The license title.
     */
    private String title;

    /**
     * The license file name.
     */
    private String fileName;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_license, null);
    }

    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // determine whether a saved instance state exists, indicating the fragment has existed
        // previously
        if (savedInstanceState == null) {
            // retrieve the fragment arguments and set the title and file name
            final Bundle bundle = getArguments();
            title = bundle.getString(EXTRA_TITLE);
            fileName = bundle.getString(EXTRA_FILE_NAME);
        } else {
            // retrieve the retained title and file name
            title = savedInstanceState.getString(SAVED_STATE_TITLE);
            fileName = savedInstanceState.getString(SAVED_STATE_FILE_NAME);
        }

        webView.setBackgroundColor(getResources().getColor(R.color.license_background));

        final String path = String.format(FILE_PATH_FORMAT, fileName);
        webView.loadUrl(path);
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // save the tile and file name to be retrieved when the fragment is recreated
        savedInstanceState.putString(SAVED_STATE_TITLE, title);
        savedInstanceState.putString(SAVED_STATE_FILE_NAME, fileName);
    }

    public String getTitle() {
        return title;
    }
}
