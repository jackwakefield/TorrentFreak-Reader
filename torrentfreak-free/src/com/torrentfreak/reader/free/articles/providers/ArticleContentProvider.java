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

package com.torrentfreak.reader.free.articles.providers;

import java.lang.Exception;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import com.torrentfreak.reader.free.articles.providers.exceptions.ArticleScrapeException;
import com.torrentfreak.reader.free.articles.tasks.ArticleContentHttpTask;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ArticleContentProvider {
    /**
     * The value key for the article title.
     */
    public static final String KEY_TITLE = "title";

    /**
     * The value key for the article author.
     */
    public static final String KEY_AUTHOR = "author";

    /**
     * The value key for the article date.
     */
    public static final String KEY_DATE = "date";

    /**
     * The value key for the article content.
     */
    public static final String KEY_CONTENT = "content";

    /**
     * The weak reference to the article received listener.
     */
    private final WeakReference<OnArticleReceivedListener> listenerReference;

    /**
     * The article URL.
     */
    private final String url;

    public ArticleContentProvider(final OnArticleReceivedListener listener, final String url) {
        listenerReference = new WeakReference<OnArticleReceivedListener>(listener);
        this.url = url;
    }

    public void fetch() {
        // create and execute the fetch task
        final ArticleContentHttpTask task = new ArticleContentHttpTask(this, listenerReference);
        task.execute(url);
    }

    public Map<String, String> scrape(final Document document) throws ArticleScrapeException {
        // ensure the document exists
        if (document == null) {
            throw new ArticleScrapeException("unable to parse document");
        }

        final Map<String, String> values = new HashMap<String, String>();

        // retrieve the title element
        final Element titleElement = document.select("article header h4 a").first();

        // ensure the title element exists
        if (titleElement == null) {
            throw new ArticleScrapeException("title not found");
        }

        values.put(KEY_TITLE, titleElement.text());

        // retrieve the author element
        final Element authorElement = document.select("a[rel=author]").first();

        // ensure the author element exists
        if (authorElement == null) {
            throw new ArticleScrapeException("author not found");
        }

        values.put(KEY_AUTHOR, authorElement.text());

        // retrieve the date element
        final Element dateElement = document.select("#post-info .date_dark").first();

        // ensure the date element exists
        if (dateElement == null) {
            throw new ArticleScrapeException("date not found");
        }

        values.put(KEY_DATE, dateElement.text());

        // retrieve the content element
        final Element contentElement = document.getElementsByTag("article").first();

        // ensure the content element exists
        if (contentElement == null) {
            throw new ArticleScrapeException("content not found");
        }

        // remove the unwanted elements from the content element
        contentElement.select("#post-info").remove();
        contentElement.select("header").remove();
        contentElement.select(".wp-flattr-button").remove();

        values.put(KEY_CONTENT, contentElement.html());

        return values;
    }

    public interface OnArticleReceivedListener {
        void onArticleReceived(final Map<String, String> values);
        void onArticleReceivedError(final Exception ex);
    }
}
