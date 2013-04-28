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

package com.torrentfreak.reader.free.widgets;

import java.lang.CharSequence;
import java.util.List;
import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;
import com.google.inject.Inject;
import com.torrentfreak.reader.free.categories.CategoryItem;
import com.torrentfreak.reader.free.categories.CategoryManager;
import com.torrentfreak.reader.free.R;
import com.torrentfreak.reader.free.widgets.StackWidgetProvider;

public class StackWidgetActivity extends RoboSherlockPreferenceActivity {
    /**
     * The settings key used to determine which category to retrieve articles from.
     */
    public static final String SETTING_CATEGORY = "category";

    /**
     * The settings key used to determine the number of milliseconds between retrieving the article
     * list.
     */
    public static final String SETTING_UPDATE_INTERVAL = "update_interval";

    /**
     * The category manager.
     */
    @Inject
    private CategoryManager categoryManager;

    /**
     * The ID of the widget.
     */
    private int widgetId;

    /**
     * The button used to create the widget.
     */
    private Button createButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // manually set the window background to the background pattern as no layout is set
        getWindow().setBackgroundDrawableResource(R.drawable.background_pattern);

        widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();

        if (extras != null) {
            // retrieve the widget ID from the intent
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // if no widget ID has been specified or an invalid widget ID has been given, close the
        // activity
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // setup the action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.stack_widget_activity_title);

        // add the preference resource to display the widget settings
        addPreferencesFromResource(R.xml.stack_widget_preferences);

        final List<CategoryItem> categories = categoryManager.getCategories();
        final CharSequence[] categoryEntries = new CharSequence[categories.size()];
        final CharSequence[] categoryEntryValues = new CharSequence[categories.size()];

        // create a list of category IDs and names used to select a category for the widget
        for (int i = 0; i < categories.size(); i++) {
            final CategoryItem category = categories.get(i);
            categoryEntries[i] = category.getName();
            categoryEntryValues[i] = String.valueOf(category.getId());
        }

        // apply the category IDs and names to the category preference
        final ListPreference categoryPreference = (ListPreference)findPreference(SETTING_CATEGORY);
        categoryPreference.setEntries(categoryEntries);
        categoryPreference.setEntryValues(categoryEntryValues);

        final CategoryItem primaryCategory = categoryManager.getPrimaryCategory();

        // if one exists, set the current selected category to the primary category
        if (primaryCategory != null) {
            categoryPreference.setValue(String.valueOf(primaryCategory.getId()));
        }

        // create the create widget button and apply the click listener
        createButton = new Button(this);
        createButton.setText(R.string.create_widget);
        createButton.setOnClickListener(createWidget);

        // add create button to the footer of the list view
        final ListView listView = getListView();
        listView.addFooterView(createButton);
    }

    private final View.OnClickListener createWidget = new View.OnClickListener() {
         public void onClick(final View view) {
            final SharedPreferences sharedPreference = getBaseContext().getSharedPreferences(
                StackWidgetProvider.getSharedPreferencesNameForWidget(widgetId), 0);
            final SharedPreferences.Editor sharedPreferenceEditor = sharedPreference.edit();

            // set the category setting value to the ID of the selected category
            final ListPreference categoryPreference =
                (ListPreference)findPreference(SETTING_CATEGORY);
            sharedPreferenceEditor.putInt(SETTING_CATEGORY,
                Integer.valueOf(categoryPreference.getValue()));

            // set the update interval setting value to the millisecond value of the selected
            // interval
            final ListPreference updateIntervalPreference =
                (ListPreference)findPreference(SETTING_UPDATE_INTERVAL);
            sharedPreferenceEditor.putLong(SETTING_UPDATE_INTERVAL,
                Long.valueOf(updateIntervalPreference.getValue()));

            // save the settings
            sharedPreferenceEditor.commit();

            // create the result intent to inform the callee the widget has been created
            final Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            setResult(RESULT_OK, resultValue);

            finish();
         }
     };
}
