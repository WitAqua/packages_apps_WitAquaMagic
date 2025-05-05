/*
 * Copyright (C) 2022 crDroid Android Project
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

package tokyo.witaqua.settings.fragments.themes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.net.Uri;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceViewHolder;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import com.bumptech.glide.Glide;

import com.android.internal.util.witaqua.ThemeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONException;

public class EmojiPicker extends SettingsPreferenceFragment {

    private final String LOG_TAG = "EmojiPicker";

    private final String CUSTOM_EMOJI_PROP = "persist.sys.custom_emoji_path";

    private RecyclerView mRecyclerView;

    private String[] presetFiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presetFiles = getResources().getStringArray(R.array.emoji_preset_filename);

        getActivity().setTitle(R.string.themes_emoji_title);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.emoji_item_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        Adapter mAdapter = new Adapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        Button btnReset = view.findViewById(R.id.btn_reset_default);

        btnReset.setOnClickListener(v -> {
            SystemProperties.set(CUSTOM_EMOJI_PROP, "");
            applyFinished(getContext());
        });

        return view;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.WITAQUA_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {
        Context context;
        String mSelectedPkg;
        String mAppliedPkg;

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fonts_option, parent, false);
            CustomViewHolder vh = new CustomViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {
            // Create 10-digit emoji sample.
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                int code = 0x1F600 + new Random().nextInt(0x1F64F - 0x1F600 + 1);
                sb.append(Character.toChars(code));
            }

            holder.title.setText(sb.toString());
            holder.title.setTextSize(24);

            final String filename = presetFiles[position];
            final String srcPath  = "/product/fonts/emoji/" + filename;

            Typeface tf = Typeface.createFromFile(srcPath);
            holder.title.setTypeface(tf);

            holder.itemView.setActivated(SystemProperties.get(CUSTOM_EMOJI_PROP, "").equals(srcPath));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setActivated(true);
                    applyEmoji(srcPath);
                }
            });
        }

        @Override
        public int getItemCount() {
            return presetFiles.length;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView title;
            public CustomViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.option_title);
                name = (TextView) itemView.findViewById(R.id.option_label);
            }
        }
    }

    public void applyEmoji(String srcPath) {
        SystemProperties.set(CUSTOM_EMOJI_PROP, srcPath);
        applyFinished(getContext());
    }

    public void applyFinished(Context context) {
        Toast.makeText(
            context,
            context.getString(R.string.themes_emoji_select_message),
            Toast.LENGTH_LONG
        ).show();
        finish();
    }
}
