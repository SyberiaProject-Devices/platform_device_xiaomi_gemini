/*
 * Copyright (C) 2017-2019 The LineageOS Project
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

package org.lineageos.settings.device;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.database.ContentObserver;
import android.util.Log;
import android.provider.Settings;
import android.view.KeyEvent;
import android.os.UserHandle;

import com.android.internal.os.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = KeyHandler.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final int KEY_HOME = 102;
    private static final int KEY_HOME_VIRTUAL = 96;
    private static final int KEY_BACK = 158;
    private static final int KEY_RECENTS = 139;

    private static final int[] sDisabledKeys = new int[]{
        KEY_HOME,
        KEY_HOME_VIRTUAL,
        KEY_BACK,
        KEY_RECENTS
    };

    protected final Context mContext;
    private Handler mHandler = new Handler();
    private SettingsObserver mSettingsObserver;
    private static boolean mButtonDisabled;

    private class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(
                    Settings.Secure.HARDWARE_KEYS_DISABLE),
                    false, this);
            update();
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }


        public void update() {
            if (DEBUG) Log.i(TAG, "update called" );
            setButtonSetting(mContext);
        }
    }

    public KeyHandler(Context context) {
        if (DEBUG) Log.i(TAG, "KeyHandler called");
        mContext = context;
	mSettingsObserver = new SettingsObserver(mHandler);
        mSettingsObserver.observe();
    }

    @Override
    public boolean handleKeyEvent(KeyEvent event) {
        if (DEBUG) Log.i(TAG, "handleKeyEvent called - scancode=" + event.getScanCode() + " - keyevent=" + event.getAction());
        return false;
    }

    @Override
    public boolean canHandleKeyEvent(KeyEvent event) {
        Log.i(TAG, "canHandleKeyEvent called - scancode=" + event.getScanCode() + " - keyevent=" + event.getAction());
        return false;
    }


    public static void setButtonSetting(Context context) {
        if (DEBUG) Log.i(TAG, "SetButtonDisable called" );
        mButtonDisabled = Settings.Secure.getIntForUser(
                context.getContentResolver(), Settings.Secure.HARDWARE_KEYS_DISABLE, 0,
                UserHandle.USER_CURRENT) == 1;
        if (DEBUG) Log.i(TAG, "setButtonDisable=" + mButtonDisabled);
    }

    @Override
    public boolean isCameraLaunchEvent(KeyEvent event) {
        return false;
    }

    @Override
    public boolean isWakeEvent(KeyEvent event){
        if (DEBUG) Log.i(TAG, "isWakeEvent called - scancode=" + event.getScanCode() + " - keyevent=" + event.getAction());
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
            if (event.getScanCode() == KEY_HOME) {
                if (DEBUG) Log.i(TAG, "KEY_HOME pressed");
                return true;
	    }
        return false;
    }

    @Override
    public boolean isDisabledKeyEvent(KeyEvent event) {
        if (DEBUG) Log.i(TAG, "isDisabledKeyEvent called");
        if (mButtonDisabled) {
            if (DEBUG) Log.i(TAG, "Buttons are disabled");
            if (ArrayUtils.contains(sDisabledKeys, event.getScanCode())) {
                if (DEBUG) Log.i(TAG, "Key blocked=" + event.getScanCode());
                return true;
            }
        }
        return false;
    }

    @Override
    public Intent isActivityLaunchEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return null;
        }
        return null;
    }
}
