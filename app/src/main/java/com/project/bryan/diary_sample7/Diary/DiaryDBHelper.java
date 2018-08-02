package com.project.bryan.diary_sample7.Diary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDBHelper extends SQLiteOpenHelper {
    public DiaryDBHelper(Context context) {
        super(context, "Diary", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table DIARY_TB (" +
                "DATE TEXT PRIMARY KEY, " +
                "CONTENTS TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists DIARY_TB";
        db.execSQL(sql);
        onCreate(db);
    }

}
