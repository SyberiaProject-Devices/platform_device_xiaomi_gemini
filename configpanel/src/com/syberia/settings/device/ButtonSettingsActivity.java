/*
 * Copyright (C) 2016 The CyanogenMod Project
 *           (C) 2017 The LineageOS Project
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

package com.syberia.settings.device;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.MenuItem;

import android.provider.Settings;

import java.io.File;

import com.syberia.settings.device.R;
import com.syberia.settings.device.preference.VibratorStrengthPreference;
import com.syberia.settings.device.utils.FileUtils;
import com.syberia.settings.device.utils.Utils;

public class ButtonSettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener{

	private Preference mKcalPref;
    private VibratorStrengthPreference mVibratorStrength;
    private SwitchPreference fixVolte;

    private SharedPreferences mPrefs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.button_panel);

        mKcalPref = findPreference("kcal");
                mKcalPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                     @Override
                     public boolean onPreferenceClick(Preference preference) {
                         Intent intent = new Intent(ButtonSettingsActivity.this, DisplayCalibration.class);
                         startActivity(intent);
                         return true;
                     }
                });

        mVibratorStrength = (VibratorStrengthPreference) findPreference("vibrator_key");
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
            mVibratorStrength.setOnPreferenceChangeListener(this);
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        fixVolte = (SwitchPreference) findPreference("fix_volte");
        fixVolte.setChecked(mPrefs.getBoolean("fix_volte", false));
        fixVolte.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferencesBasedOnDependencies();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String node = Constants.sBooleanNodePreferenceMap.get(preference.getKey());
        if (!TextUtils.isEmpty(node)) {
            Boolean value = (Boolean) newValue;
            FileUtils.writeLine(node, value ? "1" : "0");
            if (Constants.FP_WAKEUP_KEY.equals(preference.getKey())) {
                Utils.broadcastCustIntent(this, value);
            }
            return true;
        }
        node = Constants.sStringNodePreferenceMap.get(preference.getKey());
        if (!TextUtils.isEmpty(node)) {
            FileUtils.writeLine(node, (String) newValue);
            return true;
        }
        
        if (preference == mVibratorStrength) {
            return true;
        }

        if(preference == fixVolte){
            Boolean enabled = (Boolean) newValue;
            if(enabled){
                int val = Settings.Global.getInt(getContentResolver(), Settings.Global.MULTI_SIM_DATA_CALL_SUBSCRIPTION, -1);
                mPrefs.edit().putInt("MULTI_SIM_DATA_CALL_SUBSCRIPTION", val).commit();
                Settings.Global.putInt(getContentResolver(), Settings.Global.MULTI_SIM_DATA_CALL_SUBSCRIPTION, -1);
            } else {
                int val = Settings.Global.getInt(getContentResolver(), Settings.Global.MULTI_SIM_DATA_CALL_SUBSCRIPTION, -1);
                if(val == -1){                    
                    Settings.Global.putInt(getContentResolver(), Settings.Global.MULTI_SIM_DATA_CALL_SUBSCRIPTION, mPrefs.getInt("MULTI_SIM_DATA_CALL_SUBSCRIPTION", -1));
                }
            }
            mPrefs.edit().putBoolean("fix_volte", enabled).commit();
            return true;   
        }

        return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        // Initialize node preferences
        for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            b.setOnPreferenceChangeListener(this);
            String node = Constants.sBooleanNodePreferenceMap.get(pref);
            if (new File(node).exists()) {
                String curNodeValue = FileUtils.readOneLine(node);
                b.setChecked(curNodeValue.equals("1"));
            } else {
                b.setEnabled(false);
            }
        }
        for (String pref : Constants.sStringNodePreferenceMap.keySet()) {
            ListPreference l = (ListPreference) findPreference(pref);
            if (l == null) continue;
            l.setOnPreferenceChangeListener(this);
            String node = Constants.sStringNodePreferenceMap.get(pref);
            if (new File(node).exists()) {
                l.setValue(FileUtils.readOneLine(node));
            } else {
                l.setEnabled(false);
            }
        }
    }

    private void updatePreferencesBasedOnDependencies() {
        for (String pref : Constants.sNodeDependencyMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            String dependencyNode = Constants.sNodeDependencyMap.get(pref)[0];
            if (new File(dependencyNode).exists()) {
                String dependencyNodeValue = FileUtils.readOneLine(dependencyNode);
                boolean shouldSetEnabled = dependencyNodeValue.equals(
                        Constants.sNodeDependencyMap.get(pref)[1]);
                Utils.updateDependentPreference(this, b, pref, shouldSetEnabled);
            }
        }
    }

    /*@Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof VibratorStrengthPreference){
            ((VibratorStrengthPreference)preference).onDisplayPreferenceDialog(preference);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }*/

    public static void restoreFixVolte(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean val = pref.getBoolean("fix_volte", false);
        if(val==true){
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.MULTI_SIM_DATA_CALL_SUBSCRIPTION, -1);
        }
    }
}
