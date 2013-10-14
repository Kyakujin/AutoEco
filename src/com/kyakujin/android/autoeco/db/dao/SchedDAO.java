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

import java.util.ArrayList;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoDBOpenHelperV1;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

/**
 * Data Access Object for Sched Table.
 */
public class SchedDAO {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private SQLiteDatabase db;
    int mDbVersion;
    Context mContext;
    AutoEcoDBOpenHelperV1 mV1Helper;

    public SchedDAO(SQLiteDatabase db, int mDbVersion) {
        super();
        this.db = db;
        this.mDbVersion = mDbVersion;
    }

    public SchedDAO(SQLiteDatabase db, Context context, int mDbVersion) {
        super();
        this.db = db;
        this.mContext = context;
        this.mDbVersion = mDbVersion;
    }

    public SchedDAO(Context context) {
        super();
        mContext = context;
    }

    private SchedModel createDefaultSchedModel() {
        SchedModel model = new SchedModel();
        model.setEnabled(true);
        model.setHour(0);
        model.setMinute(0);
        model.setHour_minute_string("0:00");
        model.setPattern(Conf.DEFAULT_REPEAT_PATTERN);
        model.setReserve(0);
        return model;
    }

    private ContentValues createDefaultContentValues() {
        SchedModel model = createDefaultSchedModel();
        ContentValues val = new ContentValues();
        val.put(SchedTbl.ENABLED, model.getEnabled());
        val.put(SchedTbl.HOUR, model.getHour());
        val.put(SchedTbl.MINUTE, model.getMinute());
        val.put(SchedTbl.HOUR_MINUTE_STRING, model.getHour_minute_string());
        val.put(SchedTbl.PATTERN, model.getPattern());
        return val;
    }

    public void insertSched(SQLiteDatabase db, SchedModel data) {
        String query;
        int enabled = data.getEnabled() == true ? 1 : 0;
        if (mDbVersion == 1) {
            query = "insert into sched(enabled, hourofday, minute, hour_minute_string, pattern) values ("
                    +
                    "'" + enabled + "','" + data.getHour() + "','" + data.getMinute() +
                    "','" + data.getHour_minute_string() + "','" + data.getPattern() + "')";
        } else {
            query = "insert into sched(enabled, hourofday, minute, hour_minute_string, pattern, reserve) values ("
                    +
                    "'" + enabled + "','" + data.getHour() + "','" + data.getMinute() +
                    "','" + data.getHour_minute_string() + "','" + data.getPattern() +
                    "','" + data.getReserve() + "')";
        }
        db.execSQL(query);
    }

    public Uri insertSched(SchedModel model) {
        ContentValues val = createDefaultContentValues();
        val.put(SchedTbl.ENABLED, model.getEnabled() == true ? 1 : 0);
        val.put(SchedTbl.PATTERN, model.getPattern());
        val.put(SchedTbl.HOUR, model.getHour());
        val.put(SchedTbl.MINUTE, model.getMinute());
        val.put(SchedTbl.HOUR_MINUTE_STRING, model.getHour_minute_string());
        return mContext.getContentResolver().insert(SchedTbl.CONTENT_URI, val);
    }

    public Uri insertDefaultSched() {
        return insertSched(createDefaultSchedModel());
    }

    private ArrayList<SchedModel> execRawQuery(String query, String[] args) {
        ArrayList<SchedModel> scheds = new ArrayList<SchedModel>();
        Cursor cursor = db.rawQuery(query, args);
        try {
            while (cursor.moveToNext()) {
                SchedModel sched = readToSchedModel(cursor, mDbVersion);
                scheds.add(sched);
            }
            return scheds;
        } finally {
            cursor.close();
        }
    }

    public ArrayList<SchedModel> selectAllScheds() {
        return execRawQuery("select * from sched", null);
    }

    public ArrayList<SchedModel> searchSchedFromTime(String time) {
        return execRawQuery("select * from sched where hour_minute_string = ?", new String[] {
                time
        });
    }

