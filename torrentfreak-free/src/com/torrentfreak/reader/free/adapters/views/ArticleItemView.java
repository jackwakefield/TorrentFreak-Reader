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

package com.torrentfreak.reader.free.adapters.views;

import java.lang.Integer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.R;

public class ArticleItemView {
    /**
     * The application resources.
     */
    private final Resources resources;

    /**
     * The article item layout.
     */
    private final LinearLayout layout;

    /**
     * The article title text view.
     */
    private final TextView titleView;

    /**
     * The article date text view.
     */
    private final TextView dateView;

    /**
     * The article date icon image view.
     */
    private final ImageView dateIcon;

    /**
     * The article comment count text view.
     */
    private final TextView commentCountView;

    /**
     * The article comment count icon image view.
     */
    private final ImageView commentCountIcon;

    /**
     * The original article title colours.
     */
    private final ColorStateList originalTitleColours;

    /**
     * The original article date colours.
     */
    private final ColorStateList originalDateColours;

    /**
     * The original article comment count colours.
     */
    private final ColorStateList originalCommentCountColours;

    /**
     * The current article item.
     */
    private ArticleItem article;

    /**
     * Determines whether the article item has been read.
     */
    private boolean read;

    public ArticleItemView(final Context context, final LinearLayout layout,
        final TextView titleView, final TextView dateView, final ImageView dateIcon,
        final TextView commentCountView, final ImageView commentCountIcon) {
        resources = context.getResources();
        this.layout = layout;
        this.titleView = titleView;
        this.dateView = dateView;
        this.dateIcon = dateIcon;
        this.commentCountView = commentCountView;
        this.commentCountIcon = commentCountIcon;

        // set the original text view colours used when changing the read state
        originalTitleColours = titleView.getTextColors();
        originalDateColours = dateView.getTextColors();
        originalCommentCountColours = commentCountView.getTextColors();
    }

    public ArticleItem getArticle() {
        return article;
    }

    public void setArticle(final ArticleItem article) {
        // set the article item and read state
        this.article = article;
        read = article.isRead();

        titleView.setText(article.getTitle());
        dateView.setText(article.getFormattedDate());

        String commentCount = String.valueOf(article.getCommentCount()) + " ";

        // append the comment count text depending on whether the value is plural or singular
        if (article.getCommentCount() == 1) {
            commentCount += resources.getString(R.string.article_comments_suffix);
        } else {
            commentCount += resources.getString(R.string.article_comments_suffix_plural);
        }

        commentCountView.setText(commentCount);

        // determine whether the article has been read
        if (read) {
            // change the transparency of each of the text and image views to indicate the article
            // has been read
            titleView.setTextColor(originalTitleColours.withAlpha(150));
            dateView.setTextColor(originalDateColours.withAlpha(150));
            commentCountView.setTextColor(originalCommentCountColours.withAlpha(150));
            dateIcon.setAlpha(150);
            commentCountIcon.setAlpha(150);
        } else {
            // restore the original colours and transparency for the text and image views to
            // indicate the article hasn't been read
            titleView.setTextColor(originalTitleColours);
            dateView.setTextColor(originalDateColours);
            commentCountView.setTextColor(originalCommentCountColours);
            dateIcon.setAlpha(255);
            commentCountIcon.setAlpha(255);
        }
    }

    public boolean isRead() {
        return read;
    }
}
