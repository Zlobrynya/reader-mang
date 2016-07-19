package com.example.nikita.progectmangaread.DataBasePMR;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.nikita.progectmangaread.classPMR.ClassMainTop;

/**
 * Created by Nikita on 29.03.2016.
 */
public class ClassDataBaseListMang {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private String nameTable;
    public static final String NAME_MANG = "NameMang";
    public static final String URL_CHAPTER = "Url_chapter";
    public static final String URL_IMG = "URL_img";
    public static final String TOP = "top";

    public ClassDataBaseListMang(Context context, String NameTable){
        nameTable = NameTable.replace("http://"," ");
       // nameBase = nameBase.replace(".ru",".db");
        nameTable = nameTable.replace(".ru"," ");
        nameTable = nameTable.replace(".db"," ");
        nameTable = nameTable.replace(".me"," ");
        nameTable = nameTable.replace(".com"," ");
        mDatabaseHelper = new DatabaseHelper(context);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        //создаем табдлицу если не было ее
        String DATABASE_CREATE_SCRIPT = "create table if not exists "
                + nameTable + " (" + TOP
                + " integer, " +  NAME_MANG
                + " text not null, " +  URL_CHAPTER + " text not null, " +  URL_IMG
                + " text not null);";
        mSqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);
    }

    public void closeDataBase(){
        if (mSqLiteDatabase.isOpen())
            mSqLiteDatabase.close();
    }

    public boolean thereIsInTheDatabase(String nameMang){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " +  NAME_MANG + " FROM " + nameTable + " WHERE " +  NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() == 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    //добавление в базу данных
    public void addBasaData(ClassMainTop a, int number){
        String query,name;
        name = "\"";
        name += a.getName_characher().replace('"',' ') + "\"";
        query = "SELECT " +  NAME_MANG + " FROM " + nameTable + " WHERE " +  NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() == 0){
            ContentValues newValues = new ContentValues();
            // Задайте значения для каждого столбца
            newValues.put( NAME_MANG, a.getName_characher().replace('"', ' '));
            newValues.put( URL_CHAPTER, a.getURL_characher());
            newValues.put( URL_IMG, a.getURL_img());
            newValues.put( TOP,number);
            // Вставляем данные в таблицу
            mSqLiteDatabase.insert(nameTable, null, newValues);
        }
        cursor.close();
    }

    //получаем структуру с именем и сылками
    public ClassMainTop getMainClassTop(int kol){
        String query = "SELECT " + "*" + " FROM " + nameTable + " WHERE " + TOP + "=" +
                (kol);
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        if (cursor.getCount() != 0){
            ClassMainTop a = new ClassMainTop();
            cursor.moveToFirst();
            a.setURL_img(cursor.getString(cursor.getColumnIndex( URL_IMG)));
            a.setName_characher(cursor.getString(cursor.getColumnIndex( NAME_MANG)));
            a.setURL_characher(cursor.getString(cursor.getColumnIndex( URL_CHAPTER)));
            cursor.close();
            return a;
        }
        cursor.close();
        return null;
    }

    //проверяем в бд есть ли в такой элемент
    public Boolean download_the_html(int kol){
        String query = "SELECT " + "*" + " FROM " + nameTable + " WHERE " + TOP + "=" +
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

    public String getDataFromDataBase(String nameMang,String where){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + nameTable + " WHERE " +  NAME_MANG + "=" +
                name;
        String data = "null";
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            data = cursor.getString(cursor.getColumnIndex(where));
        }
        cursor.close();
        return data;
    }

    public long fetchPlacesCount() {
        String sql = "SELECT COUNT(*) FROM " + nameTable;
        SQLiteStatement statement = mSqLiteDatabase.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
}
