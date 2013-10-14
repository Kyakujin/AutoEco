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

package com.kyakujin.android.autoeco.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link AutoEcoProvider}.
 */
public class AutoEcoContract {

    private AutoEcoContract() {
    }

    interface EcoColumns {
        // eco設定名
        public static final String NAME = "name";
        public static final String UPDATE_DATE = "update_date";
        public static final String WIFI_ENABLED = "wifi_enabled";
        public static final String BLUETOOTH_ENABLED = "bluetooth_enabled";
        public static final String ROTATE_ENABLED = "rotate_enabled";
        public static final String SYNC_ENABLED = "sync_enabled";
        public static final String BRIGHTNESS_ENABLED = "brightness_enabled";
        public static final String BRIGHTNESS_VALUE = "brightness_value";
        public static final String BRIGHTNESS_AUTO = "brightness_auto";        
        public static final String SLEEP_ENABLED = "sleep_enabled";
        public static final String SLEEP_TIME = "sleep_time";
        public static final String SILENT_ENABLED = "silent_enabled";
        public static final String SILENT_MODE = "silent_mode";
    }

    interface SchedColumns {
        // Enable/Disable状態
        public static final String ENABLED = "enabled";
        // 設定時刻(時)
        public static final String HOUR = "hourofday";
        // 設定時刻(分)
        public static final String MINUTE = "minute";
        // 設定時刻表示用
        public static final String HOUR_MINUTE_STRING = "hour_minute_string";
        // 曜日設定(並び順は「月..日」で1がONを表す)
        public static final String PATTERN = "pattern";
    }

    interface BatteryColumns {
        // Enable/Disable状態
        public static final String ENABLED = "enabled";
        // しきい値
        public static final String THRESHOLD = "threshold";
    }

    interface ManualColumns {
        // Enable/Disable状態
        public static final String NAME = "name";
    }
    
    // スケジュールorバッテリー連動とeco設定を紐付けるためのテーブル
    interface MappingColumns {
        public static final String ECO_ID = "ecoid";
        public static final String SCHED_ID = "schedid";
        public static final String BATTERY_ID = "batteryid";
        public static final String MANUAL_ID = "manualid";
    }

    public static final String CONTENT_AUTHORITY = "com.kyakujin.android.autoeco";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private class Path {
        private static final String ECO = "eco";
        private static final String SCHED = "sched";
        private static final String BATTERY = "battery";
        private static final String MANUAL = "manual";
        private static final String MAPPING = "mapping";
    }

    private static Uri getContentUri(String path) {
        return BASE_CONTENT_URI.buildUpon().appendPath(path).build();
    }

    private static String getContentType(String path) {
        return "vnd.android.cursor.dir/vnd.autoeco." + path;
    }

    private static String getContentItemtype(String path) {
        return "vnd.android.cursor.item/vnd.autoeco." + path;
    }

    public static class SchedTbl implements SchedColumns, BaseColumns {
        private static String path = Path.SCHED;
        public static final Uri CONTENT_URI = getContentUri(path);
        public static final String CONTENT_TYPE = getContentType(path);
        public static final String CONTENT_ITEM_TYPE = getContentItemtype(path);

        // クエリ時のデフォルトソート
        public static final String DEFAULT_SORT = BaseColumns._ID + " ASC";
        
        // パス名の取得
        public static String getPath() {
            return path;
        }
    }

    public static class BatteryTbl implements BatteryColumns, BaseColumns {
        private static String path = Path.BATTERY;
        public static final Uri CONTENT_URI = getContentUri(path);
        public static final String CONTENT_TYPE = getContentType(path);
        public static final String CONTENT_ITEM_TYPE = getContentItemtype(path);
        
        // パス名の取得
        public static String getPath() {
            return path;
        }        
    }

    public static class EcoTbl implements EcoColumns, BaseColumns {
        private static String path = Path.ECO;
        public static final Uri CONTENT_URI = getContentUri(path);
        public static final String CONTENT_TYPE = getContentType(path);
        public static final String CONTENT_ITEM_TYPE = getContentItemtype(path);

