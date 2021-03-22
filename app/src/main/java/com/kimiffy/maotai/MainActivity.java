package com.kimiffy.maotai;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


/**
 * Description:
 * Created by kimiffy on 2020/12/22.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private static final String TAG = "MainActivity";
    public static final String BOOKING = "SP_booking";
    public static final String JD = "SP_JD";
    public static final String SN = "SP_SN";
    public static final String GM = "SP_GM";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }


    private void initData() {
        boolean booking = SpUtil.getBoolean(BOOKING, false);//默认关闭自动预约
        boolean jd = SpUtil.getBoolean(JD, true);//默认开启京东
        boolean sn = SpUtil.getBoolean(SN, false);
        boolean gm = SpUtil.getBoolean(GM, false);
        App app = App.getMyApplication();
        app.setNeedBooking(booking);
        app.setSupportJD(jd);
        app.setSupportSN(sn);
        app.setSupportGM(gm);
    }


    private void initView() {

        findViewById(R.id.fab_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAccessibilitySettingsOn(MainActivity.this, HelpService.class)) {
                    Toast.makeText(MainActivity.this, "服务已经开启了!", Toast.LENGTH_SHORT).show();
                } else {
                    SettingForAccessibility();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();

        Switch YY = menu.findItem(R.id.nav_order).getActionView().findViewById(R.id.switch_order);
        YY.setChecked(App.getMyApplication().isNeedBooking());
        YY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.getMyApplication().setNeedBooking(isChecked);
                SpUtil.setBoolean(BOOKING, isChecked);
            }
        });

        Switch JDSwitch = menu.findItem(R.id.nav_JD).getActionView().findViewById(R.id.switch_order);
        JDSwitch.setChecked(App.getMyApplication().isSupportJD());
        JDSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.getMyApplication().setSupportJD(isChecked);
                SpUtil.setBoolean(JD, isChecked);
            }
        });

        Switch SNSwitch = menu.findItem(R.id.nav_SN).getActionView().findViewById(R.id.switch_order);
        SNSwitch.setChecked(App.getMyApplication().isSupportSN());
        SNSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.getMyApplication().setSupportSN(isChecked);
                SpUtil.setBoolean(JD, isChecked);
            }
        });

        Switch GMSwitch = menu.findItem(R.id.nav_GM).getActionView().findViewById(R.id.switch_order);
        GMSwitch.setChecked(App.getMyApplication().isSupportGM());
        GMSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.getMyApplication().setSupportGM(isChecked);
                SpUtil.setBoolean(JD, isChecked);
            }
        });

    }


    /**
     * 辅助服务是否打开了
     */
    public static boolean isAccessibilitySettingsOn(Context mContext, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + clazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void SettingForAccessibility() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        int id = menuItem.getItemId();
//        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
