package com.github.kotvertolet.youtubeaudioplayer.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.APP_PREFERENCES;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        sharedPreferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Settings");
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        Preference preference = findPreference(Constants.PREFERENCE_AUDIO_QUALITY);
        preference.setIconSpaceReserved(false);
        preference.setOnPreferenceChangeListener(this);
        preference = findPreference(Constants.PREFERENCE_NO_RECOMMENDATIONS);
        preference.setIconSpaceReserved(false);
        preference.setOnPreferenceChangeListener(this);
        preference = findPreference(Constants.PREFERENCE_CACHE_SIZE);
        preference.setIconSpaceReserved(false);
        preference.setOnPreferenceChangeListener(this);
        preference = findPreference(Constants.PREFERENCE_RESTRICT_MOBILE_NETWORK_CACHING);
        preference.setIconSpaceReserved(false);
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String newValueSt = String.valueOf(newValue);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String prefKey = preference.getKey();
        if (getString(R.string.key_pref_cache_size).equals(prefKey)) {
            editor.putInt(Constants.PREFERENCE_CACHE_SIZE, Integer.valueOf(newValueSt));
        } else if (getString(R.string.key_pref_audio_quality).equals(prefKey)) {
            editor.putInt(Constants.PREFERENCE_AUDIO_QUALITY, Integer.valueOf(newValueSt));
        } else if (getString(R.string.key_pref_no_recommendations).equals(prefKey)) {
            editor.putBoolean(Constants.PREFERENCE_NO_RECOMMENDATIONS, Boolean.valueOf(newValueSt));
        } else if (getString(R.string.key_pref_save_in_cache_only_wifi).equals(prefKey)) {
            editor.putBoolean(Constants.PREFERENCE_NO_RECOMMENDATIONS, Boolean.valueOf(newValueSt));
        }
        else {
            Log.e(getClass().getSimpleName(), String.format("Unknown preference with key '%s' detected", prefKey));
        }
        editor.apply();
        return true;
    }
}