        // クエリ時のデフォルトソート
        public static final String DEFAULT_SORT = EcoColumns.NAME + " DESC";
        
        // パス名の取得
        public static String getPath() {
            return path;
        }        
    }

    public static class ManualTbl implements ManualColumns, BaseColumns {
        private static String path = Path.MANUAL;
        public static final Uri CONTENT_URI = getContentUri(path);
        public static final String CONTENT_TYPE = getContentType(path);
        public static final String CONTENT_ITEM_TYPE = getContentItemtype(path);
        
        // パス名の取得
        public static String getPath() {
            return path;
        }        
    }
    
    public static class MappingTbl implements MappingColumns, BaseColumns {
        private static String path = Path.MAPPING;
        public static final Uri CONTENT_URI = getContentUri(path);
        public static final String CONTENT_TYPE = getContentType(path);
        public static final String CONTENT_ITEM_TYPE = getContentItemtype(path);
        
        // パス名の取得
        public static String getPath() {
            return path;
        }        
    }

    // for loader
    
    /**
     * The Interface EcoQuery.
     */
    public interface EcoQuery {

        int LOADER_ID = 0;

        String[] PROJECTION = {
                EcoTbl._ID,
                EcoTbl.NAME,
                EcoTbl.UPDATE_DATE,
                EcoTbl.WIFI_ENABLED,
                EcoTbl.BLUETOOTH_ENABLED,
                EcoTbl.ROTATE_ENABLED,
                EcoTbl.SYNC_ENABLED,                
                EcoTbl.BRIGHTNESS_ENABLED,
                EcoTbl.BRIGHTNESS_VALUE,
                EcoTbl.BRIGHTNESS_AUTO,
                EcoTbl.SLEEP_ENABLED,
                EcoTbl.SLEEP_TIME,
                EcoTbl.SILENT_ENABLED,
                EcoTbl.SILENT_MODE,
        };

        enum Idx {
             _ID,
             NAME,
             UPDATE_DATE,
             WIFI_ENABLED,
             BLUETOOTH_ENABLED,
             ROTATE_ENABLED,
             SYNC_ENABLED,        
             BRIGHTNESS_ENABLED,
             BRIGHTNESS_VALUE,
             BRIGHTNESS_AUTO,
             SLEEP_ENABLED,
             SLEEP_TIME,        
             SILENT_ENABLED,
             SILENT_MODE,
        }
    }
    
    
    /**
     * The Interface SchedQuery.
     */
    public interface SchedQuery {

        int LOADER_ID = 1;

        String[] PROJECTION = {
                SchedTbl._ID,
                SchedTbl.ENABLED,
                SchedTbl.HOUR,
                SchedTbl.MINUTE,
                SchedTbl.HOUR_MINUTE_STRING,
                SchedTbl.PATTERN,
        };
        
        enum Idx {
            _ID,
            ENABLED,
            HOUR,
            MINUTE,
            HOUR_MINUTE_STRING,
            PATTERN,
        }
    }


    /**
     * The Interface BatteryQuery.
     */
    public interface BatteryQuery {

        int LOADER_ID = 2;

        String[] PROJECTION = {
                BatteryTbl._ID,
                BatteryTbl.ENABLED,
                BatteryTbl.THRESHOLD,
        };

        enum Idx {
            _ID,
            ENABLED,
            THRESHOLD,
        }
    }

    /**
     * The Interface ManualQuery.
     */
    public interface ManualQuery {

        int LOADER_ID = 3;

        String[] PROJECTION = {
                ManualTbl._ID,
                ManualTbl.NAME,
        };

        enum Idx {
            _ID,
            NAME,
        }
    }
    
    /**
     * The Interface MappingQuery.
     */
    public interface MappingQuery {

        int LOADER_ID = 4;

        String[] PROJECTION = {
                MappingTbl._ID,
                MappingTbl.ECO_ID,                
                MappingTbl.SCHED_ID,
                MappingTbl.BATTERY_ID,                
        };

        enum Idx {
            _ID,
            ECO_ID,
            SCHED_ID,
            BATTERY_ID,
        }
    }
}
