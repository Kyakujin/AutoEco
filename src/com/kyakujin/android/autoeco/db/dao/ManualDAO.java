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
import com.kyakujin.android.autoeco.db.AutoEcoContract.ManualTbl;

/**
 * Data Access Object for Manual Table.
 */
public class ManualDAO {
    int mDbVersion;
    Context mContext;
    AutoEcoDBOpenHelperV1 mV1Helper;

    public ManualDAO(Context context) {
        super();
        mContext = context;
    }

    private ManualModel createDefaultManualModel() {
        ManualModel model = new ManualModel();
        model.setName("none");
        return model;
    }

    private ContentValues createDefaultContentValues() {
        ManualModel model = createDefaultManualModel();
        ContentValues val = new ContentValues();
        val.put(ManualTbl.NAME, model.getName());
        return val;
    }

    public Uri insertDefaultManual() {
        return insertManual(createDefaultManualModel());
    }

    public void insertManual(SQLiteDatabase db, ManualModel data) {
        String query;
        query = "insert into manual(name) values (" + "'" + data.getName() + "')";
        db.execSQL(query);
    }

    public Uri insertManual(ManualModel model) {
        ContentValues val = createDefaultContentValues();
        val.put(ManualTbl.NAME, model.getName());
        return mContext.getContentResolver().insert(ManualTbl.CONTENT_URI, val);
    }

    public void updateName(ManualModel model) {
        ContentValues values = new ContentValues();
        values.put(ManualTbl.NAME, model.getName());
        mContext.getContentResolver().update(Uri.withAppendedPath(ManualTbl.CONTENT_URI,
                String.valueOf(model.getId())), values, null, null);
    }
}
