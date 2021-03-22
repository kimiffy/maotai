package com.kimiffy.maotai;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Description:
 * Created by kimiffy on 2020/1/13.
 */

public class SpUtil {

    public static final String SP_NAME = "maotaihelper";

    /**
     * 描述：将布尔值写入SharedPreferences文件中
     *
     * @param key   键
     * @param value 值
     */
    public static void setBoolean(String key, boolean value) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value).apply();
    }

    /**
     * 描述：获取存储的boolean数据
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * 描述：存储String数据到SharedPreferences中
     *
     * @param key
     * @param value
     */
    public static void setString(String key, String value) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value).apply();
    }

    /**
     * 描述：获取存储的String数据
     *
     * @param key 键
     * @return
     */
    public static String getString(String key) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 描述：保存int
     *
     * @param key
     * @param value
     */
    public static void setInt(String key, int value) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value).apply();
    }

    /**
     * 描述：取出int值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        return sp.getInt(key, defaultValue);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public static void remove(String key) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public static Map<String, ?> getAll() {
        SharedPreferences sp = App.getMyApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }


}
