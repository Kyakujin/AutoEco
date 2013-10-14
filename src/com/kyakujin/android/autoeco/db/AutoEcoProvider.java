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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.ManualTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.MappingTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;
import com.kyakujin.android.autoeco.db.AutoEcoDBOpenHelperV1.Tables;

/**
 * Provider that stores {@link AutoEcoContract} data.
 */
public class AutoEcoProvider extends ContentProvider {

    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private AutoEcoDBOpenHelperV1 mOpenHelper;
    
    private static final int ECO = 100;
    private static final int ECO_ID = 101;
    private static final int SCHED = 200;
    private static final int SCHED_ID = 201;
    private static final int BATTERY = 300;
    private static final int BATTERY_ID = 301;
    private static final int MANUAL = 400;
    private static final int MANUAL_ID = 401;
    private static final int MAPPING = 1000;
    private static final int MAPPING_ID = 1001;

    private static final UriMatcher sUriMatcher;
    static {
        final String authority = AutoEcoContract.CONTENT_AUTHORITY;
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(authority, Tables.ECO, ECO);
        sUriMatcher.addURI(authority, Tables.ECO + "/#", ECO_ID);
        sUriMatcher.addURI(authority, Tables.SCHED, SCHED);
        sUriMatcher.addURI(authority, Tables.SCHED + "/#", SCHED_ID);
        sUriMatcher.addURI(authority, Tables.BATTERY, BATTERY);
        sUriMatcher.addURI(authority, Tables.BATTERY + "/#", BATTERY_ID);        
        sUriMatcher.addURI(authority, Tables.MANUAL, MANUAL);
        sUriMatcher.addURI(authority, Tables.MANUAL + "/#", MANUAL_ID);         
        sUriMatcher.addURI(authority, Tables.MAPPING, MAPPING);
        sUriMatcher.addURI(authority, Tables.MAPPING + "/#", MAPPING_ID);
    }

    private void setOpenHelper (boolean isTest) {
        if(mOpenHelper == null) {
            Context context = null;
            if(isTest) {                
                //context  = new RenamingDelegatingContext(getContext(), "test_");
                Logger.d(TAG, "isTest is ON --> RenamingDeletgatingContext");
            } else {
                context = getContext();
                Logger.d(TAG, "isTest is OFF --> Original Context");                
            }
            mOpenHelper = new AutoEcoDBOpenHelperV1(context);
        }
    }
    
