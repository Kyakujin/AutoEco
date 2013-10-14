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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.kyakujin.android.autoeco.db.AutoEcoDBOpenHelperV1;
import com.kyakujin.android.autoeco.db.AutoEcoContract.MappingQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.MappingTbl;

/**
 * Data Access Object for Mapping Table.
 */
public class MappingDAO {

    int mDbVersion;
    Context mContext;
    AutoEcoDBOpenHelperV1 mV1Helper;

    public MappingDAO(Context context) {
        super();
        mContext = context;
    }

    public void insertMapping(SQLiteDatabase db, MappingModel data) {
        String query;
        query = "insert into mapping(ecoid, schedid, batteryid, manualid) values (" + "'"
                + data.getEcoid() + "','" + data.getSchedid() + "','" + data.getBatteryid() + "','"
                + data.getManualid() + "')";
        db.execSQL(query);
    }

    public Uri insertMapping(MappingModel model) {
        ContentValues val = new ContentValues();
        val.put(MappingTbl.ECO_ID, model.getEcoid());
        val.put(MappingTbl.SCHED_ID, model.getSchedid());
        val.put(MappingTbl.BATTERY_ID, model.getBatteryid());
        val.put(MappingTbl.MANUAL_ID, model.getManualid());
        return mContext.getContentResolver().insert(MappingTbl.CONTENT_URI, val);
    }

    private int searchMappingIdCommon(int id, String where) {
        Cursor c = null;
        int mappingId = 0;
        try {
            c = mContext.getContentResolver().query(MappingTbl.CONTENT_URI,
                    MappingQuery.PROJECTION, where, null, null);
            if (c.moveToFirst()) {
                // Mappingテーブルに存在したのでMappingの該当idを取得
                mappingId = c.getInt(MappingQuery.Idx._ID.ordinal());
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return mappingId;
    }

    public int searchMappingIdBySchedId(int schedId) {
        String where = MappingTbl.SCHED_ID + " = " + schedId;
        return searchMappingIdCommon(schedId, where);
    }

    public int searchMappingIdByBatteryId(int batteryId) {
        String where = MappingTbl.BATTERY_ID + " = " + batteryId;
        return searchMappingIdCommon(batteryId, where);
    }

    public int searchMappingIdByManualId(int manualId) {
        String where = MappingTbl.MANUAL_ID + " = " + manualId;
        return searchMappingIdCommon(manualId, where);
    }

    private int searchEcoIdCommon(int id, String where) {
        Cursor c = null;
        int ecoId = 0;
        try {
            c = mContext.getContentResolver().query(MappingTbl.CONTENT_URI,
                    MappingQuery.PROJECTION, where, null, null);
            if (c.moveToFirst()) {
                // Mappingテーブルに存在したのでMappingの該当idを取得
                ecoId = c.getInt(MappingQuery.Idx.ECO_ID.ordinal());
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return ecoId;
    }

    public int searchEcoIdBySchedId(int schedId) {
        String where = MappingTbl.SCHED_ID + " = " + schedId;
        return searchEcoIdCommon(schedId, where);
    }

    public int searchEcoIdByBatteryId(int batteryId) {
        String where = MappingTbl.BATTERY_ID + " = " + batteryId;
        return searchEcoIdCommon(batteryId, where);
    }

    public int searchEcoIdByManualId(int manualId) {
        String where = MappingTbl.MANUAL_ID + " = " + manualId;
        return searchEcoIdCommon(manualId, where);
    }

}
