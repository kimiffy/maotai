package com.kimiffy.maotai;

import android.app.Application;

/**
 * Description:
 * Created by kimiffy on 2020/12/22.
 */
public class App extends Application {
    private static App myApplication;
    private boolean needBooking=true;//是否使用自动预约
    private boolean supportJD=true;//是否支持京东
    private boolean supportSN=true;//是否支持苏宁
    private boolean supportGM=true;//是否支持国美
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

    }

    public static App getMyApplication() {
        return myApplication;
    }

    public boolean isNeedBooking() {
        return needBooking;
    }

    public void setNeedBooking(boolean needBooking) {
        this.needBooking = needBooking;
    }

    public boolean isSupportJD() {
        return supportJD;
    }

    public void setSupportJD(boolean supportJD) {
        this.supportJD = supportJD;
    }

    public boolean isSupportSN() {
        return supportSN;
    }

    public void setSupportSN(boolean supportSN) {
        this.supportSN = supportSN;
    }

    public boolean isSupportGM() {
        return supportGM;
    }

    public void setSupportGM(boolean supportGM) {
        this.supportGM = supportGM;
    }
}
