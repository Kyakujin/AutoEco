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

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.kyakujin.android.autoeco.BuildConfig;
import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Conf.EcoExecSwitch;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.BatteryTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.ManualQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.ManualTbl;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedTbl;
import com.kyakujin.android.autoeco.db.dao.SchedDAO;
import com.kyakujin.android.autoeco.db.dao.SchedModel;
import com.kyakujin.android.autoeco.eco.EcoThread;
import com.kyakujin.android.autoeco.service.BatteryService;
import com.kyakujin.android.autoeco.service.SchedAlarmManager;


/**
 * 最初に起動されるアクティビティ。<br>
 * アクティビティに表示するための情報をDBから取得してUIへ反映。
 */
public class MainActivity extends FragmentActivity implements OnClickListener,
        LoaderCallbacks<Cursor>, OnItemClickListener {

    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private Activity mActivity;
    private LinearLayout mBattery;
    private Button mAddSched;
    private LinearLayout mManual;
    private CustomTimePickerDialog timePickerDialog;
    private TimeData mTimeData;
    private Uri mCurrentSchedUri;
    private int mBatteryId;
    private LoaderManager mLoaderManager;
    private SchedListAdapter mSchedListAdapter;
    private ListView mSchedListView;
    private TextView mBatteryDisabled;
    private TextView mBatteryAccount;
    private LinearLayout mCurrentBattery;
    private TextView mCurrentBatteryLevel;
    private TextView mCurrentThreshold;
    private ImageButton mAbout;
    private int mSchedCount;
    private static final int MAX_SCHED = 5;

    public static boolean mIsForeground;
    // DBG
    private Button mStart;
    private Button mStop;
    private int mManualId;
    private AdView adView;

    // 現在のバッテリー残量レベルを定期的に取得してViewに表示するための非同期タスク
    private class SetCurrentBatteryTask extends AsyncTask<Void, Void, Integer> {

        TextView mView;

        private SetCurrentBatteryTask(TextView view) {
            super();
            mView = view;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return BatteryService.getCurrentBatteryLevel();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (mView.isShown()) {
                String desc;
                if (result == 0) {
                    desc = getResources().getString(R.string.desc_measureing);
                } else {
                    desc = getResources().getString(R.string.desc_current_batterylevel)
                            + result + "%";
                }
                mView.setText(desc);
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        mAbout = (ImageButton) findViewById(R.id.buttonAbout);
        mAbout.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 13) {
            mAbout.setVisibility(View.VISIBLE);
        } else {
            mAbout.setVisibility(View.INVISIBLE);
        }

        mTimeData = TimeData.getInstance();
        mTimeData.setHour(0);
        mTimeData.setMinute(0);

        mBattery = (LinearLayout) findViewById(R.id.batteryRoot);
        mBattery.setOnClickListener(this);
        mAddSched = (Button) findViewById(R.id.btnAddSched);
        mAddSched.setOnClickListener(this);
        mManual = (LinearLayout) findViewById(R.id.manualRoot);
        mManual.setOnClickListener(this);

        mBatteryDisabled = (TextView) findViewById(R.id.textBatteryDisabledInFrame);
        mBatteryAccount = (TextView) findViewById(R.id.textSetThresholdInFrame);
        mCurrentBattery = (LinearLayout) findViewById(R.id.layoutCurrentBattery);
        mCurrentBatteryLevel = (TextView) findViewById(R.id.textCurrentLevelInFrame);
        mCurrentThreshold = (TextView) findViewById(R.id.textThresholdInFrame);

        mSchedListView = (ListView) findViewById(android.R.id.list);
        mSchedListView.setOnItemClickListener(this);

        mSchedListAdapter = new SchedListAdapter(this,
                R.layout.list_item_sched, null,
                new String[] {
                        SchedTbl.HOUR_MINUTE_STRING,
                },
                new int[] {
                        R.id.textHourMinute,
                },
                0);

        mSchedListView.setAdapter(mSchedListAdapter);
        registerForContextMenu(mSchedListView);

        mIsForeground = true;
        Timer timer = new Timer(false);
        // バッテリー残量の表示を更新するために定期的に実行
        timer.schedule(new TimerTask() {

            public void run() {
                // これをやらないと例外が発生する(Timerの中ではUI部品は通常は触れない)
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (mIsForeground) {
                            Logger.d(TAG, "timertask.");
                            SetCurrentBatteryTask task = new SetCurrentBatteryTask(
                                    mCurrentBatteryLevel);
                            task.execute();
                        }

                    }
                });
            }
        }, 0, 3000);

        mLoaderManager = getSupportLoaderManager();
        mLoaderManager.restartLoader(SchedQuery.LOADER_ID, null, this);
        mLoaderManager.restartLoader(ManualQuery.LOADER_ID, null, this);
        mLoaderManager.restartLoader(BatteryQuery.LOADER_ID, null, this);

        addAD();
    }

    /**
     * AdViewを挿入
     */
    private void addAD() {
        // for ad
        //
        // adView を作成する
        adView = new AdView(this, AdSize.BANNER, Conf.MY_AD_UNIT_ID);
        //
        // 属性 android:id="@+id/mainLayout" が与えられているものとして
        // LinearLayout をルックアップする
        LinearLayout layout = (LinearLayout) findViewById(R.id.admobspace);

        // adView処理 --- ここから
        layout.addView(adView);

        // 一般的なリクエストを行って広告を読み込む
        AdRequest adRequest = new AdRequest();
        if (BuildConfig.DEBUG) {
            // ここから - できればリリース時にコメントアウト
            // エミュレータ
            adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
            // Android端末をテスト
            // "XXXXXX...XX"には、端末の識別IDを指定(指定したIDの端末には広告は表示されない)
            adRequest.addTestDevice("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            // ここまで
        }
        adView.loadAd(adRequest);
        // adView処理 --- ここまで

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;
        switch (item.getItemId()) {
            case R.id.menu_about:
                showAboutDialog();
                return ret;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO サービスと連携してバッテリー残量を表示させたほうがよい
        // aidlの仕組みを利用するなどを次回検討
        mIsForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsForeground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsForeground = false;
    }

    public void setCurrentBattery() {
        if (mCurrentBattery.isShown()) {
            String desc;
            int level = BatteryService.getCurrentBatteryLevel();
            if (level == 0) {
                desc = getResources().getString(R.string.desc_measureing);
            } else {
                desc = getResources().getString(R.string.desc_current_batterylevel)
                        + level + "%";
            }
            mCurrentBatteryLevel.setText(desc);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mBattery) {
            if (mBatteryId == 0) {
                mBatteryId = createBatteryData();
            }
            transitionToBatterySetting(mBatteryId);
        } else if (v == mAddSched) {
            if (mSchedCount >= MAX_SCHED) {
                Toast.makeText(
                        mActivity,
                        getResources().getString(R.string.alert_max_sched_header) + MAX_SCHED
                                + getResources().getString(R.string.alert_max_sched_footer),
                        Toast.LENGTH_LONG).show();
                return;
            }
            setTime();
        } else if (v == mManual) {
            if (mManualId == 0) {
                mManualId = createManualData();
            }
            transitionToManualSetting(mManualId);
        } else if (v == mStart) {
            EcoThread thr = new EcoThread(this, EcoExecFrom.SCHED,
                    Integer.valueOf(mCurrentSchedUri.getLastPathSegment()), EcoExecSwitch.ECO_ON);
            thr.start();

        } else if (v == mStop) {
            EcoThread thr = new EcoThread(this, EcoExecFrom.SCHED,
                    Integer.valueOf(mCurrentSchedUri.getLastPathSegment()), EcoExecSwitch.ECO_OFF);
            thr.start();
        } else if (v == mAbout) {
            showAboutDialog();
        }
    }

    private int createBatteryData() {
        ContentValues cv = new ContentValues();
        cv.put(BatteryTbl.ENABLED, 1);
        cv.put(BatteryTbl.THRESHOLD, 30);
        Uri uri = getContentResolver().insert(BatteryTbl.CONTENT_URI, cv);
        if (uri == null)
            return 0;
        mBatteryId = Integer.valueOf(uri.getLastPathSegment());
        return mBatteryId;
    }

    private int createManualData() {
        ContentValues cv = new ContentValues();
        cv.put(ManualTbl.NAME, Conf.NONE);
        Uri uri = getContentResolver().insert(ManualTbl.CONTENT_URI, cv);
        if (uri == null)
            return 0;
        mManualId = Integer.valueOf(uri.getLastPathSegment());
        return mManualId;
    }

    private void setTime() {
        // CustomTimePickerDialogでの時刻設定時に実行されるコールバックを登録
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                /** DO NOTHING */
            }
        };

        // コンテキストメニューからきた場合はクリックしたリストの時刻を取得してダイアログに設定
        if (mCurrentSchedUri != null) {
            SchedDAO dao = new SchedDAO(mActivity);
            SchedModel model = new SchedModel();
            model = dao
                    .readToSchedModelById(Integer.valueOf(mCurrentSchedUri.getLastPathSegment()));

            if (model != null) {
                mTimeData.setHour(model.getHour());
                mTimeData.setMinute(model.getMinute());
                Logger.d(TAG,
                        "get timedata hour=" + model.getHour() + ", minute=" + model.getMinute());
            }
        }
        timePickerDialog = new CustomTimePickerDialog(this, listener, mTimeData.getHour(),
                mTimeData.getMinute(), true);
        timePickerDialog.setTimeData(mTimeData);

        timePickerDialog.setTitle(getResources().getString(R.string.alert_title_settime));
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
                        model.setHour(hour);
                        model.setMinute(minute);
                        model.setHour_minute_string(String.format("%02d:%02d", hour, minute));
                        SchedDAO dao = new SchedDAO(mActivity);
                        // コンテキストメニューからきた場合
                        if (mCurrentSchedUri != null) {
                            model.setId(Integer.valueOf(mCurrentSchedUri.getLastPathSegment()));
                            dao.updateTime(model);
                            activityRestart();
                            // 新規スケジュールの場合
                        } else {
                            // 既に登録済みであれば
                            if (dao.countSchedFromTime(hour, minute) > 0)
                                return;

                            model.setEnabled(true);
                            model.setPattern(Conf.DEFAULT_REPEAT_PATTERN);
                            Uri uri = dao.insertSched(model);
                            if (uri == null)
                                return;

                            model.setId(Integer.valueOf(uri.getLastPathSegment()));

                            // mSchedListAdapter.notifyDataSetChanged();
                            transitionToSchedSetting(Integer.valueOf(uri.getLastPathSegment()));
                        }
                        SchedAlarmManager am = new SchedAlarmManager(mActivity);
                        am.addAlarm(model);

                        // 初期化
                        mCurrentSchedUri = null;
                        mTimeData.setHour(0);
                        mTimeData.setMinute(0);

                    } // end of onclick
                } // end of listener
                );

        timePickerDialog.show();
    }

    // private void transitionToTestActivity() {
    // Intent i = new Intent(getApplicationContext(), TestActivity.class);
    // startActivity(i);
    // }

    private void transitionToBatterySetting(int id) {
        Intent i = new Intent(getApplicationContext(), BatterySettingActivity.class);
        i.putExtra(Conf.SHARED_BATTERYID, id);
        startActivity(i);
    }

    private void transitionToManualSetting(int id) {
        Intent i = new Intent(getApplicationContext(), ManualSettingActivity.class);
        i.putExtra(Conf.SHARED_MANUALID, id);
        startActivity(i);
    }

    private void transitionToSchedSetting(int id) {
        Intent i = new Intent(getApplicationContext(), SchedSettingActivity.class);
        i.putExtra(Conf.SHARED_SCHEDID, id);
        startActivity(i);
    }

    private void activityRestart() {
        // Intent intent = new Intent();
        // intent.setClass(mActivity, mActivity.getClass());
        // mActivity.startActivity(intent);
        // mActivity.finish();
    }

    public CustomTimePickerDialog testGetDialog() {
        return timePickerDialog;
    }

    public TimePicker testGetTimePicker() {
        return timePickerDialog.testGetTimePicker();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        switch (id) {
            case SchedQuery.LOADER_ID:
                String order = SchedTbl._ID;
                return new CursorLoader(this, SchedTbl.CONTENT_URI,
                        SchedQuery.PROJECTION, null, null, order);
            case BatteryQuery.LOADER_ID:
                return new CursorLoader(this, BatteryTbl.CONTENT_URI,
                        BatteryQuery.PROJECTION, null, null, null);
            case ManualQuery.LOADER_ID:
                return new CursorLoader(this, ManualTbl.CONTENT_URI,
                        ManualQuery.PROJECTION, null, null, null);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SchedQuery.LOADER_ID:

                mSchedCount = data.getCount();

                // リストビューへの反映
                mSchedListAdapter.swapCursor(data);
                // mSchedListAdapter.notifyDataSetChanged();

                break;
            case BatteryQuery.LOADER_ID:
                if (data.moveToFirst()) {
                    mBatteryId = data.getInt(BatteryQuery.Idx._ID.ordinal());
                    mBatteryAccount.setVisibility(View.INVISIBLE);
                    if (data.getInt(BatteryQuery.Idx.ENABLED.ordinal()) == 1) {
                        mBatteryDisabled.setVisibility(View.INVISIBLE);
                        mCurrentBattery.setVisibility(View.VISIBLE);
                        String desc;
                        int level = BatteryService.getCurrentBatteryLevel();
                        if (level == 0) {
                            desc = getResources().getString(R.string.desc_measureing);
                        } else {
                            desc = getResources().getString(R.string.desc_current_batterylevel)
                                    + level + "%";
                        }
                        mCurrentBatteryLevel.setText(desc);
                        mCurrentThreshold.setText(
                                getResources().getString(R.string.desc_threshold)
                                        + data.getInt(BatteryQuery.Idx.THRESHOLD.ordinal()) + "%");
                    } else {
                        mBatteryDisabled.setVisibility(View.VISIBLE);
                        mCurrentBattery.setVisibility(View.INVISIBLE);
                    }
                } else {
                    mBatteryDisabled.setVisibility(View.INVISIBLE);
                    mCurrentBattery.setVisibility(View.INVISIBLE);
                    mBatteryAccount.setVisibility(View.VISIBLE);
                }

                break;
            case ManualQuery.LOADER_ID:
                if (data.moveToFirst()) {
                    mManualId = data.getInt(ManualQuery.Idx._ID.ordinal());
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mSchedListAdapter.swapCursor(null);

    }

    // リストビュークリック
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        transitionToSchedSetting((int) id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad AdapterContextMenuInfo", e);
            return;
        }

        android.view.MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_schedlist, menu);

        menu.setHeaderTitle(getResources().getString(R.string.label_menu));

        Intent intent = new Intent(null, ContentUris.withAppendedId(SchedTbl.CONTENT_URI,
                (int) info.id));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(mActivity, MainActivity.class), null, intent, 0, null);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad AdapterContextMenuInfo", e);
            return false;
        }

        mCurrentSchedUri = ContentUris.withAppendedId(SchedTbl.CONTENT_URI, info.id);
        switch (item.getItemId()) {
            case R.id.context_delete:
                // 削除確認ダイアログを表示
                AlertDialog dlg = new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_title_delete)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(R.string.alert_message_delete_sched)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // アラームマネージャからの削除
                                SchedModel model = new SchedModel();
                                model.setId(Integer.valueOf(mCurrentSchedUri.getLastPathSegment()));
                                SchedAlarmManager am = new SchedAlarmManager(mActivity);
                                am.cancelAlarm(model);

                                // DBからの削除
                                SchedDAO dao = new SchedDAO(mActivity);
                                dao.deleteSchedById(Integer.valueOf(mCurrentSchedUri
                                        .getLastPathSegment()));

                                mCurrentSchedUri = null;

                                // リストビューの再描画
                                // NOTE:現状では何もしなくて正常動作する
                                activityRestart();
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setInverseBackgroundForced(true)
                        .create();
                dlg.show();
                return true;
            case R.id.context_edit:
                setTime();
                // mManager.restartLoader(Query.LOADER_ID, null, this);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Aboutダイアログを表示。
     */
    private void showAboutDialog() {
        PackageManager pm = this.getPackageManager();
        String packageName = this.getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "N/A";
        }

        SpannableStringBuilder aboutBody = new SpannableStringBuilder();

        SpannableString mailAddress = new SpannableString(getString(R.string.mailto));
        mailAddress.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(getString(R.string.description_mailto)));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.description_mail_subject));
                startActivity(intent);
            }
        }, 0, mailAddress.length(), 0);

        aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)));
        aboutBody.append("\n");
        aboutBody.append(mailAddress);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        TextView aboutBodyView = (TextView) layoutInflater.inflate(R.layout.fragment_about_dialog,
                null);
        aboutBodyView.setText(aboutBody);
        aboutBodyView.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.alert_title_about)
                .setView(aboutBodyView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

}
