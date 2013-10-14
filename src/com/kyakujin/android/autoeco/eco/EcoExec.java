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

package com.kyakujin.android.autoeco.eco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Conf.Priority;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;
import com.kyakujin.android.autoeco.service.AutoEcoNotificationActivity;

/**
 * 節電実行クラス
 */
/**
 * @author kyakujin
 *
 */
public class EcoExec {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private static EcoExecFrom mFrom;
    private static int mId;
    private static State mState;
    private static Context mContext;
    private static Priority mPriority = Priority.BATTERY;
    private static boolean mBatterySrvEnabled;
    private static String mUpdateTime;
    private static boolean mIsOverThreathold;
    private static boolean mRollbackFlag = false;

    /**
     * State Pattern<br>
     * 節電実行の可否ルールをここで集約
     */
    public enum State {
        ON {
            /* (非 Javadoc)
             * @see com.kyakujin.android.autoeco.eco.EcoExec.State#start()
             */
            @Override
            protected void start() {
                Logger.d(Conf.APP_NAME + this.getClass().getSimpleName(), "start Eco.");
                exec.doEco();
            }

            @Override
            protected void stop() {
                /** DO NOTHING */
                Logger.d(Conf.APP_NAME + this.getClass().getSimpleName(), "stop Eco.");
            }
        },
        OFF {
            @Override
            protected void start() {
                Logger.d(Conf.APP_NAME + this.getClass().getSimpleName(), "start Eco.");
                exec.doEco();
            }

            @Override
            protected void stop() {
                /** DO NOTHING */
                Logger.d(Conf.APP_NAME + this.getClass().getSimpleName(), "DO NOTHING state:OFF");
            }
        },

        ON_BATTERY {
            @Override
            protected void start() {
                switch (mFrom) {
                    case SCHED:
                        // 以下を満たす場合はスキップする(つまり、バッテリーサービスによる節電実行を優先)
                        // ・バッテリーサービスが稼働中
                        // ・プライオリティがバッテリーサービス
                        // ・バッテリーレベルがしきい値を下回っている
                        if (mBatterySrvEnabled && (mPriority == Priority.BATTERY)
                                && !mIsOverThreathold) {

                            // バッテリーサービス実行中の状態にロールバック
                            mRollbackFlag = true;
                            break;
                        }

                        exec.doEco();
                        break;
                    case BATTERY:
                        // 設定に変更がなければ
                        EcoDAO dao = new EcoDAO(mContext);
                        if (dao.getTimeStamp(mId, mFrom).equals(mUpdateTime))
                            break;

                        exec.doEco();
                        break;
                    case MANUAL:
                        exec.doEco();
                        break;
                    default:
                }
            }

            @Override
            protected void stop() {
                /** DO NOTHING */
                Logger.d(Conf.APP_NAME + this.getClass().getSimpleName(), "stop Eco.(BATTERY_ON)");
            }
        };

        protected abstract void start();

        protected abstract void stop();
    }

