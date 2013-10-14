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

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.IBinder;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;

/**
 * アラーム受信時に起動するサービス<br>
 * {@link BatteryService}を起動する。
 */
/**
 * @author kyakujin
 *
 */
public class SchedService extends Service {

    private static final String TAG = Conf.APP_NAME + ":SchedService";
    
    @Override
    public void onCreate() {
        Thread thread = new Thread(null, mTask, Conf.THREAD_ECOSCHED_SERVICE);
        thread.start();
        Logger.d(TAG, "スレッド開始");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * DBを走査して該当時刻であればブロードキャスト。<br>
     * ブロードキャスト先は{@link SchedNotificationReciever}
     */
    Runnable mTask = new Runnable() {
        public void run() {

            // 現在時間を取得
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            Logger.d(TAG, "現在時刻:" + hour + minute);

            Cursor c = null;
            try {
                c = getContentResolver().query(SchedTbl.CONTENT_URI,
                        SchedQuery.PROJECTION, null, null, null);
                while (c.moveToNext()) {
                    Logger.d(TAG, "DB走査:" + c.getInt(SchedQuery.Idx.HOUR.ordinal()) + c.getInt(SchedQuery.Idx.MINUTE.ordinal()));
                    if (hour == c.getInt(SchedQuery.Idx.HOUR.ordinal()) && minute == c.getInt(SchedQuery.Idx.MINUTE.ordinal())) {

                        // 本日が対象曜日でなければ処理中断
                        if (!checkWeek(c)) break;
                        // 無効状態であれば処理中断
                        if(c.getInt(SchedQuery.Idx.ENABLED.ordinal()) == 0) break;

                        // eco設定を有効化するためにブロードキャストを投げる
                        Logger.d(TAG, "send broadcast");
                        Intent alarmIntent = new Intent();
                        alarmIntent.putExtra(Conf.SHARED_ALARM_SCHEDID, c.getInt(SchedQuery.Idx._ID.ordinal()));
                        alarmIntent.setAction(Conf.BROADCAST_ACTION); // 独自のメッセージを送信
                        sendBroadcast(alarmIntent);
                        break;
                    }
                }
            } catch(SQLiteException e) {
                e.printStackTrace();
            } finally {            
                if (c != null) {
                    c.close();
                }
            }
            SchedService.this.stopSelf();
            Logger.d(TAG, "サービス停止");
        }
    };

    
    /**
     * @param c SchedTblのカーソルデータ
     * @return 
     *   true:本日がチェックした曜日に該当<br>
     *   false:本日がチェックした曜日ではない
     */
    private boolean checkWeek(Cursor c) {
        int pattern = c.getInt(SchedQuery.Idx.PATTERN.ordinal());
        int checkFlag = 0;
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                checkFlag = Conf.BIT_MON;
                break;
            case Calendar.TUESDAY:
                checkFlag = Conf.BIT_TUE;
                break;
            case Calendar.WEDNESDAY:
                checkFlag = Conf.BIT_WED;
                break;
            case Calendar.THURSDAY:
                checkFlag = Conf.BIT_THU;
                break;
            case Calendar.FRIDAY:
                checkFlag = Conf.BIT_FRI;
                break;
            case Calendar.SATURDAY:
                checkFlag = Conf.BIT_SAT;
                break;
            case Calendar.SUNDAY:
                checkFlag = Conf.BIT_SUN;
                break;
            default:
        }

        if ((pattern & checkFlag) != 0) {
            Logger.d(TAG, "hit week! id=" + c.getInt(SchedQuery.Idx._ID.ordinal()));
            return true;
        } else {
            Logger.d(TAG, "not week... id=" + c.getInt(SchedQuery.Idx._ID.ordinal()));
            return false;
        }
    }

    
}
