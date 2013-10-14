package com.kyakujin.android.autoeco.eco;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Logger;

// TODO:Caution:Android2.3以降では使えないらしい
public class GpsProc extends EcoProc {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    public GpsProc(AbstractEco eco) {
        super(eco);
    }

    @Override
    protected boolean isImplemented() {
        return true;
    }

    @Override
    protected boolean isEnabled() {
        return true;
        // EcoDAO dao = new EcoDAO(getContext());
        // return dao.isEcoEnabledById(getId(), getFrom(), EcoTbl.SYNC_ENABLED);
    }

    @Override
    protected void turnOn() {
        Logger.d(TAG, "Do GPS ON :id=" + getId());
        String provider = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps"))
        // if gps is disabled
        {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getContext().sendBroadcast(poke);
        }
    }

    @Override
    protected void turnOff() {
        Logger.d(TAG, "Do GPS OFF");
        String provider = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps"))
        { // if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getContext().sendBroadcast(poke);
        }
    }

    @Override
    protected Context getContext() {
        return eco.getContext();
    }

    @Override
    protected int getId() {
        return eco.getId();
    }

    @Override
    protected EcoExecFrom getFrom() {
        return eco.getFrom();
    }

}
