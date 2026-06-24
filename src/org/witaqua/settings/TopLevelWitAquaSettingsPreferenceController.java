/*
 * Copyright (C) 2024 WitAqua
 * SPDX-License-Identifier: Apache-2.0
 */

package org.witaqua.settings;

import android.content.Context;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class TopLevelWitAquaSettingsPreferenceController extends BasePreferenceController {

    public TopLevelWitAquaSettingsPreferenceController(Context context,
            String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}
