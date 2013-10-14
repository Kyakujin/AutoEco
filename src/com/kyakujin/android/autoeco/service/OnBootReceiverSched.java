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

package com.kyakujin.android.autoeco.service;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;
import com.kyakujin.android.autoeco.db.dao.SchedModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

/**
 * 端末Boot検出時処理のクラス<br>
 * アラームマネージャ({@link SchedAlarmManager})にアラームを設定する。
 */
public class OnBootReceiverSched extends BaseOnBootReceiver {

    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    @Override
    protected void onDeviceBoot(Context context) {
        Logger.d(TAG, "BOOT検出！");
        SchedModel schedModel = new SchedModel();
        SchedAlarmManager am = new SchedAlarmManager(context);

        Cursor c = null;
        try {
            c = context.getContentResolver().query(SchedTbl.CONTENT_URI,
                    SchedQuery.PROJECTION, null, null, null);
            while (c.moveToNext()) {
                schedModel.setId(c.getInt(SchedQuery.Idx._ID.ordinal()));
                schedModel.setHour(c.getInt(SchedQuery.Idx.HOUR.ordinal()));
                schedModel.setMinute(c.getInt(SchedQuery.Idx.MINUTE.ordinal()));
                am.addAlarm(schedModel);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}
