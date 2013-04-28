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

package com.torrentfreak.reader.free.articles.tasks;

import java.io.IOException;
import java.lang.Exception;
import java.lang.ref.WeakReference;
import java.util.Map;
import android.os.AsyncTask;
import com.torrentfreak.reader.free.articles.providers.ArticleContentProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

public class ArticleContentHttpTask extends AsyncTask<String, Void, Map<String, String>> {
    /**
     * The article content provider.
     */
    private final ArticleContentProvider provider;

    /**
     * The weak reference to the article received listener.
     */
    private final WeakReference<ArticleContentProvider.OnArticleReceivedListener> listenerReference;

    /**
     * The exception which occurred.
     */
    private Exception error;

    public ArticleContentHttpTask(final ArticleContentProvider provider,
        final WeakReference<ArticleContentProvider.OnArticleReceivedListener> listenerReference) {
        this.provider = provider;
        this.listenerReference = listenerReference;
    }

    @Override
    public Map<String, String> doInBackground(final String... params) {
        Document document;

        try {
            // retrieve the document
            document = Jsoup.connect(params[0]).timeout(20000).ignoreHttpErrors(true).
                followRedirects(true).get();
        } catch (IOException e) {
            error = e;
            return null;
        }

        // setup the whitelist of elements and attributes to allow
        final Whitelist whitelist = Whitelist.relaxed();
        whitelist.addTags("abbr", "address", "area", "article", "aside", "embed", "footer",
            "header", "hr", "iframe", "label", "legend", "nav", "object", "param", "s", "section",
            "summary", "time", "video", "track", "wbr", "center");
        whitelist.addAttributes("a", "rel");
        whitelist.addAttributes("ul", "id");
        whitelist.addAttributes("li", "class");
        whitelist.addAttributes("img", "class");
        whitelist.addAttributes("img", "align");
        whitelist.addAttributes("span", "class");
        whitelist.addAttributes("table", "class");
        whitelist.addAttributes("p", "class");
        whitelist.addAttributes("iframe", "src", "scrolling", "width", "height", "frameborder");

        // clear the retrieved document with the whitelist
        final Cleaner cleaner = new Cleaner(whitelist);
        document = cleaner.clean(document);

        Map<String, String> values = null;

        try {
            // scrape the required values from the document using the article provider
            values = provider.scrape(document);
        } catch (Exception e) {
            error = e;
            return null;
        }

        return values;
    }

    @Override
    public void onPostExecute(final Map<String, String> values) {
        // retrieve the listener from the weak reference
        final ArticleContentProvider.OnArticleReceivedListener listener = listenerReference.get();

        // ensure the task hasn't been cancelled and that the listener still exists
        if (!isCancelled() && listener != null) {
            // determine whether any values were set
            if (values != null) {
                // inform the listener the article was received
                listener.onArticleReceived(values);
            }

            // determine whether an exception was set
            if (error != null) {
                // inform the listener an error occurred
                listener.onArticleReceivedError(error);
            }
        }
    }
}
