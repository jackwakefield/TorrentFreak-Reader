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
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.slidingmenu.lib.SlidingMenu;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.categories.CategoryItem;
import com.torrentfreak.reader.free.categories.CategoryManager;
import com.torrentfreak.reader.free.fragments.ArticleListFragment;
import com.torrentfreak.reader.free.fragments.SlidingMenuFragment;
import com.torrentfreak.reader.free.SettingsActivity;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class MainActivity extends RoboSherlockFragmentActivity implements
    CategoryManager.OnCategoryChangedListener, ArticleListFragment.OnArticleSelectedListener,
    SlidingMenuFragment.OnArticlesInvalidatedListener {
    /**
     * The category manager used to be informed of when the selected category changes.
     */
    @Inject
    private CategoryManager categoryManager;

    /**
     * The sliding menu mainly used to select the current category.
     */
    private SlidingMenu slidingMenu;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the window pixel format to fix an alpha blending issue in Android 2.2 and under
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setContentView(R.layout.activity_main);

        final SlidingMenuFragment slidingMenuFragment =
            (SlidingMenuFragment)getSupportFragmentManager().findFragmentById(R.id.menu_fragment);

        final ActionBar actionBar = getSupportActionBar();

        // ensure the sliding menu fragment doesn't exist or that it is isn't the layout, if it
        // doesn't exist then the device is a phone, if it exists then the device is most likely
        // a tablet in landscape mode with the sliding menu permanently displayed
        if (slidingMenuFragment == null || !slidingMenuFragment.isInLayout()) {
            // setup the sliding menu on the left hand side of the activity
            slidingMenu = new SlidingMenu(this);
            slidingMenu.setMode(SlidingMenu.LEFT);
            slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
            slidingMenu.setShadowDrawable(R.drawable.shadow);
            slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            slidingMenu.setFadeDegree(0.35f);
            slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
            slidingMenu.setMenu(R.layout.sliding_menu_frame);

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new SlidingMenuFragment()).commit();

            // setup the action bar enabling the home button to toggle the visibility of the sliding
            // menu
            actionBar.setIcon(R.drawable.spaced_logo);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            actionBar.setIcon(R.drawable.right_spaced_logo);
        }

        // determine whether the activity has saved instance data, indicating the activity has
        // existed before
        if (savedInstanceState != null) {
            setActionBarTitle(categoryManager.getSelectedCategory());
        } else {
            // if there is no saved instance but a category has been selected, the application
            // has been restarted from the multi-task view and needs to be manually refreshed
            if (categoryManager.getSelectedCategory() != null) {
                onCategoryChanged(categoryManager.getPrimaryCategory());
            }
        }
    }

    @Override
    protected void onResume() {
        // add an event listener to receive updates for when the category has been changed
        categoryManager.addCategoryChangedListener(this);

        // determine whether no category has been selected
        if (categoryManager.getSelectedCategory() == null) {
            // set the selected category as the primary category
            categoryManager.setSelectedCategory(categoryManager.getPrimaryCategory());
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        // remove the category changed event listener
        categoryManager.removeCategoryChangedListener(this);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // remove all crouton notifications for the activity
        Crouton.clearCroutonsForActivity(this);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // inflate the main menu
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // toggle the sliding menu visibility when the home button is pressed
                if (slidingMenu != null) {
                   slidingMenu.toggle();
                }

                return true;
            case R.id.refresh:
                // refresh the article list when the refresh button is pressed
                refresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // determine whether the sliding menu is currently being displayed
        if (slidingMenu != null && slidingMenu.isMenuShowing()) {
            // hide the sliding menu
            slidingMenu.toggle();
        } else {
            // retrieve the application preferences
            final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

            // determine whether the user wishes the app to display an alert dialog before exiting
            // the application
            if (preferences.getBoolean(SettingsActivity.SETTING_CONFIRM_EXIT, false)) {
                // build the alert dialog, closing the application if the "Exit" button is pressed
                final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_exit_title)
                    .setNegativeButton(R.string.confirm_exit_negative, null)
                    .setPositiveButton(R.string.confirm_exit_positive,
                        new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int which) {
                            MainActivity.super.onBackPressed();
                        }
                    }
                );

                final AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        // determine whether the hard Menu button was pressed, if so, toggle the sliding menu
        // visibility
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            slidingMenu.toggle();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    public void onCategoryChanged(final CategoryItem category) {
        // refresh the article list
        refresh();

        // set the action bar title to the name of the category selected
        setActionBarTitle(category);
    }

    public void onArticlesInvalidated() {
        // refresh the article list
        refresh();
    }

    private void setActionBarTitle(final CategoryItem category) {
        // ensure the selected category exists
        if (category != null) {
            // set the action bar title to the selected category name
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(category.getName());
        }
    }

    private void refresh() {
        // re-create the article list fragment
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.list_frame, new ArticleListFragment()).commit();

        // determine whether the sliding menu is currently being displayed
        if (slidingMenu != null && slidingMenu.isMenuShowing()) {
            // hide the sliding menu
           slidingMenu.toggle();
        }
    }

    public void onArticleSelected(final ArticleItem article) {
        // create an intent to view the selected article, passing the URL
        final Intent intent = new Intent();
        intent.setClass(this, ArticleActivity.class);
        intent.putExtra(ArticleActivity.EXTRA_URL, article.getUrl());

        // start the view article activity
        startActivity(intent);
    }
}
