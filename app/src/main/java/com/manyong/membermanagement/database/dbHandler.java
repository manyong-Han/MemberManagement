package com.manyong.membermanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by hanman-yong on 2020-03-11.
 */
public class dbHandler {
    private final String TAG = "dbHandler";
    private final String TABLE_NAME = "member";

    SQLiteOpenHelper mHelper = null;
    SQLiteDatabase mDB = null;

    public dbHandler(Context context) {
        mHelper = new dbHelper(context);
    }

    public static dbHandler open(Context context) {
        return new dbHandler(context);
    }

    // 회원 DB 관리.
    public Cursor member_select_one(String id) {
        mDB = mHelper.getReadableDatabase();

        String sql_query = "SELECT * FROM " + TABLE_NAME + " WHERE id ='" + id + "'";
        Cursor c = mDB.rawQuery(sql_query, null);

        c.moveToFirst();

        return c;
    }

    public Cursor member_select_image(String id) {
        mDB = mHelper.getReadableDatabase();

        String sql_query = "SELECT profile_image FROM " + TABLE_NAME + " WHERE id ='" + id + "'";
        Cursor c = mDB.rawQuery(sql_query, null);

        c.moveToFirst();

        return c;
    }

    public Cursor member_select() {
        mDB = mHelper.getReadableDatabase();

        String sql_query = "SELECT * FROM " + TABLE_NAME;
        Cursor c = mDB.rawQuery(sql_query, null);
        //c.moveToFirst();

        return c;
    }

    public void member_insert(String id, String password, String name, String birthday,
                              String phone, String classes, String reg_date) {

        Log.d(TAG, "member_insert");

        mDB = mHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put("id", id);
        value.put("password", password);
        value.put("name", name);
        value.put("birthday", birthday);
        value.put("phone", phone);
        value.put("classes", classes);
        value.put("reg_date", reg_date);

        mDB.insert(TABLE_NAME, null, value);
    }

    public void member_profile_update(String id, String profile_image) {
        mDB = mHelper.getWritableDatabase();

        String id_array[] = {id};

        ContentValues value = new ContentValues();
        value.put("profile_image", profile_image);

        mDB.update(TABLE_NAME, value, "id = ?", id_array);
    }

    public void member_update(String id, String name, String phone, String birthday){
        mDB = mHelper.getWritableDatabase();

        String id_array[] = {id};

        ContentValues value = new ContentValues();
        value.put("name", name);
        value.put("phone", phone);
        value.put("birthday", birthday);

        mDB.update(TABLE_NAME, value, "id = ?", id_array);
    }

    public void member_classes_update(String id) {
        Log.d(TAG, "member_update");

        mDB = mHelper.getWritableDatabase();

        String id_array[] = {id};

        ContentValues value = new ContentValues();
        value.put("classes", "관리자");

        mDB.update(TABLE_NAME, value, "id = ?", id_array);
    }

    public void member_pw_update(String id, String password) {
        Log.d(TAG, "member_update");

        mDB = mHelper.getWritableDatabase();

        String id_array[] = {id};

        ContentValues value = new ContentValues();
        value.put("password", password);

        mDB.update(TABLE_NAME, value, "id = ?", id_array);
    }

    public void member_delete(String id) {
        Log.d(TAG, "member_delete");

        mDB = mHelper.getWritableDatabase();

        mDB.delete(TABLE_NAME, "id=?", new String[]{id});
    }

    public void close() {
        mHelper.close();
    }
}