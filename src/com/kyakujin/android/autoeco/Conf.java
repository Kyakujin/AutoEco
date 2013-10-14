/*
 * Copyright 2013 Yoshihiro Miyama
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

package com.kyakujin.android.autoeco;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーション全体で使用する共通データ
 */
public final class Conf {
    // TODO Caution:リリース時にfalseにすること
    public static boolean isTest = false;

    public enum EcoExecSwitch {
        ECO_OFF,
        ECO_ON
    }
    public enum EcoExecFrom {
        SCHED,
        BATTERY,
        MANUAL
    }
    @SuppressLint("UseSparseArrays")
    public static Map<Integer, EcoExecFrom> mapEcoExecFrom = new HashMap<Integer,EcoExecFrom>() {
        /**
         * 
         */
        private static final long serialVersionUID = 2183879381907038058L;

        {
            put(EcoExecFrom.SCHED.ordinal(), EcoExecFrom.SCHED);
            put(EcoExecFrom.BATTERY.ordinal(), EcoExecFrom.BATTERY);
            put(EcoExecFrom.MANUAL.ordinal(), EcoExecFrom.MANUAL);
            
        }
    };    
    public enum Priority {
        SCHED,
        BATTERY,
        DEFAULT
    }
    
    public static final int OVER_THRESHOLD = 1;
    public static final int UNDER_THRESHOLD = 0;
    
    public static final String APP_NAME = "AutoEco";
    public static final int TRUE = 1;
    public static final int FALSE = 0;    
    public static final String NONE = "none";
    
    public static final String THREAD_ECOSCHED_SERVICE = "threadEcoSchedService";
    public static final String BROADCAST_ACTION = "broadCastSched";


    // 2進数で11111111 (1, 月, 火, 水, 木, 金, 土, 日)    
    public static final int DEFAULT_REPEAT_PATTERN = 255;
    
    public static final String FRAGTAG_TOP = "fragmentTagTop";
    public static final String FRAGTAG_ECO = "fragmentTagEco";
    public static final String FRAGTAG_SCHED = "fragmentTagSched";
    public static final String FRAGTAG_BATTERY = "fragmentTagBattery";
    public static final String FRAGTAG_MANUAL = "fragmentTagManual";
    public static final String FRAGTAG_DEFAULT = "fragmentTagDefault";

    public static final String FRAGTAG_WIFI_SETTING = "fragmentWifiSetting";
    public static final String FRAGTAG_BLUETOOTH_SETTING = "fragmentBluetoothSetting";
    public static final String FRAGTAG_ROTATE_SETTING = "fragmentRotateSetting";
    public static final String FRAGTAG_SYNC_SETTING = "fragmentSyncSetting";
    public static final String FRAGTAG_BRIGHTNESS_SETTING = "fragmentBrightnessSetting";
    public static final String FRAGTAG_SILENT_SETTING = "fragmentSilentSetting";
    public static final String FRAGTAG_SLEEP_SETTING = "fragmentSleepSetting";
    
    
    public static final String SHARED_ECOID = "sharedEcoId";
    public static final String SHARED_SCHEDID = "sharedSchedId";
    public static final String SHARED_BATTERYID = "sharedBatteryId";
    public static final String SHARED_MANUALID = "sharedManualId";
    public static final String SHARED_MAPPINGID = "sharedMappingId";
    public static final String SHARED_WIFI_ENABLED = "sharedWifiEnabled";
    public static final String SHARED_BLUETOOTH_ENABLED = "sharedBluetoothEnabled";
    public static final String SHARED_ROTATE_ENABLED = "sharedRotateEnabled";
    public static final String SHARED_SYNC_ENABLED = "sharedSyncEnabled";    
    public static final String SHARED_SILENTMODE = "sharedsilentMode";
    public static final String SHARED_BRIGHTNESSVALUE = "sharedbrightnessValue";
    public static final String SHARED_BRIGHTNESSAUTO = "sharedbrightnessAuto";
    public static final String SHARED_SLEEPTIME_ORDINAL = "sharedsleepTime";
    
    public static final String SHARED_EXTRA_ID = "sharedExtraId";
    public static final String SHARED_EXTRA_FROM = "sharedExtraFrom";

    public static final String SHARED_ALARM_SCHEDID = "alarmSchedId";
    
    public static final int BIT_MON = 64;
    public static final int BIT_TUE = 32;
    public static final int BIT_WED = 16;
    public static final int BIT_THU = 8;
    public static final int BIT_FRI = 4;
    public static final int BIT_SAT = 2;
    public static final int BIT_SUN = 1;

    public static final String PREF = "pref";
    public static final String PREFKEY_ECOSTATE = "currentEcoState";
    public static final String PREFKEY_LAST_ECOFROM = "execFrom";
    public static final String PREFKEY_BATTERYSRV_ENABLED = "batterySrvEnabled";
    public static final String PREFKEY_UPDATE_TIME = "ecoUpdateTime";
    public static final String PREFKEY_BATTERY_LEVEL = "batteryLevel";
    
    @SuppressLint("UseSparseArrays")
    public static Map<Integer, SleepTime> mapSleepTime = new HashMap<Integer, SleepTime>() {
        /**
         * 
         */
        private static final long serialVersionUID = 3036581715514812584L;

        {
            put(SleepTime.TIME1.ordinal(), SleepTime.TIME1);
            put(SleepTime.TIME2.ordinal(), SleepTime.TIME2);
            put(SleepTime.TIME3.ordinal(), SleepTime.TIME3);
            put(SleepTime.TIME4.ordinal(), SleepTime.TIME4);
            put(SleepTime.TIME5.ordinal(), SleepTime.TIME5);
            put(SleepTime.TIME6.ordinal(), SleepTime.TIME6);            
        }
    };
    
    public enum SleepTime {
        TIME1(15 * 1000),
        TIME2(30 * 1000),
        TIME3(60 * 1000),
        TIME4((60 + 30) * 1000),
        TIME5(60 * 10 * 1000),
        TIME6(60 * 30 * 1000);

        private int time;

        private SleepTime(int time) {
            this.time = time;
        }

        public int toTimeValue() {
            return this.time;
        }
    }
    
    // admob
    // "XXXXXXXXXXXXXXX"にはadmobのIDを指定
    public static final String MY_AD_UNIT_ID = "XXXXXXXXXXXXXXX";    
  }

