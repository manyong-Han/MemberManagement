package com.manyong.membermanagement.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hanman-yong on 2020-03-11.
 */
public class dbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "member_management.db";
    private static final int DATABASE_VERSION = 1;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        // 회원 정보 테이블 생성
        db.execSQL("CREATE TABLE member ( " +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT," + // 관리 번호
                "id TEXT UNIQUE NOT NULL," +               // 아이디
                "password TEXT NOT NULL, " +               // 비밀번호
                "name TEXT NOT NULL, " +                            // 이름
                "birthday TEXT NOT NULL, " +                        // 생년월일
                "phone TEXT NOT NULL, " +                           // 핸드폰번호
                "classes TEXT, " +                         // 회원 구분 (일반회원/관리자)
                "profile_image TEXT);"                     // 프로필 사진
        );
    }

    // 버전을 변경하게되면 기존의 테이블을 지운 후에 다시 db를 생성한다.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if EXISTS member");
        onCreate(db);
    }
}
