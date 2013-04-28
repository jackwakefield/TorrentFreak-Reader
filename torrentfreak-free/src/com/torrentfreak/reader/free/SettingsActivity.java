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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;
import com.google.inject.Inject;
import com.torrentfreak.reader.free.articles.ArticleStorage;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SettingsActivity extends RoboSherlockPreferenceActivity {
    /**
     * The activity result used to inform the parent activity the articles have been invalidated.
     */
    public static final int RESULT_ARTICLES_INVALIDATED = 1;

    /**
     * The settings key used when determining whether the user wishes to be prompted when exiting
     * the application.
     */
    public static final String SETTING_CONFIRM_EXIT = "confirm_exit";

    /**
     * The settings key used when determining whether the user wishes for articles to be marked
     * as read once they've been viewed.
     */
    public static final String SETTING_MARK_AS_READ = "mark_as_read";

    /**
     * The settings key used to clear the article history.
     */
    public static final String SETTING_CLEAR_ARTICLE_HISTORY = "clear_article_history";

    /**
     * The settings key used to clear the saved articles from the article storage database.
     */
    public static final String SETTING_CLEAR_SAVED_ARTICLES = "clear_saved_articles";

    /**
     * The settings key used to clear the WebKit asset cache.
     */
    public static final String SETTING_CLEAR_WEB_CACHE = "clear_web_cache";

    /**
     * The article storage database.
     */
    @Inject
    private ArticleStorage articleStorage;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // manually set the window background to the background pattern as no layout is set
        getWindow().setBackgroundDrawableResource(R.drawable.background_pattern);

        // setup the action bar, enabling the home button and setting the spaced logo
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.spaced_logo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.settings_activity_title);

        // add the preference resource to display the application settings
        addPreferencesFromResource(R.xml.preferences);

        // retrieve the individual button preferences and set the related click listeners
        final Preference clearArticleHistory = findPreference(SETTING_CLEAR_ARTICLE_HISTORY);
        clearArticleHistory.setOnPreferenceClickListener(onClearArticleHistoryClicked);

        final Preference clearArticlesCache = findPreference(SETTING_CLEAR_SAVED_ARTICLES);
        clearArticlesCache.setOnPreferenceClickListener(onClearArticlesCacheClicked);

        final Preference clearWebCache = findPreference(SETTING_CLEAR_WEB_CACHE);
        clearWebCache.setOnPreferenceClickListener(onClearWebCacheClicked);
    }

    @Override
    protected void onDestroy() {
        // clear the crouton notifications from the activity
        Crouton.clearCroutonsForActivity(this);

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // cloes the activity when the home button is pressed
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final OnPreferenceClickListener onClearArticleHistoryClicked
        = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(final Preference preference) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.clear_article_history_title)
                .setMessage(R.string.clear_article_history_message)
                .setNegativeButton(R.string.clear_article_history_negative, null)
                .setPositiveButton(R.string.clear_article_history_positive,
                    new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        // reset the read status on every stored article effectively clearing the
                        // article history
                        articleStorage.setAllArticlesAsUnread();

                        final Style style = new Style.Builder().setDuration(2000)
                            .setBackgroundColorValue(getResources().getColor(R.color.crouton_info))
                            .setHeight(LayoutParams.WRAP_CONTENT).build();

                        // display a notification informing the user the article history has been
                        // cleared
                        Crouton.makeText(SettingsActivity.this, R.string.article_history_cleared,
                            style).show();

                        // set the activity result information the parent activity the articles are
                        // invalidated and must be refreshed if being displayed
                        setResult(RESULT_ARTICLES_INVALIDATED);
                    }
                }
            );

            final AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
    };

    private final OnPreferenceClickListener onClearArticlesCacheClicked
        = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(final Preference preference) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.clear_article_database_title)
                .setMessage(R.string.clear_article_database_message)
                .setNegativeButton(R.string.clear_article_database_negative, null)
                .setPositiveButton(R.string.clear_article_database_positive,
                    new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        // remove all articles from the article storage database
                        articleStorage.removeAllArticles();

                        final Style style = new Style.Builder().setDuration(2000)
                            .setBackgroundColorValue(getResources().getColor(R.color.crouton_info))
                            .setHeight(LayoutParams.WRAP_CONTENT).build();

                        // display a notification informing the user the article database has been
                        // cleared
                        Crouton.makeText(SettingsActivity.this, R.string.article_database_cleared,
                            style).show();

                        // set the activity result information the parent activity the articles are
                        // invalidated and must be refreshed if being displayed
                        setResult(RESULT_ARTICLES_INVALIDATED);
                    }
                }
            );

            final AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
    };

    private final OnPreferenceClickListener onClearWebCacheClicked =
        new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(final Preference preference) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.clear_web_cache_title)
                .setMessage(R.string.clear_web_cache_message)
                .setNegativeButton(R.string.clear_web_cache_negative, null)
                .setPositiveButton(R.string.clear_web_cache_positive,
                    new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        final WebView webView = new WebView(SettingsActivity.this);
                        webView.clearCache(true);

                        final Style style = new Style.Builder().setDuration(2000)
                            .setBackgroundColorValue(getResources().getColor(R.color.crouton_info))
                            .setHeight(LayoutParams.WRAP_CONTENT).build();

                        // display a notification informing the user the WebKit asset  cache has
                        // been cleared
                        Crouton.makeText(SettingsActivity.this, R.string.web_cache_cleared, style)
                            .show();
                    }
                }
            );

            final AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
    };
}
