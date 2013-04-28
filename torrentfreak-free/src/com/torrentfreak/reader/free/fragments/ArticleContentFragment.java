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

package com.torrentfreak.reader.free.fragments;

import java.lang.Exception;
import java.util.Map;
import android.graphics.drawable.ColorDrawable;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.torrentfreak.reader.free.articles.ArticleItem;
import com.torrentfreak.reader.free.articles.ArticleStorage;
import com.torrentfreak.reader.free.articles.providers.ArticleContentProvider;
import com.torrentfreak.reader.free.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import roboguice.inject.InjectView;

public class ArticleContentFragment extends RoboSherlockFragment implements
    ArticleContentProvider.OnArticleReceivedListener {
    /**
     * The extra key used to pass the article URL to the fragment.
     */
    public static final String EXTRA_URL =
        "com.torrentfreak.reader.free.fragments.ArticleContentFragment.URL";

    /**
     * The saved state key used to retain the article URL.
     */
    private static final String SAVED_STATE_URL = "url";

    /**
     * The saved state key used to retain the article title.
     */
    private static final String SAVED_STATE_TITLE = "title";

    /**
     * The saved state key used to retain the article author.
     */
    private static final String SAVED_STATE_AUTHOR = "author";

    /**
     * The saved state key used to retain the article date.
     */
    private static final String SAVED_STATE_DATE = "date";

    /**
     * The saved state key used to retain the article content.
     */
    private static final String SAVED_STATE_CONTENT = "content";

    /**
     * The name to assign the article JavaScript interface to.
     */
    private static final String ARTICLE_INTERFACE_NAME = "article";

    /**
     * The article asset file path.
     */
    private static final String FILE_PATH = "file:///android_asset/article.html";

    /**
     * The article storage database.
     */
    @Inject
    private ArticleStorage articleStorage;

    /**
     * The progress bar.
     */
    @InjectView(R.id.progress)
    protected ProgressBar progressBar;

    /**
     * The web view used to display the article content.
     */
    @InjectView(R.id.web_view)
    protected WebView webView;

    /**
     * The article URL.
     */
    private String url;

    /**
     * The article title.
     */
    private String title;

    /**
     * The article author.
     */
    private String author;

    /**
     * The article date.
     */
    private String date;

    /**
     * The article content.
     */
    private String content;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
        // inflate the article fragment
        return inflater.inflate(R.layout.fragment_article_content, null);
    }

    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set a colour filter to change the progress bar colour to pink
        progressBar.getIndeterminateDrawable().setColorFilter(
            getResources().getColor(R.color.progress_bar_filter), Mode.MULTIPLY);

        // determine whether a saved instance state exists, indicating the fragment has existed
        // previously
        if (savedInstanceState == null) {
            // retrieve the fragment arguments and set the url
            final Bundle bundle = getArguments();
            url = bundle.getString(EXTRA_URL);
        } else {
            // retrieve the retained article details
            url = savedInstanceState.getString(SAVED_STATE_URL);
            title = savedInstanceState.getString(SAVED_STATE_TITLE);
            author = savedInstanceState.getString(SAVED_STATE_AUTHOR);
            date = savedInstanceState.getString(SAVED_STATE_DATE);
            content = savedInstanceState.getString(SAVED_STATE_CONTENT);
        }

        // setup the web view settings
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);

        // setDisplayZoomControls only exists on honeycomb and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
        }

        webSettings.setPluginsEnabled(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        webView.setBackgroundColor(getResources().getColor(R.color.article_content_background));
        webView.setWebChromeClient(new WebChromeClient());

        // determine whether the article details have been set
        if (title == null || author == null || date == null || content == null) {
            // fetch the article contents
            final ArticleContentProvider provider = new ArticleContentProvider(this, url);
            provider.fetch();
        } else {
            // view the article contents as the article has already been retrieved
            viewArticle();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // save the article details to be retrieved when the fragment is recreated
        savedInstanceState.putString(SAVED_STATE_URL, url);
        savedInstanceState.putString(SAVED_STATE_TITLE, title);
        savedInstanceState.putString(SAVED_STATE_AUTHOR, author);
        savedInstanceState.putString(SAVED_STATE_DATE, date);
        savedInstanceState.putString(SAVED_STATE_CONTENT, content);
    }

    private void viewArticle() {
        // remover all previous notifications
        Crouton.cancelAllCroutons();

        // ensure the parent activity exists
        if (getSherlockActivity() != null) {
            // set the title of the action bar to the article title
            final ActionBar actionBar = getSherlockActivity().getSupportActionBar();
            actionBar.setTitle(title);

            // load the web view file and setup the JavaScript interface used to retrieve
            // article details
            webView.loadUrl(FILE_PATH);
            webView.addJavascriptInterface(new ArticleJavaScriptInterface(),
                ARTICLE_INTERFACE_NAME);

            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    }

    public void onArticleReceived(final Map<String, String> values) {
        // retrieve the article details from the given values
        title = values.get(ArticleContentProvider.KEY_TITLE);
        author = values.get(ArticleContentProvider.KEY_AUTHOR);
        date = values.get(ArticleContentProvider.KEY_DATE);
        content = values.get(ArticleContentProvider.KEY_CONTENT);

        // attempt to retrieve the article from the article database
        final ArticleItem article = articleStorage.getArticleByUrl(url);

        if (article != null) {
            // update the article details with those retrieved
            article.setTitle(title);
            article.setAuthor(author);
            article.setDateAsString(date);
            article.setContent(content);

            // save the latest article details to the article database
            articleStorage.saveArticle(article);
        }

        // display the retrieved article
        viewArticle();
    }

    public void onArticleReceivedError(Exception ex) {
        // ensure the parent activity exists
        if (getActivity() != null) {
            // attempt to retrieve the article from the article database
            final ArticleItem article = articleStorage.getArticleByUrl(url);

            // ensure the article exists and that the inner article details have previously been
            // set
            if (article != null && article.getTitle() != null && article.getAuthor() != null &&
                article.getDate() != null && article.getContent() != null) {
                // set the article details from the stored article
                title = article.getTitle();
                author = article.getAuthor();
                date = article.getDateAsString();
                content = article.getContent();

                // display the cached article
                viewArticle();
            } else {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);

                if (ex != null) {
                    final Style style = new Style.Builder().setDuration(Style.DURATION_INFINITE)
                        .setBackgroundColorValue(getResources().getColor(R.color.crouton_error))
                        .setHeight(LayoutParams.WRAP_CONTENT).build();

                    // remove all previous notifications and show the error
                    Crouton.cancelAllCroutons();
                    Crouton.makeText(getActivity(),
                        "Failed to retrieve article - " + ex.getMessage(), style).show();
                }
            }
        }
    }

    class ArticleJavaScriptInterface {
        @JavascriptInterface
        public String getUrl() {
            return url;
        }

        @JavascriptInterface
        public String getTitle() {
            return title;
        }

        @JavascriptInterface
        public String getAuthor() {
            return author;
        }

        @JavascriptInterface
        public String getDate() {
            return date;
        }

        @JavascriptInterface
        public String getContent() {
            return content;
        }
    }
}
