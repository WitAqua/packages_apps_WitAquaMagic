/*
 * Copyright (C) 2019-2024 The Evolution X Project
 *               2026 WitAqua
 * SPDX-License-Identifier: Apache-2.0
 */

package org.witaqua.settings.fragments.spoofing;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.witaqua.KeyProviderManager;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.development.SystemPropPoker;
import com.android.settingslib.search.SearchIndexable;

import org.witaqua.settings.preferences.KeyboxDataPreference;
import org.witaqua.settings.preferences.SystemPropertySwitchPreference;

import java.util.List;

@SearchIndexable
public class Spoofing extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "Spoofing";

    private static final String KEY_GAMES_SPOOF = "use_games_spoof";
    private static final String SYS_GAMES_SPOOF = "persist.sys.pixelprops.games";
    private static final String KEY_PHOTOS_SPOOF = "use_photos_spoof";
    private static final String SYS_PHOTOS_SPOOF = "persist.sys.pixelprops.gphotos";
    private static final String KEY_NETFLIX_SPOOF = "use_netflix_spoof";
    private static final String SYS_NETFLIX_SPOOF = "persist.sys.spoof_netflix";
    private static final String SYS_PI_SPOOF = "persist.sys.pixelprops.pi";
    private static final String SYS_GMS_CERT_SPOOF = "persist.sys.pixelprops.gmscertchain";
    private static final String KEYBOX_DATA_KEY = "keybox_data_setting";

    private ActivityResultLauncher<Intent> mKeyboxFilePickerLauncher;
    private SystemPropertySwitchPreference mDisableForceIntegrity;
    private KeyboxDataPreference mKeyboxDataPreference;
    private SwitchPreference mGamesSpoof;
    private SwitchPreference mPhotosSpoof;
    private SwitchPreference mNetFlixSpoof;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.witaqua_settings_spoofing);

        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources resources = context.getResources();

        mGamesSpoof = (SwitchPreference) prefScreen.findPreference(KEY_GAMES_SPOOF);
        mGamesSpoof.setChecked(SystemProperties.getBoolean(SYS_GAMES_SPOOF, false));
        mGamesSpoof.setOnPreferenceChangeListener(this);

        mPhotosSpoof = (SwitchPreference) prefScreen.findPreference(KEY_PHOTOS_SPOOF);
        mPhotosSpoof.setChecked(SystemProperties.getBoolean(SYS_PHOTOS_SPOOF, true));
        mPhotosSpoof.setOnPreferenceChangeListener(this);

        mNetFlixSpoof = (SwitchPreference) findPreference(KEY_NETFLIX_SPOOF);
        mNetFlixSpoof.setChecked(SystemProperties.getBoolean(SYS_NETFLIX_SPOOF, false));
        mNetFlixSpoof.setOnPreferenceChangeListener(this);

        mDisableForceIntegrity = findPreference(SYS_GMS_CERT_SPOOF);
        if (mDisableForceIntegrity != null) {
            mDisableForceIntegrity.setEnabled(KeyProviderManager.isKeyboxAvailable());
        }

        mKeyboxFilePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    Preference pref = findPreference(KEYBOX_DATA_KEY);
                    if (pref instanceof KeyboxDataPreference) {
                        ((KeyboxDataPreference) pref).handleFileSelected(uri);
                    }
                    if (mDisableForceIntegrity != null) {
                        mDisableForceIntegrity.setEnabled(KeyProviderManager.isKeyboxAvailable());
                    }
                }
            }
        );
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();
        if (preference == mGamesSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_GAMES_SPOOF, value ? "true" : "false");
            SystemPropPoker.getInstance().poke();
            return true;
        } else if (preference == mPhotosSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_PHOTOS_SPOOF, value ? "true" : "false");
            SystemPropPoker.getInstance().poke();
            return true;
        } else if (preference == mNetFlixSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_NETFLIX_SPOOF, value ? "true" : "false");
            SystemPropPoker.getInstance().poke();
            return true;
        }
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mKeyboxDataPreference = findPreference(KEYBOX_DATA_KEY);
        if (mKeyboxDataPreference != null) {
            mKeyboxDataPreference.setFilePickerLauncher(mKeyboxFilePickerLauncher);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.WITAQUA_SETTINGS;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider(R.xml.witaqua_settings_spoofing) {

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                List<String> keys = super.getNonIndexableKeys(context);
                final Resources resources = context.getResources();
                return keys;
            }
        };
}
