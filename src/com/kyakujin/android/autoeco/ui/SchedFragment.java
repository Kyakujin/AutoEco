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

package com.kyakujin.android.autoeco.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kyakujin.android.autoeco.CheckBoxUtils;
import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;
import com.kyakujin.android.autoeco.db.dao.SchedDAO;
import com.kyakujin.android.autoeco.db.dao.SchedModel;
import com.kyakujin.android.autoeco.service.SchedAlarmManager;

/**
 * スケジュール設定画面フラグメント
 */
public class SchedFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    class CheckBoxControl extends CheckBoxUtils {
        @Override
        public void function(CheckBox v) {
            // BinaryStringをIngegerに
            switch (v.getId()) {
            // 曜日に関しては、排他的論理和でbitのON/OFFを制御する
            // 「0月火水木金土日」の並びで、上位1bitは使用しない。
            // カッコ()内は10進数表記
                case R.id.chkBoxMon:
                    // 01000000(64)
                    mModel.setPattern(mModel.getPattern() ^ Conf.BIT_MON);
                    break;
                case R.id.chkBoxTue:
                    // 00100000(32)
                    mModel.setPattern(mModel.getPattern() ^ Conf.BIT_TUE);
                    break;
                case R.id.chkBoxWed:
                    // 00010000(16)
                    mModel.setPattern(mModel.getPattern() ^ Conf.BIT_WED);
                    break;
                case R.id.chkBoxThu:
                    // 00001000(8)
                    mModel.setPattern(mModel.getPattern() ^ Conf.BIT_THU);
                    break;
                case R.id.chkBoxFri:
                    // 00000100(4)
                    mModel.setPattern(mModel.getPattern() ^ Conf.BIT_FRI);
                    break;
                case R.id.chkBoxSat:
                    // 00000010(2)
                    mModel.setPattern(mModel.getPattern() ^ Conf.BIT_SAT);
                    break;
                case R.id.chkBoxSun:
                    // 00000001(1)
                    mModel.setPattern(mModel.getPattern() ^ Conf.BIT_SUN);
                    break;
                default:
                    return;
            }
            SchedDAO dao = new SchedDAO(mActivity);
            mModel.setId(mCurrentSchedId);
            dao.updatePattern(mModel);
        }
    }

    private int mCurrentSchedId;
    private Activity mActivity;
    private LoaderManager mManager;
    private SchedModel mModel;

    private TextView mTime;
    private CheckBox mMon;
    private CheckBox mTue;
    private CheckBox mWed;
    private CheckBox mThu;
    private CheckBox mFri;
    private CheckBox mSat;
    private CheckBox mSun;

    private CustomTimePickerDialog timePickerDialog;
    private TimeData mTimeData;

    public static SchedFragment newInstance() {
        return new SchedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sched_setting, container, false);

        mTimeData = TimeData.getInstance();
        mActivity = getActivity();
        mCurrentSchedId = getArguments().getInt(Conf.SHARED_SCHEDID);
        mModel = new SchedModel();

        mTime = (TextView) v.findViewById(R.id.textTime);
        mTime.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setTime();
            }
        });

        mMon = (CheckBox) v.findViewById(R.id.chkBoxMon);
        mTue = (CheckBox) v.findViewById(R.id.chkBoxTue);
        mWed = (CheckBox) v.findViewById(R.id.chkBoxWed);
        mThu = (CheckBox) v.findViewById(R.id.chkBoxThu);
        mFri = (CheckBox) v.findViewById(R.id.chkBoxFri);
        mSat = (CheckBox) v.findViewById(R.id.chkBoxSat);
        mSun = (CheckBox) v.findViewById(R.id.chkBoxSun);
        // チェックボックスのリスナーを登録
        CheckBoxControl cbc = new CheckBoxControl();
        cbc.addCheckBox(mMon);
        cbc.addCheckBox(mTue);
        cbc.addCheckBox(mWed);
        cbc.addCheckBox(mThu);
        cbc.addCheckBox(mFri);
        cbc.addCheckBox(mSat);
        cbc.addCheckBox(mSun);
        cbc.setCheckBoxListenerControl();

        // DBから情報を取得してUIに反映
        mManager = getLoaderManager();
        mManager.restartLoader(SchedQuery.LOADER_ID, null, this);

        return v;
    }

    private void setTime() {
        // CustomTimePickerDialogでの時刻設定時に実行されるコールバックを登録
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                /** DO NOTHING */
            }
        };

        if (timePickerDialog == null) {
            SchedDAO dao = new SchedDAO(mActivity);
            SchedModel model = new SchedModel();
            model = dao.readToSchedModelById(mCurrentSchedId);
            if (model != null) {
                mTimeData.setHour(model.getHour());
                mTimeData.setMinute(model.getMinute());
                Logger.d(TAG,
                        "get timedata hour=" + model.getHour() + ", minute=" + model.getMinute());
            }

            timePickerDialog = new CustomTimePickerDialog(mActivity, listener, mTimeData.getHour(),
                    mTimeData.getMinute(), true);
            timePickerDialog.setTimeData(mTimeData);
        }

        timePickerDialog.setTitle(mActivity.getResources().getString(R.string.alert_title_settime));
        // ボタンの設定
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    } // end of onclick
                } // end of listener
                );

        timePickerDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // ボタンがクリックされた時の動作
                        if (mTimeData == null)
                            return;

                        int hour = mTimeData.getHour();
                        int minute = mTimeData.getMinute();
                        Logger.v("Time", String.format("%02d:%02d", hour, minute));

                        SchedModel model = new SchedModel();
                        model.setId(mCurrentSchedId);
                        model.setHour(hour);
                        model.setMinute(minute);
                        model.setHour_minute_string(String.format("%02d:%02d", hour, minute));
                        SchedDAO dao = new SchedDAO(mActivity);
                        dao.updateTime(model);

                        SchedAlarmManager am = new SchedAlarmManager(mActivity);
                        am.addAlarm(model);
                    } // end of onclick
                } // end of listener
                );

        timePickerDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
        switch (id) {
            case SchedQuery.LOADER_ID:
                return new CursorLoader(getActivity(), Uri.withAppendedPath(SchedTbl.CONTENT_URI,
                        String.valueOf(mCurrentSchedId)),
                        SchedQuery.PROJECTION, null, null, null);

            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SchedQuery.LOADER_ID:
                getSchedFromDB(data);
                break;
            default:
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SchedQuery.LOADER_ID:
                // mTagListAdapter.swapCursor(null);
                break;
            default:
                return;
        }

    }

    public void setSchedId(int id) {
        mCurrentSchedId = id;
    }

    private void getSchedFromDB(Cursor c) {
        if (c != null && c.moveToFirst()) {

            mModel.setId(c.getInt(SchedQuery.Idx._ID.ordinal()));
            mModel.setEnabled(c.getInt(SchedQuery.Idx.ENABLED.ordinal()) == 1 ? true : false);
            mModel.setHour(c.getInt(SchedQuery.Idx.HOUR.ordinal()));
            mModel.setMinute(c.getInt(SchedQuery.Idx.MINUTE.ordinal()));
            mModel.setHour_minute_string(c.getString(SchedQuery.Idx.HOUR_MINUTE_STRING.ordinal()));
            mModel.setPattern(c.getInt(SchedQuery.Idx.PATTERN.ordinal()));

            mTime.setText(mModel.getHour_minute_string());

            int mask = 128; // まずは先頭1bitにマスク設定
            for (int i = 0; i < 7; i++) {
                // bitを一つづつ右にシフトしながら、各曜日のbitがONか調べる
                mask = mask >>> 1;
                if ((mModel.getPattern() & mask) != 0) {

                    switch (i) {
                        case 0:
                            mMon.setChecked(true);
                            break;
                        case 1:
                            mTue.setChecked(true);
                            break;
                        case 2:
                            mWed.setChecked(true);
                            break;
                        case 3:
                            mThu.setChecked(true);
                            break;
                        case 4:
                            mFri.setChecked(true);
                            break;
                        case 5:
                            mSat.setChecked(true);
                            break;
                        case 6:
                            mSun.setChecked(true);
                            break;
                    }
                }
            }
        }

    }

}
