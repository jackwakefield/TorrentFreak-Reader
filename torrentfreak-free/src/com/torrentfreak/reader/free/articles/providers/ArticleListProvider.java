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
import java.util.List;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.categories.CategoryItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class ArticleListProvider {
    /**
     * The category to retrieve the article list for.
     */
    protected final CategoryItem category;

    /**
     * The page of articles to scrape from.
     */
    protected int page;

    public ArticleListProvider(final CategoryItem category) {
        this.category = category;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public List<ArticleItem> fetch() throws Exception {
        // retrieve the document
        Document document = Jsoup.connect(getUrl()).timeout(0).ignoreHttpErrors(true).
            followRedirects(true).get();

        // scrape the document
        return scrape(document);
    }

    private String getUrl() {
        return String.format(category.getUrl(), page);
    }

    public abstract List<ArticleItem> scrape(final Document document) throws Exception;
}
