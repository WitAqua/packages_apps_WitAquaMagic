/*
 * Copyright (C) 2024 WitAqua
 * SPDX-License-Identifier: Apache-2.0
 */

package org.witaqua.settings;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

@SearchIndexable
public class WitAquaSettings extends DashboardFragment {

    private static final String TAG = "WitAquaMagic";

    public static final String CATEGORY_WITAQUA_SETTINGS = "com.android.settings.category.ia.witaqua_settings";

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.witaqua_settings;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.WITAQUA_SETTINGS;
    }

    @Override
    public String getCategoryKey() {
        return CATEGORY_WITAQUA_SETTINGS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.witaqua_settings);
}
