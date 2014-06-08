/*
 * Copyright 2014 Yuriy Yunikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yyunikov.dimblock.base;

import android.content.Context;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * @author yyunikov
 */
public class Admob {

    private static InterstitialAd sInterstitialAd;

    public static void initAd(final Context context) {
        // Create the interstitial.
        sInterstitialAd = new InterstitialAd(context);
        sInterstitialAd.setAdUnitId(Model.getInstance().getConfiguration().getAdmobId());

        // Create ad request.
        final AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        sInterstitialAd.loadAd(adRequest);

        sInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                displayInterstitial();
            }
        });
    }

    public static void displayInterstitial() {
        if (sInterstitialAd.isLoaded()) {
            sInterstitialAd.show();
        }
    }
}
