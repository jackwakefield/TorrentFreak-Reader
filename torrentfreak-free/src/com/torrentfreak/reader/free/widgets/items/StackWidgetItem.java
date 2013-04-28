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

package com.torrentfreak.reader.free.widgets.items;

public class StackWidgetItem {
    /**
     * The article title.
     */
    private final String title;

    /**
     * The article date.
     */
    private final String date;

    /**
     * The article URL.
     */
    private final String url;

    public StackWidgetItem(final String title, final String date, final String url) {
        this.title = title;
        this.date = date;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
