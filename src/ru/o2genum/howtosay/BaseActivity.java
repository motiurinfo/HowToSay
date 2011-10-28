/*
    Copyright (c) 2011, Andrey Moiseev

    Licensed under the Apache License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may obtain
    a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package ru.o2genum.howtosay;

/**
 * Base class for app's activities
 * 
 * @author Andrey Moiseev
 */

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Bundle;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import ru.o2genum.forvo.ApiKey;

public abstract class BaseActivity extends Activity {

    protected ActionBar actionBar;
    protected LayoutInflater inflater;
    protected SearchManager searchManager;
    protected MediaPlayer mediaPlayer;
    SharedPreferences prefs;

    final String PREFS_API_KEY = "API_KEY";
    final String SHARED_PREFS_KEY = "ru.o2genum.howtosay.PREFS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        searchManager = (SearchManager) 
            getSystemService(Context.SEARCH_SERVICE);
        inflater = (LayoutInflater) getSystemService
                  (Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.base);
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        Intent homeIntent = new Intent(this, DashboardActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        actionBar.setHomeAction(new IntentAction(this, homeIntent,
                    R.drawable.ic_actionbar_home));
        actionBar.addAction(new AbstractAction(R.drawable.ic_actionbar_search) {
            @Override
            public void performAction(View view) {
                doSearch(null);
            }
        });
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        // Set API key
        ApiKey.setKey(getApiKey());
    }

    public void setView(View view) {
        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        frame.addView(view);
    }

    @Override
    public void setTitle(CharSequence title) {
        actionBar.setTitle(title);
    }

    public void doSearch(String query) {
        doWordSearch(query);
    }

    public void doWordSearch(String query) {
        searchManager.startSearch(query, false, new ComponentName(this,
                    WordSearchActivity.class), null, false);
    }

    public void doPronunciationSearch(String query) {
        searchManager.startSearch(query, false, new ComponentName(this,
                    PronunciationSearchActivity.class), null, false);
    }

    public void playSound(String url) {
        final String finalUrl = url;
        new Thread(new Runnable() {
            public void run() {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(finalUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch(Exception ex) {
                    toastException(ex);
                }
            }
        }).start();
    }

    public void toastException(Exception ex) {
        String msg = getString(R.string.error_exception);
        String msg2 = ex.getClass().getSimpleName() + 
                ((ex.getMessage() != null) ? ": " + ex.getMessage() : "");
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Toast.makeText(this, msg2, Toast.LENGTH_LONG).show();
    }

    public String getLocalizedLanguageName(String code, String inEnglish) {
        int id = getStrResId(code);
        try {
            return id > 0 ? getString(id) : inEnglish;
        } catch (Resources.NotFoundException ex) {
            // Don't know why, but this exception occurs on
            // Ice Cream Sandwich emulator.
        } finally {
            return inEnglish;
        }
    }

    public String getLocalizedCountryName(String inEnglish) {
        int id = getStrResId(inEnglish);
        try {
            return id > 0 ? getString(id) : inEnglish;
        } catch (Resources.NotFoundException ex) {
            // See getLocalizedLanguageName
        } finally {
            return inEnglish;
        }
    }

    public int getStrResId(String inEnglish) {
        String result = 
                inEnglish.toLowerCase()
                .replace(' ', '_')
                .replace(")", "")
                .replace("(", "")
                .replace('-', '_')
                .replace(",", "");
        if(result.equals("new")) // "new" is a language code and Java keyword
            result += "_";
        return getResources().getIdentifier(result, "string",
                getClass().getPackage().getName());
    }

    public String getApiKey() {
        String fromPrefs = prefs.getString(PREFS_API_KEY, "");
        fromPrefs = fromPrefs.trim();
        if(fromPrefs.length() == 0) {
            return Secret.apiKey;
        } else {
            return fromPrefs;
        }
    }

    public boolean isApiKeySet() {
        String fromPrefs = prefs.getString(PREFS_API_KEY, "");
        fromPrefs = fromPrefs.trim();
        return fromPrefs.length() != 0;
    }
}
