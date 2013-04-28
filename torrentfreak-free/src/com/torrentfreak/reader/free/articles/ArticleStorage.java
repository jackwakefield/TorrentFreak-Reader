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

package com.torrentfreak.reader.free.articles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.categories.CategoryItem;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ArticleStorage extends SQLiteOpenHelper {
    /**
     * The name of the database.
     */
    private static final String DATABASE_NAME = "torrentfreak";

    /**
     * The current database version.
     */
    private static final int DATABASE_VERSION = 5;

    /**
     * The articles table name.
     */
    private static final String TABLE_ARTICLES = "articles";

    @Inject
    public ArticleStorage(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // create the articles table
        db.execSQL("CREATE TABLE " + TABLE_ARTICLES + " (id INTEGER PRIMARY KEY, category NUMERIC, title TEXT, author TEXT, date TEXT, comment_count NUMERIC, url TEXT, content TEXT, read NUMERIC, page_order NUMERIC);");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // drop the articles table and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        onCreate(db);
    }

    public List<ArticleItem> getArticles(final String selection, final String[] selectionArguments,
        final String limit) {
        final SQLiteDatabase db = getReadableDatabase();

        // retrieve the article data from the articles table using the selection arguments and
        // specified limit
        final Cursor cursor = db.query(TABLE_ARTICLES, new String[] {
            "id", "category", "title", "author", "date", "comment_count", "url", "content", "read",
            "page_order"
        }, selection, selectionArguments, null, null, "page_order ASC", limit);

        final List<ArticleItem> articles = new ArrayList<ArticleItem>();

        // ensure the cursor is valid
        if (cursor != null) {
            // move the cursor to the first result and ensure the result exists
            if (cursor.moveToFirst()) {
                // loop through each cursor until the end of the results
                do {
                    // create the article from the cursor and add it to the article items
                    final ArticleItem article = new ArticleItem();
                    article.setId(cursor.getInt(0));
                    article.setCategoryId(cursor.getInt(1));
                    article.setTitle(cursor.getString(2));
                    article.setAuthor(cursor.getString(3));
                    article.setDateAsString(cursor.getString(4));
                    article.setCommentCount(cursor.getInt(5));
                    article.setUrl(cursor.getString(6));
                    article.setContent(cursor.getString(7));
                    article.setRead(cursor.getInt(8) != 0 ? true : false);
                    article.setOrder(cursor.getInt(9));

                    articles.add(article);
                } while (cursor.moveToNext());
            }

            // close the cursor
            cursor.close();
        }

        // close the database
        db.close();

        return articles;
    }

    public ArticleItem getArticle(final String selection, final String[] selectionArguments) {
        // retrieve a single item from the articles database
        final List<ArticleItem> articles = getArticles(selection, selectionArguments, "1");

        // ensure at least one result was retrieved
        if (articles.size() > 0) {
            return articles.get(0);
        }

        return null;
    }

    public ArticleItem getArticleById(final long id) {
        // retrieve a single article matching the specified ID
        return getArticle("id=?", new String[] { String.valueOf(id) });
    }

    public ArticleItem getArticleByUrl(final String url) {
        // retrieve a single article matching the specified URL
        return getArticle("url=?", new String[] { url });
    }

    public List<ArticleItem> getArticlesByCategory(final CategoryItem category, final int page) {
        // retrieve the number of articles per page for the specified category and calculate the
        // limit to use when retrieving results
        final int perPage = category.getPerPage();
        final String limit = (perPage * (page - 1)) + "," + perPage;

        // retrieve the articles matching the category specified
        return getArticles("category=?", new String[] { String.valueOf(category.getId()) }, limit);
    }

    public ArticleItem setArticleAsRead(final ArticleItem article) {
        // set the article as read
        article.setRead(true);

        // set the read  content value
        final ContentValues values = new ContentValues();
        values.put("read", true);

        // save the article
        return saveArticle(article, values);
    }

    private void setArticleDetails(final ArticleItem article, final ContentValues values) {
        // save the article details retrieved when parsing the article list
        values.put("category", article.getCategoryId());
        values.put("title", article.getTitle());
        values.put("date", article.getDateAsString());
        values.put("comment_count", article.getCommentCount());
        values.put("url", article.getUrl());
        values.put("page_order", article.getOrder());
    }

    public ArticleItem saveArticleDetails(final ArticleItem article) {
        // set the content values for the article details
        final ContentValues values = new ContentValues();
        setArticleDetails(article, values);

        // save the article
        return saveArticle(article, values);
    }

    public ArticleItem saveArticle(final ArticleItem article) {
        return saveArticle(article, null);
    }

    public ArticleItem saveArticle(ArticleItem article, ContentValues values) {
        // determine whether no content values were specified
        if (values == null) {
            values = new ContentValues();

            // set the content values for the article details
            setArticleDetails(article, values);

            // set the content values for the details retrieved when the article content is
            // retrieved
            values.put("author", article.getAuthor());
            values.put("content", article.getContent());
            values.put("read", article.isRead() ? 1 : 0);
        }

        // determine whether an ID has been set
        if (article.getId() == 0) {
            // attempt to retrieve an existing article using the article URL
            final ArticleItem existingArticle = getArticleByUrl(article.getUrl());

            // if the article exists, replace the specified one with it so to have an ID set and
            // therefore update the existing record rather than creating a new and duplicate record
            if (existingArticle != null) {
                article = existingArticle;
            }
        }

        final SQLiteDatabase db = getWritableDatabase();

        // determine whether the article has an ID, indicating a record already exists for the
        // article
        if (article.getId() > 0) {
            // update the existing article record
            db.update(TABLE_ARTICLES, values, "id=?",
                new String[] { String.valueOf(article.getId()) });
        } else {
            // insert the article details into the database
            final long id = db.insert(TABLE_ARTICLES, null, values);

            // if the ID returned is valid, update the article item
            if (id != -1) {
                article.setId(id);
            }
        }

        // close the database
        db.close();

        return article;
    }

    public void setAllArticlesAsUnread() {
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_ARTICLES + " SET read=0 WHERE read=1");
        db.close();
    }

    public void removeAllArticles() {
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ARTICLES);
        db.close();
    }
}
