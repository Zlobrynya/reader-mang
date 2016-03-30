package com.example.nikita.progectmangaread.DataBasePMR;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.nikita.progectmangaread.classPMR.MainClassTop;

/**
 * Created by Nikita on 29.03.2016.
 */
public class classDataBaseListMang {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    public classDataBaseListMang(Context context,String nameBase){
        nameBase = nameBase.replace("http://","");
        nameBase = nameBase.replace(".ru",".db");
        mDatabaseHelper = new DatabaseHelper(context,nameBase, null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }


    //добавление в базу данных
    public void addBasaData(MainClassTop a, String imgSrc){
        String query,name;
        name = "\"";
        name += a.getName_characher().replace('"',' ') + "\"";
        query = "SELECT " + DatabaseHelper.NAME_MANG + " FROM " + DatabaseHelper.DATABASE_TABLE + " WHERE " + DatabaseHelper.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() == 0){
            ContentValues newValues = new ContentValues();
            // Задайте значения для каждого столбца
            newValues.put(DatabaseHelper.NAME_MANG, a.getName_characher().replace('"', ' '));
            newValues.put(DatabaseHelper.URL_CHAPTER, a.getURL_characher());
            newValues.put(DatabaseHelper.URL_IMG, imgSrc);
            // Вставляем данные в таблицу
            mSqLiteDatabase.insert("Mang", null, newValues);
        }
        cursor.close();
    }

    //получаем структуру с именем и сылками
    public MainClassTop getMainClassTop(int kol, int page){
        String query = "SELECT " + "*" + " FROM " + DatabaseHelper.DATABASE_TABLE + " WHERE " + " _id" + "=" +
                (kol+1);
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        if (cursor.getCount() != 0){
            MainClassTop a = new MainClassTop();
            cursor.moveToFirst();
            a.setURL_img(cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL_IMG)));
            a.setName_characher(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME_MANG)));
            a.setURL_characher(cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL_CHAPTER)));
            cursor.close();
            return a;
        }
        cursor.close();
        return null;
    }

    //проверяем в бд есть ли в такой элемент
    public Boolean download_the_html(int kol, int page){
        String query = "SELECT " + "*" + " FROM " + DatabaseHelper.DATABASE_TABLE + " WHERE " + " _id" + "=" +
                (kol+1);
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        //Log.i("LOG_TAG", "download_the_html " + cursor.getCount());
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    public long fetchPlacesCount() {
        String sql = "SELECT COUNT(*) FROM " + DatabaseHelper.DATABASE_TABLE;
        SQLiteStatement statement = mSqLiteDatabase.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
}
