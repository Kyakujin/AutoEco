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
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Conf.EcoExecSwitch;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;
import com.kyakujin.android.autoeco.db.dao.SchedModel;
import com.kyakujin.android.autoeco.eco.EcoThread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

/**
 * アラーム受信時処理クラス
 * {@link EcoThread}を起動する。
 */
public class SchedNotificationReciever extends BroadcastReceiver {

    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();
    private int mSchedId;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Logger.d(TAG, "action: " + intent.getAction());
        
        // アラームの再設定
        SchedModel model = new SchedModel();
        SchedAlarmManager am = new SchedAlarmManager(context);
        mSchedId = intent.getExtras().getInt(Conf.SHARED_ALARM_SCHEDID);
        Logger.d(TAG, "受信したid: " + mSchedId);

        Cursor c = null;
        try {
            c = context.getContentResolver().query(Uri.withAppendedPath(SchedTbl.CONTENT_URI,
                    String.valueOf(mSchedId)),SchedQuery.PROJECTION, null, null, null);
            while (c.moveToNext()) {
                model.setId(c.getInt(SchedQuery.Idx._ID.ordinal()));
                model.setHour(c.getInt(SchedQuery.Idx.HOUR.ordinal()));
                model.setMinute(c.getInt(SchedQuery.Idx.MINUTE.ordinal()));
                am.addAlarm(model);
                Logger.d(TAG, "アラーム再設定 id: " + mSchedId);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        Logger.d(TAG, "節電実行！ id: " + mSchedId);
        EcoThread thr = new EcoThread(mContext, EcoExecFrom.SCHED, mSchedId, EcoExecSwitch.ECO_ON);
        thr.start();        
    }
}
