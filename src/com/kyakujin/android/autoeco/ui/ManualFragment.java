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
import android.widget.Button;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Conf.EcoExecSwitch;
import com.kyakujin.android.autoeco.db.AutoEcoContract.ManualQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.ManualTbl;
import com.kyakujin.android.autoeco.db.dao.ManualModel;
import com.kyakujin.android.autoeco.eco.EcoThread;

/**
 * マニュアルによる節電設定用の画面フラグメント
 */
public class ManualFragment extends Fragment  implements LoaderCallbacks<Cursor> {
    private final String TAG = Conf.APP_NAME +":" + this.getClass().getSimpleName();
    
    private int mCurrentManualId = 0;
    private LoaderManager mManager;

    private Activity mActivity;
    private ManualModel mModel;
    private Button mDo;
    
    public static ManualFragment newInstance() {
        return new ManualFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manual_setting, container,
                false);
        
        mActivity = getActivity();
        mCurrentManualId = getArguments().getInt(Conf.SHARED_MANUALID);
                
        mModel = new ManualModel();
        mActivity = getActivity();
        mDo = (Button) v.findViewById(R.id.btnDoManually);
        mDo.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                EcoThread thr = new EcoThread(mActivity, EcoExecFrom.MANUAL, mCurrentManualId, EcoExecSwitch.ECO_ON);
                thr.start();                    
                
            }
        }); 
                
        // DBから情報を取得してUIに反映
        mManager = getLoaderManager();
        mManager.restartLoader(ManualQuery.LOADER_ID, null, this);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
        switch (id) {
            case ManualQuery.LOADER_ID:
                return new CursorLoader(getActivity(), Uri.withAppendedPath(ManualTbl.CONTENT_URI,
                        String.valueOf(mCurrentManualId)), ManualQuery.PROJECTION, null, null, null);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ManualQuery.LOADER_ID:
                getManualFromDB(data);
                break;
            default:
                break;
        }

        
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
    
    private void getManualFromDB(Cursor c) {
        if (c != null && c.moveToFirst()) {
            mModel.setId(c.getInt(ManualQuery.Idx._ID.ordinal()));
            mModel.setName(c.getString(ManualQuery.Idx.NAME.ordinal()));
        }
    }

}
