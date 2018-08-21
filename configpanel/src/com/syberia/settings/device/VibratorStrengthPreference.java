/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.syberia.settings.device;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.database.ContentObserver;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Bundle;
import android.util.Log;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.util.Log;

import java.util.List;

public class VibratorStrengthPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, DialogInterface.OnDismissListener {

    public static final String KEY_VIBSTRENGTH = "vib_strength";

    private SeekBar mSeekBar;
    private int mOldStrength;
    private int mMinValue;
    private int mMaxValue;
    private float offset;
    private Vibrator mVibrator;
    private TextView mValueText;

    private int buttonPress = DialogInterface.BUTTON_NEGATIVE;

    private static final String FILE_LEVEL = "/sys/class/timed_output/vibrator/vtg_level";
    private static final String FILE_MIN = "/sys/class/timed_output/vibrator/vtg_min";
    private static final String FILE_MAX = "/sys/class/timed_output/vibrator/vtg_max";
    private static final long testVibrationPattern[] = {0,250};

    public VibratorStrengthPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        setNegativeButtonText(android.R.string.cancel);
        setPositiveButtonText(android.R.string.ok);
        setDialogTitle(R.string.vibrator_summary);                
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = super.onCreateView(parent);
        return v;
    }

    @Override
    protected View onCreateDialogView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.preference_dialog_vibrator_strength, null);

        mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);
        mValueText = (TextView) view.findViewById(R.id.current_value);

        mOldStrength = Integer.parseInt(getValue(getContext()));
        mMinValue = Integer.parseInt(Utils.getFileValue(FILE_MIN, "0"));
        mMaxValue = Integer.parseInt(Utils.getFileValue(FILE_MAX, "100"));
        offset = mMaxValue/100f;

        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mOldStrength - mMinValue);
        mValueText.setText(Integer.toString(Math.round(mOldStrength / offset)) + "%");            
        mSeekBar.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        buttonPress = which;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult){
        super.onDialogClosed(positiveResult);
        if(buttonPress == DialogInterface.BUTTON_POSITIVE){
            Log.e("mVibrator", "onClick BUTTON_POSITIVE");
            final int value = mSeekBar.getProgress() + mMinValue;
            setValue(String.valueOf(value));
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putString(KEY_VIBSTRENGTH, String.valueOf(value));
            editor.commit();

            mVibrator.cancel();
        } else {
            Log.e("mVibrator", "onClick BUTTON_NEGATIVE");
            restoreOldState();

            mVibrator.cancel();
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        setValue(String.valueOf(progress + mMinValue));
        mValueText.setText(Integer.toString(Math.round((progress + mMinValue) / offset)) + "%");        
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mVibrator.hasVibrator())
            mVibrator.vibrate(testVibrationPattern, -1);
    }

    public static boolean isSupported() {
        return Utils.fileWritable(FILE_LEVEL);
    }

    public static String getValue(Context context) {
        Log.e("mVibrator", "getValue "+Utils.getFileValue(FILE_LEVEL, "100"));
        return Utils.getFileValue(FILE_LEVEL, "100");
    }

    private void setValue(String newValue) {
        Log.e("mVibrator", "setValue "+newValue);
        Utils.writeValue(FILE_LEVEL, newValue);
    }

    public static void restore(Context context) {
        Log.e("mVibrator", "restore ");
        if (!isSupported()) {
            return;
        }

        String storedValue = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_VIBSTRENGTH, "100"); 
        Utils.writeValue(FILE_LEVEL, storedValue);
    }

    private void restoreOldState() {
        setValue(String.valueOf(mOldStrength));
    }
}
