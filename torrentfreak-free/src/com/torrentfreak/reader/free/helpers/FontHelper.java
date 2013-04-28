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

package com.torrentfreak.reader.free.helpers;

import android.content.Context;
import android.graphics.Typeface;

public class FontHelper {
    /**
     * The path of the Roboto Light typeface relative to the assets directory.
     */
    private static final String ROBOTO_LIGHT_FILE_PATH = "fonts/Roboto-Light.ttf";

    /**
     * The Roboto Light typeface.
     */
    private static Typeface robotoLight;

    public static Typeface getRobotoLight(Context context) {
        // determine whether the font has previously been loaded
        if (robotoLight == null) {
            // load the font from the asset path
            robotoLight = Typeface.createFromAsset(context.getAssets(), ROBOTO_LIGHT_FILE_PATH);
        }

        return robotoLight;
    }
}
