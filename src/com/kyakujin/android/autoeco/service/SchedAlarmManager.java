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

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.dao.SchedModel;

/**
 * スケジュール機能用アラーム管理(アラーム追加/削除)クラス
 */
public class SchedAlarmManager {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    Context mContext;
    AlarmManager mManager;
    private PendingIntent mAlarmSender;

    public SchedAlarmManager(Context c) {
        this.mContext = c;
        mManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Logger.d(TAG, "初期化完了");
    }

    public void addAlarm(SchedModel model) {

        Intent intent = new Intent(mContext, SchedService.class);

        // 最後に設定したアラームしか機能しない件の回避策
        // 2つ目以降のintentが上書きされてしまうため、回避策としてダミーを設定する
        Logger.d(TAG, "dummy type = "+String.valueOf(model.getId()));
        intent.setType("schedAlarm"+String.valueOf(model.getId()));

        mAlarmSender = PendingIntent.getService(mContext, -1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
         // 現在時刻を取得
        cal.setTimeInMillis(System.currentTimeMillis()); 
//        Logger.d(TAG, "設定【前】時刻:" + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE));
        cal.set(Calendar.HOUR_OF_DAY, model.getHour());
        cal.set(Calendar.MINUTE, model.getMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Logger.d(TAG, "設定【後】時刻:" + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE));
        Logger.d(TAG,
                "現在時刻(システム時間):"
                        + Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        + Calendar.getInstance().get(Calendar.MINUTE));

        // 設定時刻 < 現在時刻 であれば翌日のアラームにする
        // 【例】 設定9:00 < 現在10:00
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DATE, 1);
            Logger.d(TAG, "明日に設定:" + model.getId() + "::" + model.getHour() + ":" + model.getMinute());
        }
        mManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);

        Logger.d(TAG, "アラームセット完了:" + model.getId());
    }
    
    public void cancelAlarm(SchedModel model) {
        Intent intent = new Intent(mContext, SchedService.class);
        intent.setType("schedAlarm"+String.valueOf(model.getId()));
        mAlarmSender = PendingIntent.getService(mContext, -1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mManager.cancel(mAlarmSender);
        Logger.d(TAG, "アラームキャンセル:" + model.getId());        
    }


}