    /**
     * 実行状態(以下のstate)を設定する。
     * 
     * @param state 実行状態
     *  ON: 実行済み
     *  OFF: 停止済み
     *  ON_BATTERY: バッテリー連動機能から実行済み
     */
    private static void setState(State state) {
        mState = state;

        // プリファレンスに登録
        SharedPreferences pref =
                mContext.getSharedPreferences(Conf.PREF, Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt(Conf.PREFKEY_ECOSTATE, state.ordinal());
        e.commit();

        Logger.d(Conf.APP_NAME + ":EcoExec", "set state=" + state.ordinal());

    }

    /**
     * バッテリー連動機能からの実行済み状態へロールバックする。
     */
    private synchronized void rollbackToBattery() {
        Logger.d(Conf.APP_NAME + ":EcoExec", "ROLLBACK!");
        SharedPreferences pref =
                mContext.getSharedPreferences(Conf.PREF, Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt(Conf.PREFKEY_ECOSTATE, State.ON_BATTERY.ordinal());
        e.putInt(Conf.PREFKEY_LAST_ECOFROM, EcoExecFrom.BATTERY.ordinal());
        EcoDAO dao = new EcoDAO(mContext);
        e.putString(Conf.PREFKEY_UPDATE_TIME, dao.getTimeStamp(1, EcoExecFrom.BATTERY));
        e.commit();
    }

    /**
     * スケジュール、バッテリー連動、マニュアルのどれから実行されたか記録する。
     */
    private synchronized void savePref() {
        // どこからECO実行命令がきたかプリファレンスに登録
        SharedPreferences pref =
                mContext.getSharedPreferences(Conf.PREF, Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt(Conf.PREFKEY_LAST_ECOFROM, mFrom.ordinal());
        Logger.d(Conf.APP_NAME + ":EcoExec", "last executed by shced or battry=" + mFrom.ordinal());

        EcoDAO dao = new EcoDAO(mContext);
        Logger.d(Conf.APP_NAME + ":EcoExec",
                "DB: eco modified time=" + dao.getTimeStamp(mId, mFrom));
        e.putString(Conf.PREFKEY_UPDATE_TIME, dao.getTimeStamp(mId, mFrom));
        e.commit();
    }

    /**
     * 節電実行のキックメソッド。
     * @param id 実行対象のecoId
     */
    public synchronized void startEco(int id) {
        mId = id;
        State state = State.ON;
        switch (mFrom) {
            case SCHED:
            case MANUAL:
                Logger.d(Conf.APP_NAME + ":EcoExec", "MANUAL CLICK!");
                state = State.ON;
                break;
            case BATTERY:
                state = State.ON_BATTERY;
                break;
            default:
                state = State.ON;
        }
        mState.start();
        setState(state);
        savePref();
        if (mRollbackFlag) {
            rollbackToBattery();
            mRollbackFlag = false;
        }
    }

    /**
     * 節電実行を停止状態にする。
     */
    public synchronized void stopEco() {
        mState.stop();
        setState(State.OFF);
        savePref();
    }

    // Singleton Pattern
    private static EcoExec exec = new EcoExec();

    private EcoExec() {
    }

    /**
     * このクラスのインスタンスを返却。
     * @param context アプリケーションコンテキスト
     * @param from どの機能から実行指示がきたか
     * @return exec このクラスのインスタンス
     */
    public synchronized static EcoExec getInstance(Context context, EcoExecFrom from) {
        mFrom = from;
        mContext = context;

        // ECO実行状態を取得
        SharedPreferences pref =
                mContext.getSharedPreferences(Conf.PREF, Context.MODE_PRIVATE);
        Logger.d(
                Conf.APP_NAME + ":EcoExec",
                "getInstance current state="
                        + pref.getInt(Conf.PREFKEY_ECOSTATE, State.ON.ordinal()));
        State[] values = State.values();
        setState(values[pref.getInt(Conf.PREFKEY_ECOSTATE, State.ON.ordinal())]);

        // バッテリーサービス有効化状態を取得
        mBatterySrvEnabled = (pref.getInt(Conf.PREFKEY_BATTERYSRV_ENABLED, 0) == 1 ? true : false);

        // バッテリーレベルがしきい値を上回っているか取得
        mIsOverThreathold = (pref.getInt(Conf.PREFKEY_BATTERY_LEVEL, 0) == 1 ? true : false);

        // 前回節電実行時のアップデート時間を取得
        mUpdateTime = pref.getString(Conf.PREFKEY_UPDATE_TIME, "");
        Logger.d(Conf.APP_NAME + ":EcoExec", "PREF: eco update time=" + mUpdateTime);

        return exec;
    }

    /**
     * 節電を実行する。
     */
    private void doEco() {
        Intent notification = new Intent(mContext, AutoEcoNotificationActivity.class);
        // 画面を起動するためにはこれが必要
        notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notification.putExtra(Conf.SHARED_EXTRA_ID, mId);
        notification.putExtra(Conf.SHARED_EXTRA_FROM, mFrom.ordinal());
        mContext.startActivity(notification);
    }

}
