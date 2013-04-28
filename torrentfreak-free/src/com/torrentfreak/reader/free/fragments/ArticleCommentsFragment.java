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

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.torrentfreak.reader.free.R;
import roboguice.inject.InjectView;

public class ArticleCommentsFragment extends RoboSherlockFragment {
    /**
     * The extra key used to pass the article URL to the fragment.
     */
    public static final String EXTRA_URL =
        "com.torrentfreak.reader.free.fragments.ArticleCommentsFragment.URL";

    /**
     * The saved state key used to retain the article URL.
     */
    private static final String SAVED_STATE_URL = "url";

    /**
     * The URL of the remote comments proxy.
     */
    private static final String COMMENTS_PROXY_URL =
        "http://proxy.torrentfreakreader.com/comments.html";

    /**
     * The name to assign the article JavaScript interface to.
     */
    private static final String ARTICLE_INTERFACE_NAME = "article";

    /**
     * The progress bar.
     */
    @InjectView(R.id.progress)
    protected ProgressBar progressBar;

    /**
     * The web view used to display the article comments.
     */
    @InjectView(R.id.web_view)
    protected WebView webView;

    /**
     * The article URL.
     */
    private String url;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
        // inflate the article fragment
        return inflater.inflate(R.layout.fragment_article_comments, null);
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
            // retrieve the retained article URL
            url = savedInstanceState.getString(SAVED_STATE_URL);
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

        webView.setWebChromeClient(new WebChromeClient());
        webView.setBackgroundColor(getResources().getColor(R.color.article_comments_background));
        webView.setWebViewClient(webViewClient);

        // load the comments proxy and setup the JavaScript interface used to retrieve article
        // details
        webView.loadUrl(COMMENTS_PROXY_URL);
        webView.addJavascriptInterface(new ArticleJavaScriptInterface(), ARTICLE_INTERFACE_NAME);
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // save the article URL to be retrieved when the fragment is recreated
        savedInstanceState.putString(SAVED_STATE_URL, url);
    }

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(final WebView view, final String url) {
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            // create an intent to view the the specified URL in another app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

            return true;
        }
    };

    class ArticleJavaScriptInterface {
        @JavascriptInterface
        public String getUrl() {
            return url;
        }
    }
}
