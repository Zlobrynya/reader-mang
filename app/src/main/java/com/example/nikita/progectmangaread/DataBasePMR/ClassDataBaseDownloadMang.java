package com.example.nikita.progectmangaread.DataBasePMR;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Nikita on 19.07.2016.
 */
public class ClassDataBaseDownloadMang {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private String nameTable;
    public static final String NAME_MANG = "NameMang";
    public static final String DATABASE_TABLE = "DownloadMang";
    public static final String URL_MANG = "Url_mang";
    public static final String NAME_CHAPTER = "Name_chapter";
    public static final String NAME_DIR = "Name_dir";
    public static final String RATING = "rating";
    public static final String TOMS = "toms";
    public static final String TRANSLATION = "translation";
    public static final String AUTHOR = "author";
    public static final String GENRES = "genres";
    public static final String DESCRIPTION = "description";
    public static final String NAME_IMG = "img";
    public static final String CATEGORY = "category";

    public ClassDataBaseDownloadMang(Context context){
        mDatabaseHelper = new DatabaseHelper(context);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        String DATABASE_CREATE_SCRIPT = "create table if not exists "
                +  DATABASE_TABLE + " (" + NAME_MANG + " text not null, " +  URL_MANG + " text not null, " +
                NAME_CHAPTER + " text not null, "+  NAME_DIR+ " text not null, " + RATING + " text not null, " +
                TOMS + " text not null, " + TRANSLATION + " text not null, " + AUTHOR + " text not null, " +
                GENRES + " text not null, " + CATEGORY + " text not null, " + NAME_IMG + " text not null, " + DESCRIPTION + " text);";
        mSqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);
    }

    public void closeDataBase(){
        if (mSqLiteDatabase.isOpen())
            mSqLiteDatabase.close();
    }


    //добавление в базу данных
    public boolean addBasaData(String nameMang){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " +  NAME_MANG + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() == 0){
            ContentValues newValues = new ContentValues();
            // Задайте значения для каждого столбца
            newValues.put( NAME_MANG, nameMang.replace('"', ' '));
            newValues.put( URL_MANG, "null");
            newValues.put( NAME_CHAPTER,"null");
            newValues.put( NAME_DIR,"null");
            newValues.put( RATING, "null");
            newValues.put( TOMS, "null");
            newValues.put( TRANSLATION, "null");
            newValues.put( AUTHOR, "null");
            newValues.put( GENRES, "null");
            newValues.put( DESCRIPTION, "");
            newValues.put( NAME_IMG,"null");
            newValues.put( CATEGORY,"null");

            // Вставляем данные в таблицу
            mSqLiteDatabase.insert(DATABASE_TABLE, null, newValues);
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void setData(String nameMang, String data,String where){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + "=" +
                name;

        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put(where, data);
            mSqLiteDatabase.update(DATABASE_TABLE, cv,  NAME_MANG + "=" + name,null);
        }
        cursor.close();
    }

    public Cursor getDownloadMang(){
        String query;
        query = "SELECT " + "*" + "FROM " + DATABASE_TABLE;
        Log.i("BD Viewed", query);
        return mSqLiteDatabase.rawQuery(query, null);
    }

    public String getDataFromDataBase(String nameMang,String where){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + "=" +
                name;
        String data = null;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            data = cursor.getString(cursor.getColumnIndex(where));
        }
        cursor.close();
        return data;
    }

    public long fetchPlacesCount() {
        String sql = "SELECT COUNT(*) FROM " +  DATABASE_TABLE;
        SQLiteStatement statement = mSqLiteDatabase.compileStatement(sql);
        return statement.simpleQueryForLong();
    }
}
