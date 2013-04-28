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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.torrentfreak.reader.free.adapters.ArticleFragmentAdapter;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.articles.ArticleStorage;
import com.torrentfreak.reader.free.fragments.ArticleCommentsFragment;
import com.torrentfreak.reader.free.fragments.ArticleContentFragment;
import com.torrentfreak.reader.free.R;
import com.torrentfreak.reader.free.SettingsActivity;
import com.viewpagerindicator.TitlePageIndicator;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.inject.InjectView;

public class ArticleActivity extends RoboSherlockFragmentActivity {
    /**
     * The extra key used to pass the articles URL to the activity.
     */
    public static final String EXTRA_URL = "com.torrentfreak.reader.free.ArticleActivity.URL";

    /**
     * The extra key used to inform the activity whether or not it has a parent.
     */
    public static final String EXTRA_SINGLE_ACTIVITY =
        "com.torrentfreak.reader.free.ArticleActivity.SINGLE_ACTIVITY";

    /**
     * The saved state key used to retain the articles URL.
     */
    private static final String SAVED_STATE_URL = "url";

    /**
     * The saved state key used to retain the value determining whether the activity has a parent.
     */
    private static final String SAVED_STATE_SINGLE_ACTIVITY = "single_activity";

    /**
     * The article storage database manager.
     */
    @Inject
    private ArticleStorage articleStorage;

    /**
     * The fragment adapter to display the article contents and comments.
     */
    private ArticleFragmentAdapter fragmentAdapter;

    /**
     * The URL of the article.
     */
    private String url;

