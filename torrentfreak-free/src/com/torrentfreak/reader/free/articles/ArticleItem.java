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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import android.os.Parcel;
import android.os.Parcelable;
import com.torrentfreak.reader.free.categories.CategoryItem;

public class ArticleItem implements Parcelable {
    /**
     * The date format.
     */
    private static final String DATE_FORMAT = "dd/MM/yy";

    /**
     * The date formatter.
     */
    private static final SimpleDateFormat dateFormatter;

    /**
     * Todays date.
     */
    private static final Date todaysDate;

    /**
     * The internal ID.
     */
    private long id;

    /**
     * The category ID.
     */
    private int categoryId;

    /**
     * The article title.
     */
    private String title;

    /**
     * The article author.
     */
    private String author;

    /**
     * The article URL.
     */
    private String url;

    /**
     * The date the article was posted.
     */
    private GregorianCalendar date;

    /**
     * The number of comments the article has.
     */
    private int commentCount;

    /**
     * The article content.
     */
    private String content;

    /**
     * Determines whether the article has been read.
     */
    private boolean read;

    /**
     * The page order the article was in.
     */
    private int order;

    static {
        dateFormatter = new SimpleDateFormat(DATE_FORMAT);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        todaysDate = calendar.getTime();
    }

    public ArticleItem() {

    }

    public ArticleItem(final Parcel in) {
        // read the article details from the parcel
        id = in.readLong();
        categoryId = in.readInt();
        title = in.readString();
        url = in.readString();
        date = new GregorianCalendar(in.readInt(), in.readInt(), in.readInt(), in.readInt(),
            in.readInt(), in.readInt());
        commentCount = in.readInt();
        content = in.readString();
        read = in.readByte() != 0 ? true : false;
        order = in.readInt();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final int id) {
        categoryId = id;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setDate(final GregorianCalendar date) {
        this.date = date;
    }

    public void setDateAsString(final String value) {
        Date parsedDate;

        try {
            // attempt to parse the date from the specified text value
            parsedDate = dateFormatter.parse(value);
        } catch (final ParseException ex) {
            // set the date to today
            parsedDate = new Date();
        }

        final GregorianCalendar date = new GregorianCalendar();
        date.setTime(parsedDate);

        this.date = date;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public String getDateAsString() {
        return dateFormatter.format(date.getTime());
    }

    public String getFormattedDate() {
        // retrieve the difference between today and the articles date in days
        final long dayDifference = (todaysDate.getTime() - date.getTime().getTime()) /
            (1000 * 60 * 60 * 24);

        if (dayDifference == 0) {
            return "Today";
        }

        if (dayDifference == 1) {
            return "Yesterday";
        }

        return getDateAsString();
    }

    public void setCommentCount(final int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setRead(final boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        // write the article details to the parcel
        dest.writeLong(id);
        dest.writeInt(categoryId);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeInt(date.get(Calendar.YEAR));
        dest.writeInt(date.get(Calendar.MONTH));
        dest.writeInt(date.get(Calendar.DAY_OF_MONTH));
        dest.writeInt(date.get(Calendar.HOUR_OF_DAY));
        dest.writeInt(date.get(Calendar.MINUTE));
        dest.writeInt(date.get(Calendar.SECOND));
        dest.writeInt(commentCount);
        dest.writeString(content);
        dest.writeByte(read ? (byte)1 : (byte)0);
        dest.writeInt(order);
    }

    public static final Parcelable.Creator<ArticleItem> CREATOR =
        new Parcelable.Creator<ArticleItem>() {
        public ArticleItem createFromParcel(final Parcel in) {
            return new ArticleItem(in);
        }

        public ArticleItem[] newArray(final int size) {
            return new ArticleItem[size];
        }
    };
}
