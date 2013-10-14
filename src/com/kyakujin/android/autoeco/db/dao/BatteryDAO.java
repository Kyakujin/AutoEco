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

package com.kyakujin.android.autoeco.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.kyakujin.android.autoeco.db.AutoEcoDBOpenHelperV1;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryTbl;

/**
 * Data Access Object for Battery Table.
 */
public class BatteryDAO {
    int mDbVersion;
    Context mContext;
    AutoEcoDBOpenHelperV1 mV1Helper;

    public BatteryDAO(Context context) {
        super();
        mContext = context;
    }

    private BatteryModel createDefaultBatteryModel() {
        BatteryModel model = new BatteryModel();
        model.setEnabled(true);
        model.setThreshold(20);
        return model;
    }

    private ContentValues createDefaultContentValues() {
        BatteryModel model = createDefaultBatteryModel();
        ContentValues val = new ContentValues();
        val.put(BatteryTbl.ENABLED, model.getEnabled() == true ? 1 : 0);
        val.put(BatteryTbl.THRESHOLD, model.getThreshold());
        return val;
    }

    public Uri insertDefaultBattery() {
        return insertBattery(createDefaultBatteryModel());
    }

    public void insertBattery(SQLiteDatabase db, BatteryModel data) {
        String query;
        int enabled = data.getEnabled() == true ? 1 : 0;
        query = "insert into battry(enabled, threshold) values ("
                + "'" + enabled + "','" + data.getThreshold() + "')";
        db.execSQL(query);
    }

    public Uri insertBattery(BatteryModel model) {
        ContentValues val = createDefaultContentValues();
        val.put(BatteryTbl.ENABLED, model.getEnabled() == true ? 1 : 0);
        val.put(BatteryTbl.ENABLED, model.getThreshold());
        return mContext.getContentResolver().insert(BatteryTbl.CONTENT_URI, val);
    }

    public void updateThreshold(BatteryModel model) {
        ContentValues values = new ContentValues();
        values.put(BatteryTbl.THRESHOLD, model.getThreshold());
        mContext.getContentResolver().update(Uri.withAppendedPath(BatteryTbl.CONTENT_URI,
                String.valueOf(model.getId())), values, null, null);
    }

    public void updateEnabled(int batteryId, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(BatteryTbl.ENABLED, enabled == true ? 1 : 0);
        mContext.getContentResolver().update(Uri.withAppendedPath(BatteryTbl.CONTENT_URI,
                String.valueOf(batteryId)), values, null, null);
    }

}
