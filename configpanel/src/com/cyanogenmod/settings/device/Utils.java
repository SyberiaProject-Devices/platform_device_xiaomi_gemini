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

package com.cyanogenmod.settings.device;

import android.content.Context;
import android.content.Intent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.PreferenceManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.lang.ProcessBuilder;

public class Utils {

    /**
     * Write a string value to the specified file.
     * @param filename      The filename
     * @param value         The value
     */
    public static void writeValue(String filename, String value) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(filename));
            fos.write(value.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String filename) {
        return new File(filename).exists();
    }

    public static boolean fileWritable(String filename) {
        return fileExists(filename) && new File(filename).canWrite();
    }

    public static boolean isPreferenceEnabled(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, (Boolean) Constants.sNodeDefaultMap.get(key));
    }

    public static String getPreferenceString(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, (String) Constants.sNodeDefaultMap.get(key));
    }

    public static void updateDependentPreference(Context context, SwitchPreference b,
            String key, boolean shouldSetEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean prefActualValue = preferences.getBoolean(key, false);

        if (shouldSetEnabled) {
            if (Constants.sNodeUserSetValuesMap.get(key) != null &&
                    (Boolean) Constants.sNodeUserSetValuesMap.get(key)[1] &&
                    (Boolean) Constants.sNodeUserSetValuesMap.get(key)[1] != prefActualValue) {
                b.setChecked(true);
                Constants.sNodeUserSetValuesMap.put(key, new Boolean[]{ prefActualValue, false });
            }
        } else {
            if (b.isEnabled() && prefActualValue) {
                Constants.sNodeUserSetValuesMap.put(key, new Boolean[]{ prefActualValue, true });
            }
            b.setEnabled(false);
            b.setChecked(false);
        }
    }

    public static void broadcastCustIntent(Context context, boolean value) {
        final Intent intent = new Intent(Constants.CUST_INTENT);
        intent.putExtra(Constants.CUST_INTENT_EXTRA, value);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    public static String getFileValue(String filename, String defValue) {
        String fileValue = readLine(filename);
        if(fileValue!=null){
            return fileValue;
        }
        return defValue;
    }

    public static String readLine(String filename) {
        if (filename == null) {
            return null;
        }
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(filename), 1024);
            line = br.readLine();
        } catch (IOException e) {
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return line;
    }

    /**
     * Convert color temperature in Kelvins to ColorMatrix color
     * @param temperature
     * @return array of ColorMatrix (R, G, B)
     */
    public static int[] RGBfromK(int temperature) {
        int[] rgb = new int[3];
        temperature = temperature / 100;
        double red;
        double green;
        double blue;
        // R
        if (temperature <=66 )red = 255;
        else {
            red = temperature - 60;
            red = 329.698727446 * (Math.pow (red, -0.1332047592));
            if (red < 0) red = 0;
            if (red > 255) red = 255;
        }
        // G
        if (temperature <= 66){
            green = temperature;
            green = 99.4708025861 * Math.log(green) - 161.1195681661;
            if (green < 0) green = 0;
            if (green > 255) green = 255;
        }
        else {
            green = temperature - 60;
            green = 288.1221695283 * (Math.pow(green, -0.0755148492));
            if (green < 0) green = 0;
            if (green > 255) green = 255;
        }

        // B
        if (temperature >= 66) blue = 255;
        else 
        if (temperature <= 19) blue = 0;
        else {
            blue = temperature - 10;
            blue = 138.5177312231 * Math.log(blue) - 305.0447927307;
            if (blue < 0) blue = 0;
            if (blue > 255) blue = 255;
        }

        rgb[0] = (int) red;
        rgb[1] = (int) green;
        rgb[2] = (int) blue;
        Log.e("RGBfromK",""+temperature+" "+red+" "+" "+green+" "+blue);
        return rgb;
    }

    public static int KfromRGB(double R, double G, double B) {
        double r, g, b, X, Y, Z, xr, yr, zr;

        // D65/2°
        double Xr = 95.047;
        double Yr = 100.0;
        double Zr = 108.883;

        r = R/255.0;
        g = G/255.0;
        b = B/255.0;

        if (r > 0.04045)
            r = Math.pow((r+0.055)/1.055,2.4);
        else
            r = r/12.92;

        if (g > 0.04045)
            g = Math.pow((g+0.055)/1.055,2.4);
        else
            g = g/12.92;

        if (b > 0.04045)
            b = Math.pow((b+0.055)/1.055,2.4);
        else
            b = b/12.92 ;

        r*=100;
        g*=100;
        b*=100;

        X =  0.4124*r + 0.3576*g + 0.1805*b;
        Y =  0.2126*r + 0.7152*g + 0.0722*b;
        Z =  0.0193*r + 0.1192*g + 0.9505*b;

        double x =  (X/(X+Y+Z));
        double y =  (Y/(X+Y+Z));

        int CCT=(int) ((-449*Math.pow((x-0.332)/(y-0.1858), 3))+(3525*Math.pow((x-0.332)/(y-0.1858), 2))-(6823.3*((x-0.332)/(y-0.1858)))+(5520.33));
        return CCT;
    }

    public static double clamp(double x, double min, double max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }
        return x;
    }

    public static String getPanelName() {
        StringBuilder cmdReturn = new StringBuilder();
        try {            
            ProcessBuilder processBuilder = new ProcessBuilder("sh","-c","(cat /sys/class/graphics/fb0/msm_fb_panel_info | grep panel_name)");
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            int c;
            while ((c = inputStream.read()) != -1) {
                cmdReturn.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }        
        return cmdReturn.toString();
    }

    public static boolean isLGDPanel() {
        return getPanelName().toLowerCase().trim().contains("panel_name=lgd fhd cmd incell dsi panel".toLowerCase().trim());
    }

    public static boolean isSharpPanel() {
        return getPanelName().toLowerCase().trim().contains("panel_name=sharp fhd cmd incell dsi panel".toLowerCase().trim());
    }

    private static final KKalParams LGDPanel = new KKalParams(230, 230, 244, 268, 255);
    private static final KKalParams sharpPanel = new KKalParams(242, 242, 255, 256, 255);
    private static final KKalParams defaultPanel = new KKalParams();

    public static KKalParams getDefaultParams() {
        return defaultPanel;
    }

    public static KKalParams getParams() {
        if(isLGDPanel())
            return LGDPanel;
        else if(isSharpPanel())
            return sharpPanel;
        else 
            return defaultPanel;
    }

    public static boolean isNotDefaultPanel() {
        if(isLGDPanel())
            return true;
        else if(isSharpPanel())
            return true;
        else 
            return false;
    }

    public static class KKalParams {
        public int red = 256;
        public int green = 256;
        public int blue = 256;
        public int saturation = 256;
        public int contrast = 255;

        public KKalParams(){}

        public KKalParams(int r, int g, int b, int s, int c){
            red = r;
            green = g;
            blue = b;
            saturation = s;
            contrast = c;
        }
    }
}