    @Override
    public boolean onCreate() {
        //mOpenHelper = new AutoEcoDBOpenHelperV1(getContext());
        setOpenHelper(Conf.isTest);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ECO:
                return EcoTbl.CONTENT_TYPE;
            case ECO_ID:
                return EcoTbl.CONTENT_ITEM_TYPE;
            case SCHED:
                return SchedTbl.CONTENT_TYPE;
            case SCHED_ID:
                return SchedTbl.CONTENT_ITEM_TYPE;
            case BATTERY:
                return BatteryTbl.CONTENT_TYPE;
            case BATTERY_ID:
                return BatteryTbl.CONTENT_ITEM_TYPE;
            case MANUAL:
                return ManualTbl.CONTENT_TYPE;
            case MANUAL_ID:
                return ManualTbl.CONTENT_ITEM_TYPE;
            case MAPPING:
                return MappingTbl.CONTENT_TYPE;
            case MAPPING_ID:
                return MappingTbl.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        //setOpenHelper(Conf.isTest);
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case ECO:
                qb.setTables(Tables.ECO);
                // sortOrder = Tags.DEFAULT_SORT;
                break;
            case ECO_ID:
                qb.setTables(Tables.ECO);
                qb.appendWhere(EcoTbl._ID + "=" + uri.getLastPathSegment());
                break;
            case SCHED:
                if (selection != null) {
                 // TODO 次回改善箇所
                    // 無理やりここを通すための苦肉の策
                    // 以下の条件式を満たすためには、呼び出し元のwhere句で「WHERE」を指定する
                    if (selection.contains("WHERE")) {
                        String sql = "SELECT " + Tables.SCHED + "." + SchedTbl._ID + ", "
                                + Tables.SCHED + "." + SchedTbl.ENABLED + ", "
                                + Tables.SCHED + "." + SchedTbl.HOUR_MINUTE_STRING + ", "
                                + Tables.ECO + "." + EcoTbl.NAME
                                + " FROM " + Tables.SCHED
                                + " JOIN " + Tables.MAPPING
                                + " ON " + Tables.MAPPING + "." + MappingTbl.SCHED_ID + " = "
                                + Tables.SCHED + "." + SchedTbl._ID
                                + " JOIN " + Tables.ECO
                                + " ON " + Tables.MAPPING + "." + MappingTbl.ECO_ID + " = "
                                + Tables.ECO + "." + EcoTbl._ID + " "
                                + selection + ";";

                        Cursor c = db.rawQuery(sql, selectionArgs);
                        c.setNotificationUri(getContext().getContentResolver(), uri);
                        return c;
                    }
                } else {
                    sortOrder = SchedTbl.DEFAULT_SORT;
                    qb.setTables(Tables.SCHED);
                }

                break;
            case SCHED_ID:
                qb.setTables(Tables.SCHED);
                qb.appendWhere(SchedTbl._ID + "=" + uri.getLastPathSegment());
                break;
            case BATTERY:
                qb.setTables(Tables.BATTERY);
                // sortOrder = Tags.DEFAULT_SORT;
                break;
            case BATTERY_ID:
                qb.setTables(Tables.BATTERY);
                qb.appendWhere(BatteryTbl._ID + "=" + uri.getLastPathSegment());
                break;
            case MANUAL:
                qb.setTables(Tables.MANUAL);
                // sortOrder = Tags.DEFAULT_SORT;
                break;
            case MANUAL_ID:
                qb.setTables(Tables.MANUAL);
                qb.appendWhere(ManualTbl._ID + "=" + uri.getLastPathSegment());
                break;
            case MAPPING:
                qb.setTables(Tables.MAPPING);
                // sortOrder = Tags.DEFAULT_SORT;
                break;
            case MAPPING_ID:
                qb.setTables(Tables.MAPPING);
                qb.appendWhere(MappingTbl._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        //setOpenHelper(Conf.isTest);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName;
        String hackString;
        Uri contentUri;
        switch (sUriMatcher.match(uri)) {
            case ECO:
                tableName = Tables.ECO;
                hackString = EcoTbl.NAME;
                contentUri = EcoTbl.CONTENT_URI;
                break;
            case SCHED:
                tableName = Tables.SCHED;
                hackString = SchedTbl.HOUR;
                contentUri = SchedTbl.CONTENT_URI;
                break;
            case BATTERY:
                tableName = Tables.BATTERY;
                hackString = BatteryTbl.THRESHOLD;
                contentUri = BatteryTbl.CONTENT_URI;
                break;
            case MANUAL:
                tableName = Tables.MANUAL;
                hackString = ManualTbl.NAME;
                contentUri = ManualTbl.CONTENT_URI;
                break;
            case MAPPING:
                tableName = Tables.MAPPING;
                hackString = MappingTbl.ECO_ID;
                contentUri = MappingTbl.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // SDKバージョンが異なると、insertWithOnConflict()でCONFLICT_IGNOREを指定した場合の
        // 一意性違反発生時の戻り値が以下のように異なる。
        // [SDKバージョン → rowId]
        // Android 2.3.4以下 → 0
        // Android 3.1以上 → -1
        // したがって以下のような処理を記述した。
        long rowId = 0;
        try {
            rowId = db.insertWithOnConflict(tableName, hackString,
                    values, SQLiteDatabase.CONFLICT_IGNORE);
            Logger.d(TAG, "rowId=" + rowId);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to insert: " + rowId, e);
            return null;
        }

        if (rowId > 0) {
            Uri insertUri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(insertUri, null);
            return insertUri;
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {

        //setOpenHelper(Conf.isTest);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName;
        String whereClause = where;

        switch (sUriMatcher.match(uri)) {
            case ECO:
                tableName = Tables.ECO;
                break;
            case ECO_ID:
                tableName = Tables.ECO;
                whereClause = EcoTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case SCHED:
                tableName = Tables.SCHED;
                break;
            case SCHED_ID:
                tableName = Tables.SCHED;
                whereClause = SchedTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case BATTERY:
                tableName = Tables.BATTERY;
                break;
            case BATTERY_ID:
                tableName = Tables.BATTERY;
                whereClause = BatteryTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case MANUAL:
                tableName = Tables.MANUAL;
                break;
            case MANUAL_ID:
                tableName = Tables.MANUAL;
                whereClause = ManualTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case MAPPING:
                tableName = Tables.MAPPING;
                break;
            case MAPPING_ID:
                tableName = Tables.MAPPING;
                whereClause = MappingTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        int count = db.updateWithOnConflict(tableName, values, whereClause, whereArgs,
                SQLiteDatabase.CONFLICT_REPLACE);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        //setOpenHelper(Conf.isTest);        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName;
        String whereClause = where;
        switch (sUriMatcher.match(uri)) {
            case ECO:
                tableName = Tables.ECO;
                break;
            case ECO_ID:
                tableName = Tables.ECO;
                whereClause = EcoTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case SCHED:
                tableName = Tables.SCHED;
                break;
            case SCHED_ID:
                tableName = Tables.SCHED;
                whereClause = SchedTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case BATTERY:
                tableName = Tables.BATTERY;
                break;
            case BATTERY_ID:
                tableName = Tables.BATTERY;
                whereClause = BatteryTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case MANUAL:
                tableName = Tables.MANUAL;
                break;
            case MANUAL_ID:
                tableName = Tables.MANUAL;
                whereClause = BatteryTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            case MAPPING:
                tableName = Tables.MAPPING;
                break;
            case MAPPING_ID:
                tableName = Tables.MAPPING;
                whereClause = MappingTbl._ID + " == " + uri.getLastPathSegment()
                        + (!TextUtils.isEmpty(where) ? "AND (" + where + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);

        }

        int count = db.delete(tableName, whereClause, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    // public void deleteDatabase() {
    // mOpenHelper.close();
    // Context context = getContext();
    // AutoEcoDBOpenHelperV1.deleteDatabase(context);
    // mOpenHelper = new AutoEcoDBOpenHelperV1(getContext());
    // }
}
