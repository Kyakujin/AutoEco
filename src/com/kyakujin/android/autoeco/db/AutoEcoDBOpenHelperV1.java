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

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.ManualTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.MappingTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DataBase OpenHelper Version 1.
 */
public class AutoEcoDBOpenHelperV1 extends AutoEcoDBOpenHelper {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();
    private static final int DB_VERSION = 1;

    public AutoEcoDBOpenHelperV1(Context context) {
        super(context, DB_VERSION);
    }

    interface Tables {
        String ECO = EcoTbl.getPath();
        String SCHED = SchedTbl.getPath();
        String BATTERY = BatteryTbl.getPath();
        String MANUAL = ManualTbl.getPath();
        String MAPPING = MappingTbl.getPath();
    }

    /** 削除トリガー */
    interface Triggers {
        String MAPPING_DELETE = "mapping_delete";
        String ECO_DELETE = "eco_delete";
        String ECO_UPDATE = "eco_update";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + Tables.ECO + " ("
                + EcoTbl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EcoTbl.NAME + " TEXT, "
                + EcoTbl.UPDATE_DATE + " TIMESTAMP DEFAULT (DATETIME('now','localtime')), "
                + EcoTbl.WIFI_ENABLED + " INTEGER, "
                + EcoTbl.BLUETOOTH_ENABLED + " INTEGER, "
                + EcoTbl.ROTATE_ENABLED + " INTEGER, "
                + EcoTbl.SYNC_ENABLED + " INTEGER, "
                + EcoTbl.BRIGHTNESS_ENABLED + " INTEGER, "
                + EcoTbl.BRIGHTNESS_VALUE + " INTEGER, "
                + EcoTbl.BRIGHTNESS_AUTO + " INTEGER, "
                + EcoTbl.SLEEP_ENABLED + " INTEGER, "
                + EcoTbl.SLEEP_TIME + " INTEGER, "
                + EcoTbl.SILENT_ENABLED + " INTEGER, "
                + EcoTbl.SILENT_MODE + " INTEGER"
                + ");");

        db.execSQL("CREATE TABLE " + Tables.SCHED + " ("
                + SchedTbl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SchedTbl.ENABLED + " INTEGER, "
                + SchedTbl.HOUR + " INTEGER, "
                + SchedTbl.MINUTE + " INTEGER, "
                + SchedTbl.HOUR_MINUTE_STRING + " TEXT, "
                + SchedTbl.PATTERN + " INTEGER, "
                // + "UNIQUE(" + SchedTbl.HOUR + ", " + SchedTbl.MINUTE + ")"
                + "UNIQUE(" + SchedTbl.HOUR_MINUTE_STRING + ")"

                + ");");

        db.execSQL("CREATE TABLE " + Tables.BATTERY + " ("
                + BatteryTbl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BatteryTbl.ENABLED + " INTEGER, "
                + BatteryTbl.THRESHOLD + " INTEGER "
                + ");");

        db.execSQL("CREATE TABLE " + Tables.MANUAL + " ("
                + ManualTbl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ManualTbl.NAME + " TEXT "
                + ");");
        
        db.execSQL("CREATE TABLE " + Tables.MAPPING + " ("
                + MappingTbl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MappingTbl.ECO_ID + " INTEGER,"
                + MappingTbl.SCHED_ID + " INTEGER,"
                + MappingTbl.BATTERY_ID + " INTEGER, "
                + MappingTbl.MANUAL_ID + " INTEGER, "                
                + "UNIQUE(" + MappingTbl.ECO_ID + ", " + MappingTbl.SCHED_ID + ")"
                + ");");

        db.execSQL("CREATE TRIGGER " + Triggers.MAPPING_DELETE
                + " AFTER DELETE ON " + Tables.SCHED
                + " BEGIN DELETE FROM " + Tables.MAPPING
                + " WHERE " + MappingTbl.SCHED_ID + " =old." + SchedTbl._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + Triggers.ECO_DELETE
                + " AFTER DELETE ON " + Tables.MAPPING
                + " BEGIN DELETE FROM " + Tables.ECO
                + " WHERE " + EcoTbl._ID + " =old." + MappingTbl.ECO_ID
                + ";" + " END;");
        
        db.execSQL("CREATE TRIGGER " + Triggers.ECO_UPDATE
                + " AFTER UPDATE ON " + Tables.ECO
                + " BEGIN UPDATE " + Tables.ECO + " SET " + EcoTbl.UPDATE_DATE 
                + " = (DATETIME('now','localtime'))" 
                + " WHERE " + EcoTbl._ID + " =old." + EcoTbl._ID
                + ";" + " END;");        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "upgrading database from version " + oldVersion + " to"
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ECO);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHED);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.BATTERY);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MANUAL);        
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MAPPING);

        onCreate(db);
    }

    @Override
    public int getVersion() {
        return DB_VERSION;
    }

}
