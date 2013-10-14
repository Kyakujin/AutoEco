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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link AutoEcoProvider}.
 */
public abstract class AutoEcoDBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE ="autoeco.db";
    
    public AutoEcoDBOpenHelper(Context context, int version) {
        super(context, DATABASE, null, version);
    }

    @Override
    public abstract void onCreate(SQLiteDatabase db);
    
    @Override
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    
    public abstract int getVersion();
}
