package com.example.nikita.progectmangaread.DataBasePMR;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Nikita on 17.03.2016.
 */
public class DataBaseViewedHead extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ViewedHead.db";
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_TABLE = "ViewedHead";
    public static final String NAME_MANG = "Name_mang";
    public static final String VIEWED_HEAD = "Viewed_head";
    public static final String LAST_CHAPTER = "Last_chapter";
    public static final String LAST_PAGE= "Last_page";
    public static final String NAME_LAST_CHAPTER= "name_last_chapter";
    public static final String URL_IMG = "url_img";
    public static final String NOTEBOOK  = "notebook";




    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + NAME_MANG + " text not null, " + VIEWED_HEAD
            + " text not null, " + LAST_CHAPTER + " text, "+ NAME_LAST_CHAPTER + " text, " + URL_IMG
            + " text, " + LAST_PAGE +" integer, "+ NOTEBOOK +  " integer);";
/* " text not null, " + LAST_PAGE +
" (" + BaseColumns._ID
            + " integer primary key autoincrement, " + NAME_MANG
            + " text not null, " + URL_CHAPTER + " text not null, " + URL_IMG
            + " text not null);"
 */

    public DataBaseViewedHead(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, DATABASE_VERSION);
    }

    public DataBaseViewedHead(Context context, String name, SQLiteDatabase.CursorFactory factory,DatabaseErrorHandler errorHandler) {
        super(context, name, factory, DATABASE_VERSION, errorHandler);
    }

    public DataBaseViewedHead(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
