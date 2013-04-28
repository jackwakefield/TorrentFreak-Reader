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

import java.util.List;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.torrentfreak.reader.free.adapters.views.ArticleItemView;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.helpers.FontHelper;
import com.torrentfreak.reader.free.R;

public class ArticleListAdapter extends BaseAdapter {
    /**
     * The adapter context.
     */
    private final Context context;

    /**
     * The article item list.
     */
    private final List<ArticleItem> items;

    /**
     * The layout inflator service.
     */
    private final LayoutInflater layoutInflater;

    public ArticleListAdapter(final Context context, final List<ArticleItem> items) {
        this.context = context;
        this.items = items;

        // retrieve the layout inflator service
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(final int position) {
        return position < items.size();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(final int position) {
        // ensure the position is within the item range
        if (position >= 0 && position < getCount()) {
            return items.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        ArticleItemView view = null;

        // retrieve the article item from the given position
        final ArticleItem article = (ArticleItem)getItem(position);

        if (article != null) {
            // determine whether the view has previously been set
            if (convertView == null) {
                // inflate the article item view
                convertView = layoutInflater.inflate(R.layout.article_item, null);

                // retrieve the article item views
                final LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.layout);
                final TextView titleView = (TextView)convertView.findViewById(R.id.title);
                final TextView dateView = (TextView)convertView.findViewById(R.id.date);
                final ImageView dateIcon = (ImageView)convertView.findViewById(R.id.date_icon);
                final TextView commentCountView =
                    (TextView)convertView.findViewById(R.id.comment_count);
                final ImageView commentCountIcon =
                    (ImageView)convertView.findViewById(R.id.comment_count_icon);

                // set the font to roboto light manually if the OS doesn't include it
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    titleView.setTypeface(FontHelper.getRobotoLight(context));
                }

                // create the article item view and set it to the view tag
                view = new ArticleItemView(context, layout, titleView, dateView, dateIcon,
                    commentCountView, commentCountIcon);
                convertView.setTag(view);
            } else {
                // retrieve the article item view from the view tag
                view = (ArticleItemView)convertView.getTag();
            }

            // ensure the article item hasn't already been or whether the read state is different
            // between the article item and view
            if (article != view.getArticle() || article.isRead() != view.isRead()) {
                view.setArticle(article);
            }
        }

        return convertView;
    }
}
