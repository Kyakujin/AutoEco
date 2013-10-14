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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryTbl;
import com.kyakujin.android.autoeco.db.dao.BatteryDAO;
import com.kyakujin.android.autoeco.db.dao.BatteryModel;
import com.kyakujin.android.autoeco.service.BatteryService;

/**
 * バッテリー連動機能の設定画面フラグメント
 */
public class BatteryFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private int mCurrentBatteryId = 0;
    private LoaderManager mManager;

    private SeekBar mSeekBar;
    private TextView mThreshold;
    private Activity mActivity;
    private BatteryModel mModel;
    private ToggleButton mToggle;

    public static BatteryFragment newInstance() {
        return new BatteryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battery_setting, container,
                false);
        mCurrentBatteryId = getArguments().getInt(Conf.SHARED_BATTERYID);
        mModel = new BatteryModel();
        mActivity = getActivity();
        mThreshold = (TextView) v.findViewById(R.id.textThreshold);
        mSeekBar = (SeekBar) v.findViewById(R.id.seekBattery);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(30);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            // トラッキング開始時にコールされる
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Logger.d(TAG, "onStartTrackingTouch(): " +
                        String.valueOf(seekBar.getProgress()));
                mThreshold.setText(String.valueOf(seekBar.getProgress()).concat("%"));
            }

            // トラッキング中にコールされる
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                Logger.d(TAG, "onProgressChanged(): " +
                        String.valueOf(progress) + ", " + String.valueOf(fromTouch));
                mThreshold.setText(String.valueOf(seekBar.getProgress()).concat("%"));
            }

            // トラッキング終了時にコールされる
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                Logger.v(TAG, "onStopTrackingTouch(): " + String.valueOf(progress));
                mModel.setThreshold(progress);
                mModel.setId(mCurrentBatteryId);
                BatteryDAO dao = new BatteryDAO(mActivity);
                dao.updateThreshold(mModel);
            }
        });

        mToggle = (ToggleButton) v.findViewById(R.id.toggleBattery);
        mToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // トグルキーが変更された際にコールされる
                BatteryDAO dao = new BatteryDAO(mActivity);
                dao.updateEnabled(mCurrentBatteryId, isChecked);
                if (isChecked) {
                    //dispCautionDialog();
                    mActivity.startService(new Intent(mActivity, BatteryService.class));                        
                        
                } else {
                    mActivity.stopService(new Intent(mActivity, BatteryService.class));
                }
                // プリファレンスに登録
                SharedPreferences pref =
                        mActivity.getSharedPreferences(Conf.PREF, Context.MODE_PRIVATE);
                Editor e = pref.edit();
                e.putInt(Conf.PREFKEY_BATTERYSRV_ENABLED, isChecked == true ? 1 : 0);
                e.commit();

            }


        });

        // DBから情報を取得してUIに反映
        mManager = getLoaderManager();
        mManager.restartLoader(BatteryQuery.LOADER_ID, null, this);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
        switch (id) {
            case BatteryQuery.LOADER_ID:
                return new CursorLoader(getActivity(), Uri.withAppendedPath(BatteryTbl.CONTENT_URI,
                        String.valueOf(mCurrentBatteryId)), BatteryQuery.PROJECTION, null, null,
                        null);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case BatteryQuery.LOADER_ID:
                getBatteryFromDB(data);
                break;
            default:
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    private void getBatteryFromDB(Cursor c) {
        if (c != null && c.moveToFirst()) {
            mModel.setId(c.getInt(BatteryQuery.Idx._ID.ordinal()));
            mModel.setEnabled(c.getInt(BatteryQuery.Idx.ENABLED.ordinal()) == 1 ? true : false);
            mModel.setThreshold(c.getInt(BatteryQuery.Idx.THRESHOLD.ordinal()));
            mSeekBar.setProgress(mModel.getThreshold());
            mThreshold.setText(c.getInt(BatteryQuery.Idx.THRESHOLD.ordinal()) + "%");
            mToggle.setChecked(c.getInt(BatteryQuery.Idx.ENABLED.ordinal()) == 1 ? true : false);
        }
    }

    // Below method is not used.    
    boolean mIsEnabledOK; 
    private void dispCautionDialog() {
        String[] items = new String[] {mActivity.getResources().getString(R.string.desc_nextdisp)};
        Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(mActivity.getResources().getString(R.string.desc_battery_setting_enable_caution));
        builder.setTitle(mActivity.getResources().getString(R.string.desc_caution));
        builder.setIcon(mActivity.getResources().getDrawable(android.R.drawable.ic_dialog_alert));
        builder.setPositiveButton("OK", 
                new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIsEnabledOK = true;                        
                    }
                });
        builder.setNeutralButton(mActivity.getResources().getString(R.string.desc_nextdisp), 
                new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIsEnabledOK = true;                        
                    }
                });
        
        builder.create();
        builder.show();
    }

}
