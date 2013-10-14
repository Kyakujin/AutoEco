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
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryTbl;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

/**
 * 端末Boot検出時処理のクラス<br>
 * {@link BatteryService}を起動する。
 */
public class OnBootReceiverBattery extends BaseOnBootReceiver {

    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    @Override
    protected void onDeviceBoot(Context context) {
        Logger.d(TAG, "BOOT検出 in Battery！");

        Cursor c = null;
        try {
            String where = BatteryTbl.ENABLED + "=1";
            c = context.getContentResolver().query(BatteryTbl.CONTENT_URI,
                    BatteryQuery.PROJECTION, where, null, null);
            if (c.getCount() >= 1) {
                context.startService(new Intent(context, BatteryService.class));
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
