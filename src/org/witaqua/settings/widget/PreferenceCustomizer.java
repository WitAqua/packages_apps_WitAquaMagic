/*
 * Copyright (C) 2026 WitAqua
 * SPDX-License-Identifier: Apache-2.0
 */

package org.witaqua.settings.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;

/**
 * WitAqua-specific look for Settings preference rows.
 *
 * <p>Kept outside of the upstream sources so that upstream only needs a one-line call
 * from the existing bind hooks:
 * <ul>
 *   <li>{@link #onBindHomepagePreference} from {@code HomepagePreferenceLayoutHelper}</li>
 *   <li>{@link #onBindPreference} from {@code HighlightablePreferenceGroupAdapter}</li>
 * </ul>
 */
public final class PreferenceCustomizer {

    /** Key prefix of dynamically injected tiles; they navigate via a click listener. */
    private static final String TILE_KEY_PREFIX = "dashboard_tile_pref_";

    private PreferenceCustomizer() {}

    /**
     * Homepage rows: title only (no summary) with a trailing chevron. Every homepage
     * entry opens another page, so the chevron is always shown.
     */
    public static void onBindHomepagePreference(PreferenceViewHolder holder) {
        final View summary = holder.findViewById(android.R.id.summary);
        if (summary != null) {
            summary.setVisibility(View.GONE);
        }
        if (holder.itemView instanceof ViewGroup root) {
            final View chevron = ensureChevron(root);
            chevron.setEnabled(holder.itemView.isEnabled());
            if (chevron.getLayoutParams() instanceof ViewGroup.MarginLayoutParams lp) {
                final int marginEnd = root.getResources().getDimensionPixelSize(
                        R.dimen.homepage_chevron_margin_end_customized);
                if (lp.getMarginEnd() != marginEnd) {
                    lp.setMarginEnd(marginEnd);
                    chevron.setLayoutParams(lp);
                }
            }
        }
    }

    /**
     * Regular rows: add a trailing chevron to preferences that open another page and
     * have no widget of their own (switch, radio, etc.).
     */
    public static void onBindPreference(PreferenceViewHolder holder,
            @Nullable Preference preference) {
        if (preference == null) {
            return;
        }
        final View frame = holder.findViewById(android.R.id.widget_frame);
        if (!(frame instanceof ViewGroup widgetFrame)) {
            return;
        }
        final View existing = widgetFrame.findViewById(R.id.preference_chevron_customized);
        final int ownChildren = widgetFrame.getChildCount() - (existing != null ? 1 : 0);

        if (ownChildren == 0 && isNavigational(preference)) {
            final View chevron = ensureChevron(widgetFrame);
            chevron.setEnabled(preference.isEnabled());
            widgetFrame.setVisibility(View.VISIBLE);
        } else if (existing != null) {
            // Recycled view holder that previously showed a chevron.
            widgetFrame.removeView(existing);
            if (ownChildren == 0) {
                widgetFrame.setVisibility(View.GONE);
            }
        }
    }

    private static boolean isNavigational(Preference preference) {
        if (!preference.isSelectable() || preference instanceof PreferenceGroup) {
            return false;
        }
        if (preference.getFragment() != null || preference.getIntent() != null) {
            return true;
        }
        final String key = preference.getKey();
        return key != null && key.startsWith(TILE_KEY_PREFIX);
    }

    private static View ensureChevron(ViewGroup parent) {
        View chevron = parent.findViewById(R.id.preference_chevron_customized);
        if (chevron == null) {
            chevron = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.preference_chevron_customized, parent, false);
            parent.addView(chevron);
        }
        return chevron;
    }
}
