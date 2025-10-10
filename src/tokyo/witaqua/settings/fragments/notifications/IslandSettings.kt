/*
 * Copyright (C) 2025 WitAqua
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

package tokyo.witaqua.settings.fragments.notifications

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import com.android.internal.logging.nano.MetricsProto
import com.android.settings.R
import com.android.settings.SettingsPreferenceFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.search.SearchIndexable
import com.android.internal.util.witaqua.VibrationUtils

@SearchIndexable
class IslandSettings : SettingsPreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.island_settings)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key != null) {
            VibrationUtils.triggerVibration(context, 3)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun getMetricsCategory(): Int {
        return MetricsProto.MetricsEvent.WITAQUA_SETTINGS
    }

    companion object {
        const val TAG = "IslandSettings"

        /** For search */
        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER = object : BaseSearchIndexProvider(R.xml.island_settings) {
            override fun getNonIndexableKeys(context: Context): List<String> {
                return super.getNonIndexableKeys(context)
            }
        }
    }
}
