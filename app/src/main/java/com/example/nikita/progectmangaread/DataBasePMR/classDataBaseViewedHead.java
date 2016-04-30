package com.example.nikita.progectmangaread.DataBasePMR;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Nikita on 17.03.2016.
 */
public class classDataBaseViewedHead {

    private DataBaseViewedHead mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    public classDataBaseViewedHead(Context context){
        String nameBase = "ViewedHead.db";
        mDatabaseHelper = new DataBaseViewedHead(context,nameBase, null);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }


    public classDataBaseViewedHead(Context context,String name){
        String nameBase = "ViewedHead.db";
        mDatabaseHelper = new DataBaseViewedHead(context,nameBase, null);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        addBasaData(name);
    }
    //добавление в базу данных
    public boolean addBasaData(String nameMang){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + DataBaseViewedHead.NAME_MANG + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() == 0){
            ContentValues newValues = new ContentValues();
            // Задайте значения для каждого столбца
            newValues.put(DataBaseViewedHead.NAME_MANG, nameMang);
            newValues.put(DataBaseViewedHead.LAST_CHAPTER, "null");
            newValues.put(DataBaseViewedHead.VIEWED_HEAD,"null");
            newValues.put(DataBaseViewedHead.NAME_LAST_CHAPTER,"null");
            newValues.put(DataBaseViewedHead.URL_IMG,"null");
            // Вставляем данные в таблицу
            mSqLiteDatabase.insert("ViewedHead", null, newValues);
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public String getDataFromDataBase(String nameMang,String where){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        String data = null;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            data = cursor.getString(cursor.getColumnIndex(where));
            //data[1] = cursor.getString(cursor.getColumnIndex(DataBaseViewedHead.LAST_CHAPTER));
            //cursor.close();
        }
        cursor.close();
        return data;
    }

    public void editPage(String nameMang, String numberPage){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            String viewedHead,lastChapter;
            lastChapter =  cursor.getString(cursor.getColumnIndex(DataBaseViewedHead.LAST_CHAPTER));
            cursor.close();
            String[] strings = lastChapter.split(",");
            lastChapter = strings[0]+","+numberPage;
            ContentValues cv = new ContentValues();
            cv.put(DataBaseViewedHead.LAST_CHAPTER, lastChapter);
            mSqLiteDatabase.update("ViewedHead", cv, DataBaseViewedHead.NAME_MANG + "=" + name, null);
        }
    }

    public long fetchPlacesCount() {
        String sql = "SELECT COUNT(*) FROM " + DataBaseViewedHead.DATABASE_TABLE;
        SQLiteStatement statement = mSqLiteDatabase.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }

    public void  editLastChapter(String nameMang, String thisURL){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put(DataBaseViewedHead.LAST_CHAPTER, thisURL);
            mSqLiteDatabase.update("ViewedHead", cv, DataBaseViewedHead.NAME_MANG + "=" + name,null);
            cv.put(DataBaseViewedHead.LAST_PAGE, "1");
            mSqLiteDatabase.update("ViewedHead", cv, DataBaseViewedHead.NAME_MANG + "=" + name,null);
        }
    }

    public void editBaseDate(String nameMang, String thisChap){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            String viewedHead,lastChapter,lastPage = "1";
            viewedHead = cursor.getString(cursor.getColumnIndex(DataBaseViewedHead.VIEWED_HEAD));
            //lastChapter =  cursor.getString(cursor.getColumnIndex(DataBaseViewedHead.LAST_CHAPTER));
            cursor.close();
            if (!viewedHead.contains(thisChap)){
                if (viewedHead.contains("null")){
                    viewedHead = thisChap;
                }else{
                    viewedHead += ","+thisChap;
                }
            }
            ContentValues cv = new ContentValues();
            cv.put(DataBaseViewedHead.VIEWED_HEAD, viewedHead);
            mSqLiteDatabase.update("ViewedHead", cv, DataBaseViewedHead.NAME_MANG + "=" + name, null);
        }
    }


    public void  editLastPage(String nameMang, int page){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put(DataBaseViewedHead.LAST_PAGE, page);
            mSqLiteDatabase.update("ViewedHead", cv, DataBaseViewedHead.NAME_MANG + "=" + name,null);
        }
    }

    public void editNameLastChapter(String nameMang, String nameChapter){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put(DataBaseViewedHead.NAME_LAST_CHAPTER, nameChapter);
            mSqLiteDatabase.update("ViewedHead", cv, DataBaseViewedHead.NAME_MANG + "=" + name,null);
        }
    }

    public String getNameMang(int i){
        String name = null;
        String query = "select * from ViewedHead WHERE _id="+(i+1);
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            name =  cursor.getString(cursor.getColumnIndex(DataBaseViewedHead.NAME_MANG));
        }
        cursor.close();
        return name;
    }

    public void setData(String nameMang, String urlImg,String where){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " + DataBaseViewedHead.DATABASE_TABLE + " WHERE " + DataBaseViewedHead.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put(where, urlImg);
            mSqLiteDatabase.update("ViewedHead", cv, DataBaseViewedHead.NAME_MANG + "=" + name,null);
        }
    }
}
