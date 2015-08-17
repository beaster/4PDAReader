package com.zetadex.pdareader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PdaDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pda";
    private static final int DATABASE_VERSION = 1;

    public PdaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Columns.NewsColumns.CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Columns.NewsColumns.TABLE_NAME);
        onCreate(db);
    }
}
