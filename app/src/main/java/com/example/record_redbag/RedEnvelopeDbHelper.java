package com.example.record_redbag;// RedEnvelopeDbHelper.java

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RedEnvelopeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "redenvelope.db";
    private static final int DATABASE_VERSION = 1;

    public RedEnvelopeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_RED_ENVELOPE_TABLE =  "CREATE TABLE " +
                RedEnvelopeContract.RedEnvelopeEntry.TABLE_NAME + " (" +
                RedEnvelopeContract.RedEnvelopeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RedEnvelopeContract.RedEnvelopeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RedEnvelopeContract.RedEnvelopeEntry.COLUMN_AMOUNT + " INTEGER NOT NULL DEFAULT 0, " +
                RedEnvelopeContract.RedEnvelopeEntry.COLUMN_TIME + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_RED_ENVELOPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果数据库结构发生更改，则在此处执行升级逻辑
    }
}