    /**
     * Determines whether the instance of this activity has a parent.
     */
    private boolean singleActivity;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);

        // determine whether a saved instance state exists, indicating the activity has existed
        // previously
        if (savedInstanceState == null) {
            // retrieve the parameters passed to the activity
            final Bundle extras = getIntent().getExtras();

            // ensure the intent parameters exist
            if (extras != null) {
                // ensure a URL was specified
                if (extras.containsKey(EXTRA_URL)) {
                    url = extras.getString(EXTRA_URL);
                }

                // determine whether a parameter was given specifying whether the activity has a
                // parent
                if (extras.containsKey(EXTRA_SINGLE_ACTIVITY)) {
                    singleActivity = extras.getBoolean(EXTRA_SINGLE_ACTIVITY);
                }
            }

            // if no URL was specified as a parameter, retrieve the possible data string used when
            // the activity has been opened using by a third-party intent such as handling a URL
            // opened from the browser
            if (url == null) {
                url = getIntent().getDataString();

                // specify that the activity has no parent as the activity has been started by
                // a third-party app
                singleActivity = true;
            }

            // ensure a URL was specified either as a parameter or a data string
            if (url != null) {
                // retrieve the article from the database
                final ArticleItem article = articleStorage.getArticleByUrl(url);

                // retrieve the application preferences
                final SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(this);

                // ensure the article exists and determine whether the user has requested articles
                // be marked as read once viewed
                if (article != null &&
                    preferences.getBoolean(SettingsActivity.SETTING_MARK_AS_READ, true)) {
                    // mark the article as read in the database
                    articleStorage.setArticleAsRead(article);
                }
            }
        } else {
            // retrieve the retained article URL and parent state
            url = savedInstanceState.getString(SAVED_STATE_URL);
            singleActivity = savedInstanceState.getBoolean(SAVED_STATE_SINGLE_ACTIVITY);
        }

        // determine whether a URL has been specified
        if (url == null) {
            // finish the activity as there's no URL of an article to view
            finish();
            return;
        }

        final ActionBar actionBar = getSupportActionBar();

        // ensure the home button is only enabled and the wider logo only displayed when the
        // activity has a parent and therefore has an activity to fall back to
        if (!singleActivity) {
            // setup the action bar enabling the home button to be used to finish the activity
            actionBar.setIcon(R.drawable.spaced_logo);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // attempt to retrieve the stored article from the given URL
        final ArticleItem article = articleStorage.getArticleByUrl(url);

        // ensure the article exists and a title has been set
        if (article != null && article.getTitle() != null) {
            // if the article does exist, set the action bar title as the article title
            // if the article doesn't exist, the title will be set by the content fragment when the
            // article content has been retrieved
            actionBar.setTitle(article.getTitle());
        }

        // set the fragments or create the fragment adapter, depending on the layout used
        setFragments();
    }

    @Override
    protected void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // save the articles URL and parent state to be retrieved when the activity is recreated
        savedInstanceState.putString(SAVED_STATE_URL, url);
        savedInstanceState.putBoolean(SAVED_STATE_SINGLE_ACTIVITY, singleActivity);
    }

    @Override
    protected void onDestroy() {
        // clear all notifications for this activity
        Crouton.clearCroutonsForActivity(this);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // inflate the article activity menu
        getSupportMenuInflater().inflate(R.menu.article_menu, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        // retrieve the application preferences
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // ensure the activity has no parent and determine whether the user wishes the app to
        // display an alert dialog before exiting the application (as there is no parent activity
        // to fall back to)
        if (singleActivity &&
            preferences.getBoolean(SettingsActivity.SETTING_CONFIRM_EXIT, false)) {
            // build the alert dialog, closing the application if the "Exit" button is pressed
            final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_exit_title)
                .setNegativeButton(R.string.confirm_exit_negative, null)
                .setPositiveButton(R.string.confirm_exit_positive,
                    new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        ArticleActivity.super.onBackPressed();
                    }
                }
            );

            // create the dialog and show it to the user
            final AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void setFragmentAdapter() {
        // create the fragment adatper to split the article content and comments
        fragmentAdapter = new ArticleFragmentAdapter(this, url, getSupportFragmentManager());

        final ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(fragmentAdapter);

        final TitlePageIndicator titlePageIndicator =
            (TitlePageIndicator)findViewById(R.id.title_page_indicator);
        titlePageIndicator.setViewPager(viewPager);
    }

    private void setFragments() {
        // determine whether the article frame exists, indicating the article contents and comments
        // are to be displayed side-by-side
        if (findViewById(R.id.article_frame) != null) {
            // create the bundle to be passed to the article content fragment specifying the URL
            final Bundle articleBundle = new Bundle();
            articleBundle.putString(ArticleContentFragment.EXTRA_URL, url);

            // create the article fragment passing the bundle and adding it to the article frame
            // layout
            ArticleContentFragment articleFragment = new ArticleContentFragment();
            articleFragment.setArguments(articleBundle);

            getSupportFragmentManager().beginTransaction().
                replace(R.id.article_frame, articleFragment).commit();

            // create the bundle to be passed to the article comments fragment specifying the URL
            final Bundle commentsBundle = new Bundle();
            commentsBundle.putString(ArticleCommentsFragment.EXTRA_URL, url);

            // create the article comments fragment passing the bundle and adding it to the
            // comments frame layout
            ArticleCommentsFragment commentsFragment = new ArticleCommentsFragment();
            commentsFragment.setArguments(commentsBundle);

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.comments_frame, commentsFragment).commit();
        } else {
            // if the article frame doesn't exist, create the fragment adapter
            setFragmentAdapter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // close the activity if the home button has been pressed
                finish();
                return true;
            case R.id.refresh:
                // set the fragments or create the fragment adapter, depending on the layout used,
                // when the refresh button has been pressed
                setFragments();
                return true;
            case R.id.share:
                // create the article sharing intent setting the text as the articles URL if the
                // share button has been pressed
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, url);

                // retrieve the article associated with the URL from the database
                final ArticleItem article = articleStorage.getArticleByUrl(url);

                // ensure the article retrieved exists in the database
                if (article != null) {
                    // set the subject of the share intent to the articles title
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
                }

                // start the chooser activity to select an application to use to share the article
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_article)));

                return true;
            case R.id.open:
                // create a view intent setting the URL of the article to allow the user to view
                // the article in a web browser if the open in browser menu item has been selected
                final Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setData(Uri.parse(url));
                startActivity(viewIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
