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

import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.util.Log;
import com.google.inject.Inject;
import com.torrentfreak.reader.free.ArticleActivity;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.articles.ArticleStorage;
import com.torrentfreak.reader.free.articles.providers.ArticleListProvider;
import com.torrentfreak.reader.free.categories.CategoryItem;
import com.torrentfreak.reader.free.categories.CategoryManager;
import com.torrentfreak.reader.free.R;
import com.torrentfreak.reader.free.widgets.items.StackWidgetItem;
import com.torrentfreak.reader.free.widgets.StackWidgetActivity;
import com.torrentfreak.reader.free.widgets.StackWidgetProvider;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new StackRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    /**
     * The application context.
     */
    private final Context context;

    /**
     * The category manager.
     */
    private final CategoryManager categoryManager;

    /**
     * The article storage database.
     */
    private final ArticleStorage articleStorage;

    /**
     * The category item chosen for the widget.
     */
    private final CategoryItem category;

    /**
     * The current article list provider.
     */
    private ArticleListProvider articleProvider;

    /**
     * The article update timer.
     */
    private final Timer timer;

    /**
     * The current widget items being displayed.
     */
    private final List<StackWidgetItem> widgetItems;

    /**
     * The ID of the widget.
     */
    private final int widgetId;

    public StackRemoteViewsFactory(final Context context, final Intent intent) {
        this.context = context;
        categoryManager = new CategoryManager(context);
        articleStorage = new ArticleStorage(context);
        timer = new Timer();
        widgetItems = new ArrayList<StackWidgetItem>();

        // retrieve the ID of the widget
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID);

        // retrieve the preferences associated with the widget ID set by the configuration activity
        final SharedPreferences sharedPreference = context.getSharedPreferences(
            StackWidgetProvider.getSharedPreferencesNameForWidget(widgetId), 0);
        final int categoryId = sharedPreference.getInt(StackWidgetActivity.SETTING_CATEGORY, 1);

        // set the category from the selected category and create the article list provider
        category = categoryManager.getCategoryById(categoryId);
        articleProvider = category.createProvider();
    }

    public void onCreate() {
        // retrieve the preferences associated with the widget ID set by the configuration activity
        final SharedPreferences sharedPreference = context.getSharedPreferences(
            StackWidgetProvider.getSharedPreferencesNameForWidget(widgetId), 0);
        final long updateInterval =
            sharedPreference.getLong(StackWidgetActivity.SETTING_UPDATE_INTERVAL, 1800000);

        // set the timer to retrieve the articles for the chosen categor immediately and then
        // every x milliseconds chosen by the configuration activity
        timer.scheduleAtFixedRate(new RetrieveArticlesTask(), 0, updateInterval);
    }

    public void onDestroy() {
        // clear the widget items and cancel the article retrieval timer
        widgetItems.clear();
        timer.cancel();
    }

    public int getCount() {
        return widgetItems.size();
    }

    public RemoteViews getViewAt(final int position) {
        final StackWidgetItem item = widgetItems.get(position);

        // create the remove view for the specified widget item, setting the title and date
        final RemoteViews remoteView =
            new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteView.setTextViewText(R.id.title, item.getTitle());
        remoteView.setTextViewText(R.id.date, item.getDate());

        // create a fill-in intent to inform the widget provider to view the article with the
        // specified URL
        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(StackWidgetProvider.EXTRA_URL, item.getUrl());
        remoteView.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return remoteView;
    }

    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.widget_loading);
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(final int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {

    }

    private void addArticles(final List<ArticleItem> articles) {
        // clear the current widget items
        widgetItems.clear();

        // add each article to the widget items
        for (final ArticleItem article : articles) {
            widgetItems.add(new StackWidgetItem(article.getTitle(), article.getDateAsString(),
                article.getUrl()));
        }

        // notify the widget manager the data has changed to refresh the widget items
        notifyDataChanged();
    }

    private void notifyDataChanged() {
        // notify the app widget manager the data has changed for the widget ID
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.stack_view);
    }

    class RetrieveArticlesTask extends TimerTask {
        public void run() {
            try {
                // attempt to retrieve the articles for the selected category
                final List<ArticleItem> articles = articleProvider.fetch();
                addArticles(articles);
            } catch (Exception ex) {
                // if widget items have previously been added, keep the current set, else retrieve
                // the first page of articles for the widgets category from the article storage
                // database
                if (widgetItems.size() == 0) {
                    // retrieve the articles for the category from the article storage database
                    final List<ArticleItem> articles =
                        articleStorage.getArticlesByCategory(category, 1);

                    if (articles.size() > 0) {
                        // add the cached articles to the widget
                        addArticles(articles);
                    }
                }
            }
        }
    }
}