    public ArrayList<SchedModel> searchSchedByRawQuery(String sql, String[] args) {
        return execRawQuery(sql, args);
    }

    public void updateSchedByRawQuery(String sql, String[] args) {
        execRawQuery(sql, args);
    }

    public void deleteSchedByRawQuery(String sql, String[] args) {
        execRawQuery(sql, args);
    }

    public int countSchedFromId(int id) {
        mV1Helper = new AutoEcoDBOpenHelperV1(mContext);
        SQLiteDatabase db = mV1Helper.getWritableDatabase();
        Cursor c = db.rawQuery("select count(_id) from sched where _id='" + id + "'", null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        Logger.d(TAG, "count sched = " + count);
        db.close();
        return count;
    }

    public int countSchedFromTime(int hour, int minute) {
        mV1Helper = new AutoEcoDBOpenHelperV1(mContext);
        SQLiteDatabase db = mV1Helper.getWritableDatabase();
        String sql = "select * from sched where " + SchedTbl.HOUR + "='" + hour + "' and "
                + SchedTbl.MINUTE + " = '" + minute + "'";
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(sql, null);
            if (c != null)
                count = c.getCount();
            Logger.d(TAG, hour + ":" + minute + " count sched = " + count);
            return count;

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return -1;
    }

    public void updatePattern(SchedModel model) {
        ContentValues values = new ContentValues();
        values.put(SchedTbl.PATTERN, model.getPattern());
        mContext.getContentResolver().update(Uri.withAppendedPath(SchedTbl.CONTENT_URI,
                String.valueOf(model.getId())), values, null, null);

    }

    public void updateTime(SchedModel model) {
        ContentValues values = new ContentValues();
        values.put(SchedTbl.HOUR, model.getHour());
        values.put(SchedTbl.MINUTE, model.getMinute());
        values.put(SchedTbl.HOUR_MINUTE_STRING, model.getHour_minute_string());
        mContext.getContentResolver().update(Uri.withAppendedPath(SchedTbl.CONTENT_URI,
                String.valueOf(model.getId())), values, null, null);
    }

    public void updateEnabled(int schedId, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(SchedTbl.ENABLED, enabled ? 1 : 0);
        mContext.getContentResolver().update(Uri.withAppendedPath(SchedTbl.CONTENT_URI,
                String.valueOf(schedId)), values, null, null);
        Logger.d(TAG, "enabled=" + enabled);
    }

    public void deleteSchedById(int schedId) {
        mContext.getContentResolver().delete(Uri.withAppendedPath(SchedTbl.CONTENT_URI,
                String.valueOf(schedId)), null, null);
    }

    public SchedModel readToSchedModelById(int id) {
        SchedModel model = new SchedModel();

        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(Uri.withAppendedPath(SchedTbl.CONTENT_URI,
                    String.valueOf(id)), SchedQuery.PROJECTION, null, null, null);

            if (c.moveToFirst()) {
                model.setId(id);
                model.setPattern(c.getInt(c.getColumnIndex(SchedTbl.PATTERN)));
                model.setHour(c.getInt(c.getColumnIndex(SchedTbl.HOUR)));
                model.setMinute(c.getInt(c.getColumnIndex(SchedTbl.MINUTE)));
                model.setHour_minute_string(c.getString(c
                        .getColumnIndex(SchedTbl.HOUR_MINUTE_STRING)));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return model;
    }

    protected SchedModel readToSchedModel(Cursor cursor, int version) {
        int colIndex = 0;
        SchedModel sched = new SchedModel();
        sched.setId(cursor.getInt(colIndex++));
        sched.setEnabled(cursor.getInt(colIndex++) == 1 ? true : false);
        sched.setHour(cursor.getInt(colIndex++));
        sched.setMinute(cursor.getInt(colIndex++));
        sched.setHour_minute_string(cursor.getString(colIndex++));
        sched.setPattern(cursor.getInt(colIndex++));
        if (version > 1) {
            sched.setReserve(cursor.getInt(colIndex++));
        }
        return sched;
    }
}
