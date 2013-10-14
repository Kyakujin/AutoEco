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
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.SleepTime;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;
import com.kyakujin.android.autoeco.db.dao.EcoModel;

/**
 * 各種節電機能の設定画面フラグメント
 */
public class EcoFragment extends Fragment  implements OnClickListener, LoaderCallbacks<Cursor>{
    private final String TAG = Conf.APP_NAME +":" + this.getClass().getSimpleName();

    private int mCurrentEcoId;
    private LoaderManager mManager;    
    private Activity mActivity;
    private EcoModel mModel;
    private LinearLayout mLayoutWifi;
    private LinearLayout mLayoutBluetooth;
    private LinearLayout mLayoutRotate;
    private LinearLayout mLayoutSync;
    private LinearLayout mLayoutBrightness;
    private LinearLayout mLayoutSilent;
    private LinearLayout mLayoutSleep;
    private TextView mWifiDesc;
    private TextView mBluetoothDesc;
    private TextView mRotateDesc;
    private TextView mSyncDesc;
    private TextView mBrightnessDesc;
    private TextView mSilentDesc;
    private TextView mSleepDesc;
    private SleepTime mSleepTime = SleepTime.TIME1;
        
    public static EcoFragment newInstance() {
        return new EcoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        mActivity = getActivity();
        mCurrentEcoId = getArguments().getInt(Conf.SHARED_ECOID);    
        mModel = new EcoModel();
        
        View v = inflater.inflate(R.layout.fragment_eco_settings, container, false);
                       
        mLayoutWifi = (LinearLayout) v.findViewById(R.id.layoutWifi);
        mLayoutWifi.setOnClickListener(this);
        mLayoutBluetooth = (LinearLayout) v.findViewById(R.id.layoutBluetooth);
        mLayoutBluetooth.setOnClickListener(this);
        mLayoutRotate = (LinearLayout) v.findViewById(R.id.layoutRotate);
        mLayoutRotate.setOnClickListener(this);
        mLayoutSync = (LinearLayout) v.findViewById(R.id.layoutSync);
        mLayoutSync.setOnClickListener(this);
        mLayoutBrightness = (LinearLayout) v.findViewById(R.id.layoutBrightness);
        mLayoutBrightness.setOnClickListener(this);
        mLayoutSilent = (LinearLayout) v.findViewById(R.id.layoutSilent);
        mLayoutSilent.setOnClickListener(this);
        mLayoutSleep = (LinearLayout) v.findViewById(R.id.layoutSleep);
        mLayoutSleep.setOnClickListener(this);

        mWifiDesc = (TextView) v.findViewById(R.id.textWifiDesc);        
        mBluetoothDesc = (TextView) v.findViewById(R.id.textBluetoothDesc);
        mRotateDesc = (TextView) v.findViewById(R.id.textRotateDesc);
        mSyncDesc = (TextView) v.findViewById(R.id.textSyncDesc);
        mBrightnessDesc = (TextView) v.findViewById(R.id.textBrightnessDesc);
        mSilentDesc = (TextView) v.findViewById(R.id.textSilentDesc);        
        mSleepDesc = (TextView) v.findViewById(R.id.textSleepDesc);        
                
        // DBから情報を取得してUIに反映
        mManager = getLoaderManager();
        mManager.restartLoader(EcoQuery.LOADER_ID, null, this);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
        switch (id) {
            case EcoQuery.LOADER_ID:
                return new CursorLoader(getActivity(), Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                        String.valueOf(mCurrentEcoId)), 
                        EcoQuery.PROJECTION, null, null, null);                

            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case EcoQuery.LOADER_ID:
                fillEco(data);
                break;
            default:
                break;
        }
        
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    
    private void fillEco(Cursor c) {
        EcoDAO dao = new EcoDAO(mActivity);
        mModel = dao.readToEcoModelByCursor(c);
        
        // 詳細Descriptionへの反映
        
        // Wifi Desc
        if(mModel.getWifiEnabled()) {
            mWifiDesc.setText(getResources().getString(R.string.radio_on));
        } else {
            mWifiDesc.setText(getResources().getString(R.string.radio_off));            
        }

        // Bluetooth Desc
        if(mModel.getBluetoothEnabled()) {
            mBluetoothDesc.setText(getResources().getString(R.string.radio_on));
        } else {
            mBluetoothDesc.setText(getResources().getString(R.string.radio_off));            
        }

        // Rotate Desc        
        if(mModel.getRotateEnabled()) {
            mRotateDesc.setText(getResources().getString(R.string.radio_on));
        } else {
            mRotateDesc.setText(getResources().getString(R.string.radio_off));            
        }

        // Sync Desc        
        if(mModel.getSyncEnabled()) {
            mSyncDesc.setText(getResources().getString(R.string.radio_on));
        } else {
            mSyncDesc.setText(getResources().getString(R.string.radio_off));            
        }

        String desc = "";
        // SilentMode Desc
        switch (mModel.getSilentMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                desc = getResources().getString(R.string.radio_normal);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                desc = getResources().getString(R.string.radio_silent);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                desc = getResources().getString(R.string.radio_vibrate);
                break;
            default:
        }
        mSilentDesc.setText(desc);        
        
        // 輝度 Desc           
        if (mModel.getBrightnessAuto()) {
            desc = mActivity.getResources().getString(R.string.label_auto_brightness);
        } else {
            desc = String.valueOf(mModel.getBrightnessValue());
        }
        mBrightnessDesc.setText(desc);
       
        // Sleep Desc
        mSleepTime = Conf.mapSleepTime.get(mModel.getSleepTimeOrdinal());
        switch (mSleepTime) {
            case TIME1:
                mSleepTime = SleepTime.TIME1; 
                mSleepDesc.setText(getResources().getString(R.string.radio_time1));
                break;
            case TIME2:
                mSleepTime = SleepTime.TIME2; 
                mSleepDesc.setText(getResources().getString(R.string.radio_time2));
                break;
            case TIME3:
                mSleepTime = SleepTime.TIME3; 
                mSleepDesc.setText(getResources().getString(R.string.radio_time3));
                break;
            case TIME4:
                mSleepTime = SleepTime.TIME4; 
                mSleepDesc.setText(getResources().getString(R.string.radio_time4));
                break;
            case TIME5:
                mSleepTime = SleepTime.TIME5; 
                mSleepDesc.setText(getResources().getString(R.string.radio_time5));
                break;
            case TIME6:
                mSleepTime = SleepTime.TIME6; 
                mSleepDesc.setText(getResources().getString(R.string.radio_time6));
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putInt(Conf.SHARED_ECOID, mCurrentEcoId);        
        if (v == mLayoutWifi) {
            bundle.putInt(Conf.SHARED_WIFI_ENABLED, mModel.getWifiEnabled() == true ? 1: 0);
            WifiSettingFragment fragment = WifiSettingFragment.newInstance();
            fragment.setArguments(bundle);
            fragment.show(manager, Conf.FRAGTAG_WIFI_SETTING);            
        } else if (v == mLayoutBluetooth) {
            bundle.putInt(Conf.SHARED_BLUETOOTH_ENABLED, mModel.getBluetoothEnabled() == true ? 1: 0);
            BluetoothSettingFragment fragment = BluetoothSettingFragment.newInstance();
            fragment.setArguments(bundle);
            fragment.show(manager, Conf.FRAGTAG_BLUETOOTH_SETTING);            
        } else if (v == mLayoutRotate) {
            bundle.putInt(Conf.SHARED_ROTATE_ENABLED, mModel.getRotateEnabled() == true ? 1: 0);
            RotateSettingFragment fragment = RotateSettingFragment.newInstance();
            fragment.setArguments(bundle);
            fragment.show(manager, Conf.FRAGTAG_ROTATE_SETTING);            
        } else if (v == mLayoutSync) {
            bundle.putInt(Conf.SHARED_SYNC_ENABLED, mModel.getSyncEnabled() == true ? 1: 0);
            SyncSettingFragment fragment = SyncSettingFragment.newInstance();
            fragment.setArguments(bundle);
            fragment.show(manager, Conf.FRAGTAG_SYNC_SETTING);            
        } else if (v == mLayoutBrightness) {
            bundle.putInt(Conf.SHARED_BRIGHTNESSVALUE, mModel.getBrightnessValue());
            bundle.putInt(Conf.SHARED_BRIGHTNESSAUTO, mModel.getBrightnessAuto()== true ? 1: 0);
            BrightnessSettingFragment fragment = BrightnessSettingFragment.newInstance();
            fragment.setArguments(bundle);
            fragment.show(manager, Conf.FRAGTAG_BRIGHTNESS_SETTING);            
        } else if (v == mLayoutSilent) {
            bundle.putInt(Conf.SHARED_SILENTMODE, mModel.getSilentMode());
            SilentModeSettingFragment fragment = SilentModeSettingFragment.newInstance();
            fragment.setArguments(bundle);
            fragment.show(manager, Conf.FRAGTAG_SILENT_SETTING);
        } else if (v == mLayoutSleep) {
            bundle.putInt(Conf.SHARED_SLEEPTIME_ORDINAL, mModel.getSleepTimeOrdinal());
            SleepSettingFragment fragment = SleepSettingFragment.newInstance();
            fragment.setArguments(bundle);
            fragment.show(manager, Conf.FRAGTAG_SLEEP_SETTING);
        }
    }
    
}
