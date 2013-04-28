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

import java.lang.ref.WeakReference;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.torrentfreak.reader.free.AboutActivity;
import com.torrentfreak.reader.free.adapters.SlidingMenuAdapter;
import com.torrentfreak.reader.free.adapters.items.SlidingMenuItem;
import com.torrentfreak.reader.free.adapters.views.SlidingMenuItemView;
import com.torrentfreak.reader.free.categories.CategoryManager;
import com.torrentfreak.reader.free.categories.CategoryItem;
import com.torrentfreak.reader.free.LicensesActivity;
import com.torrentfreak.reader.free.R;
import com.torrentfreak.reader.free.SettingsActivity;

public class SlidingMenuFragment extends RoboSherlockListFragment implements
    CategoryManager.OnCategoryChangedListener {
    /**
     * The settings activity request identifier.
     */
    private static final int REQUEST_SETTINGS = 1;

    /**
     * The category manager used to change the selected category.
     */
    @Inject
    private CategoryManager categoryManager;

    /**
     * The adapter used to display the menu items.
     */
    private SlidingMenuAdapter adapter;

    /**
     * The weak reference to the articles invalidated listener, used when changing settings
     * affecting the validness of the article list.
     */
    private WeakReference<OnArticlesInvalidatedListener> listenerReference;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_menu, null);
    }

    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // create the sliding menu adapter
        adapter = new SlidingMenuAdapter(getActivity());

        final Resources resources = getResources();
        final String packageName = getActivity().getPackageName();

        // iterate through each category adding it to the sliding menu adapter
        for (final CategoryItem category : categoryManager.getCategories()) {
            // retrieve the resource ID from the icon name
            int resourceId = resources.getIdentifier(category.getIcon(), null, packageName);

            // add the menu item to the adapter
            adapter.add(new SlidingMenuItem(category.getName(), resourceId));
        }

        // add the non-category related menu items to the adapter
        adapter.add(new SlidingMenuItem(getString(R.string.settings_menu_item),
            R.drawable.ic_action_settings));
        adapter.add(new SlidingMenuItem(getString(R.string.about_menu_item),
            R.drawable.ic_action_info));
        adapter.add(new SlidingMenuItem(getString(R.string.licenses_menu_item),
            R.drawable.ic_action_business));

        // retrieve the selected category and manually invoke the category changed event listener
        final CategoryItem selectedCategory = categoryManager.getSelectedCategory();
        onCategoryChanged(selectedCategory);

        // set the list adapter to the sliding menu adapter
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        // add the category changed event listener
        categoryManager.addCategoryChangedListener(this);

        // determine whether a category has been selected
        if (categoryManager.getSelectedCategory() != null) {
            // manually invoke the category changed event listener
            onCategoryChanged(categoryManager.getSelectedCategory());
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        // remove the category changed event listener
        categoryManager.removeCategoryChangedListener(this);

        super.onPause();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        try {
            // attempt to assign the listener reference from the parent activity
            listenerReference = new WeakReference<OnArticlesInvalidatedListener>(
                    (OnArticlesInvalidatedListener)activity);
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticlesInvalidatedListener");
        }
    }

    @Override
    public void onListItemClick(final ListView listView, final View view, final int position,
        final long id) {
        final SlidingMenuItem item = adapter.getItem(position);

        // retrieve the menu item name
        final String name = item.getName();

        // determine the action to take using the menu item name
        // if no matching name is found interpret the menu item as a category
        if (name.equals(getString(R.string.settings_menu_item))) {
            // show the settings activity if the settings menu item has been selected
            final Intent intent = new Intent();
            intent.setClass(getActivity(), SettingsActivity.class);
            startActivityForResult(intent, REQUEST_SETTINGS);
        } else if (name.equals(getString(R.string.about_menu_item))) {
            // show the about activity if the about menu item has been selected
            final Intent intent = new Intent();
            intent.setClass(getActivity(), AboutActivity.class);
            startActivity(intent);
        } else if (name.equals(getString(R.string.licenses_menu_item))) {
            // show the licenses activity if the licenses menu item has been selected
            final Intent intent = new Intent();
            intent.setClass(getActivity(), LicensesActivity.class);
            startActivity(intent);
        } else {
            // retrieve the category using the menu item name and set it to the selected category
            final CategoryItem category = categoryManager.getCategoryByName(name);

            // ensure the category exists and set the category to the selected category
            if (category != null) {
               categoryManager.setSelectedCategory(category);
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // ensure the activity result was for the settings activity
        if (requestCode == REQUEST_SETTINGS) {
            // determine whether the result of the activity was that the articles were invalidated
            if (resultCode == SettingsActivity.RESULT_ARTICLES_INVALIDATED) {
                // retrieve the articles invalidated listener from the listener reference
                final OnArticlesInvalidatedListener listener = listenerReference.get();

                // ensure the listener still exist
                if (listener != null) {
                    // inform the listener the articles have been invalidated
                    listener.onArticlesInvalidated();
                }
            }
        }
    }

    public void onCategoryChanged(final CategoryItem category) {
        // ensure the category and adapters have been set
        if (category != null && adapter != null) {
            // set the selected category
            adapter.setSelectedCategory(category);
        }
    }

    public interface OnArticlesInvalidatedListener {
        void onArticlesInvalidated();
    }
}
