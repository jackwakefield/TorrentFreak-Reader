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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.torrentfreak.reader.free.R;
import com.torrentfreak.reader.free.ArticleActivity;

public class StackWidgetProvider extends AppWidgetProvider {
    /**
     * The extra key used to pass the articles URL to the activity.
     */
    public static final String ACTION_VIEW =
        "com.torrentfreak.reader.free.widgets.StackWidgetProvider.VIEW_ACTION";

    /**
     * The extra key used to inform the activity whether or not it has a parent.
     */
    public static final String EXTRA_URL =
        "com.torrentfreak.reader.free.widgets.StackWidgetProvider.URL";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // determine whether the received intent is the view action
        if (intent.getAction().equals(ACTION_VIEW)) {
            // retrieve the article URL
            final String url = intent.getStringExtra(EXTRA_URL);

            if (url != null) {
                // create an intent to view the selected article, passing the articles URL
                final Intent viewIntent = new Intent();
                viewIntent.setClass(context, ArticleActivity.class);
                viewIntent.putExtra(ArticleActivity.EXTRA_URL, url);
                viewIntent.putExtra(ArticleActivity.EXTRA_SINGLE_ACTIVITY, true);
                viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // start the view article activity
                context.startActivity(viewIntent);
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
        final int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            // create the intent to create the widget service passing the widget ID
            final Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // create the remove view for the widget service
            final RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
            remoteView.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
            remoteView.setEmptyView(R.id.stack_view, R.id.empty_view);

            // create the view intent for fill-in intents to derive from
            final Intent viewIntent = new Intent(context, StackWidgetProvider.class);
            viewIntent.setAction(StackWidgetProvider.ACTION_VIEW);
            viewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, viewIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setPendingIntentTemplate(R.id.stack_view, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteView);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static String getSharedPreferencesNameForWidget(final int widgetId) {
        return "widget_preferences_" + widgetId;
    }
}
