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
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.torrentfreak.reader.free.fragments.ArticleCommentsFragment;
import com.torrentfreak.reader.free.fragments.ArticleContentFragment;
import com.torrentfreak.reader.free.R;

public class ArticleFragmentAdapter extends FragmentPagerAdapter {
    /**
     * The number of pages.
     */
    private static final int PAGE_COUNT = 2;

    /**
     * The article page index.
     */
    private static final int PAGE_ARTICLE = 0;

    /**
     * The article comments index.
     */
    private static final int PAGE_COMMENTS = 1;

    /**
     * The adapter context.
     */
    private final Context context;

    /**
     * The article URL.
     */
    private final String url;

    public ArticleFragmentAdapter(final Context context, final String url,
        final FragmentManager fragmentManager) {
        super(fragmentManager);

        this.context = context;
        this.url = url;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        final Resources resources = context.getResources();

        // retrieve the page title based on the page position
        switch (position) {
            case PAGE_ARTICLE:
                return resources.getString(R.string.article_content_title);
            case PAGE_COMMENTS:
                return resources.getString(R.string.article_comments_title);
        }

        return null;
    }

    @Override
    public Fragment getItem(final int position) {
        final Bundle bundle = new Bundle();

        Fragment fragment = null;

        // retrieve the page fragment based on the page position
        switch (position) {
            case PAGE_ARTICLE:
                bundle.putString(ArticleContentFragment.EXTRA_URL, url);
                fragment = new ArticleContentFragment();
                break;
            case PAGE_COMMENTS:
                bundle.putString(ArticleCommentsFragment.EXTRA_URL, url);
                fragment = new ArticleCommentsFragment();
                break;
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            return fragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
