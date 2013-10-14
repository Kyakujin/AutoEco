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
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryTbl;
import com.kyakujin.android.autoeco.eco.EcoThread;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.IBinder;

/**
 * バッテリーレベル更新を捕捉するクラス
 */
public class BatteryService extends Service {

    private final static String TAG = Conf.APP_NAME + ":BatteryService";
    private int mBatteryId;
    private static int mThreshold = 100;
    private Context mContext;
    private static int mLevel = 0;


    public static void setThreshold(int value) {
        mThreshold = value;
        Logger.d(TAG, "しきい値変更:" + mThreshold);
    }

    public BatteryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
        Logger.d(TAG, "バッテリーサービススタート");
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "バッテリーサービスストップ");
        unregisterReceiver(mBroadcastReceiver);        
    }

    public static int getCurrentBatteryLevel() {
        return mLevel;
    }
    
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            mContext = context;

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mLevel = level;

                Logger.d(TAG, "バッテリーレベル:" + level);

                  //
                // DBから設定値を取得
                  //
                Cursor c = null;
                try {
                    String selection = BatteryTbl.ENABLED + "=1";
                    c = context.getContentResolver().query(BatteryTbl.CONTENT_URI,
                            BatteryQuery.PROJECTION, selection, null, null);
                    if (c != null && c.moveToFirst()) {
                        mBatteryId = c.getInt(BatteryQuery.Idx._ID.ordinal());
                        mThreshold = c.getInt(BatteryQuery.Idx.THRESHOLD.ordinal());
                    }
                    if(c == null || c.getCount() <= 0)                        
                        return;
                    
                    if (mBatteryId <= 0)
                    Logger.d(TAG, "mBatteryId取得完了:" + mBatteryId);
                    Logger.d(TAG, "しきい値取得完了:" + mThreshold);

                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }

                // プリファレンスに登録
                SharedPreferences pref =
                        mContext.getSharedPreferences(Conf.PREF, Context.MODE_PRIVATE);
                Editor e = pref.edit();
                
                if (mThreshold >= level) {
                    Logger.d(TAG, "あ、バッテリーしきい値を下回った！");
                    e.putInt(Conf.PREFKEY_BATTERY_LEVEL, Conf.UNDER_THRESHOLD);
                    e.commit();
                    
                    EcoThread thr = new EcoThread(mContext, EcoExecFrom.BATTERY, mBatteryId, EcoExecSwitch.ECO_ON);
                    thr.start();                    
                    
                } else {
                    Logger.d(TAG, "バッテリーしきい値大丈夫...");
                    e.putInt(Conf.PREFKEY_BATTERY_LEVEL, Conf.OVER_THRESHOLD);
                    e.commit();

                    EcoThread thr = new EcoThread(mContext, EcoExecFrom.BATTERY, mBatteryId, EcoExecSwitch.ECO_OFF);
                    thr.start();                                        
                }
            }
        }
    };

}
