package com.zlobrynya.project.readermang.DataBasePMR;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Nikita on 17.03.2016.
 */
public class ClassDataBaseViewedHead {

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    public static final String DATABASE_TABLE = "ViewedHead";
    public static final String NAME_MANG = "Name_mang";
    public static final String VIEWED_HEAD = "Viewed_head";
    public static final String LAST_CHAPTER = "Last_chapter";
    public static final String LAST_PAGE = "Last_page";
    public static final String NAME_LAST_CHAPTER = "name_last_chapter";
    public static final String NOTEBOOK  = "Notebook";
    public static final String DATA  = "Date";
    public static final String NUMBER_OF_HEADS = "Number_chapter";


    public ClassDataBaseViewedHead(Context context){
        //тут косяк NullPointerException
        mDatabaseHelper = new DatabaseHelper(context);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        String DATABASE_CREATE_SCRIPT = "create table if not exists "
                +  DATABASE_TABLE + " (" + NAME_MANG + " text not null, " +  VIEWED_HEAD
                + " text not null, " +  LAST_CHAPTER + " text, "+  NAME_LAST_CHAPTER + " text, " +   LAST_PAGE +" integer, "+  NOTEBOOK +  " integer," +  DATA + " text, " +
                NUMBER_OF_HEADS + " integer);";
        mSqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);
    }

    public void closeDataBase() {
        if (mSqLiteDatabase.isOpen())
            mSqLiteDatabase.close();
    }

    public ClassDataBaseViewedHead(Context context, String name){
        mDatabaseHelper = new DatabaseHelper(context);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        String DATABASE_CREATE_SCRIPT = "create table if not exists "
                + DATABASE_TABLE + " (" + NAME_MANG + " text not null, " + VIEWED_HEAD
                + " text not null, " +  LAST_CHAPTER + " text, "+  NAME_LAST_CHAPTER + " text, " +   LAST_PAGE +" integer, "+  NOTEBOOK +  " integer," +  DATA + " text, " +
                NUMBER_OF_HEADS + " integer);";
        mSqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);
        addBasaData(name);
    }

    //добавление в базу данных
    public boolean addBasaData(String nameMang){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " +  NAME_MANG + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + " LIKE " +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() == 0){
            ContentValues newValues = new ContentValues();
            // Задайте значения для каждого столбца
            newValues.put( NAME_MANG, nameMang.replace('"', ' '));
            newValues.put( LAST_CHAPTER, "null");
            newValues.put( VIEWED_HEAD,"null");
            newValues.put( NAME_LAST_CHAPTER,"null");
            newValues.put( NOTEBOOK,0);
            newValues.put( LAST_PAGE,1);
            newValues.put( NUMBER_OF_HEADS,0);

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
        query = "SELECT " + "*" + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + " LIKE " +
                name;
        String data = null;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            data = cursor.getString(cursor.getColumnIndex(where));
        }
        cursor.close();
        return data;
    }

    public long fetchPlacesCount() {
        String sql = "SELECT COUNT(*) FROM " +  DATABASE_TABLE;
        SQLiteStatement statement = mSqLiteDatabase.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }

    public void  editLastChapter(String nameMang, String thisURL){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + " LIKE " +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put( LAST_CHAPTER, thisURL);
            mSqLiteDatabase.update("ViewedHead", cv,  NAME_MANG + " LIKE " + name,null);
            cv.put( LAST_PAGE, "1");
            mSqLiteDatabase.update("ViewedHead", cv,  NAME_MANG + " LIKE " + name,null);
        }
    }

    public void addViewedChapter(String nameMang, String thisChap){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + " LIKE " +
                name;
       // Log.i("qqql",query);
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            String viewedHead = "1";
            viewedHead = cursor.getString(cursor.getColumnIndex( VIEWED_HEAD));
            //lastChapter =  cursor.getString(cursor.getColumnIndex( LAST_CHAPTER));
            cursor.close();
            if (!viewedHead.contains(thisChap)){
                if (viewedHead.contains("null")){
                    viewedHead = thisChap;
                }else{
                    viewedHead += ","+thisChap;
                }
            }
            ContentValues cv = new ContentValues();
            cv.put( VIEWED_HEAD, viewedHead);
            mSqLiteDatabase.update("ViewedHead", cv,  NAME_MANG + " LIKE " + name, null);
        }
    }

    public void setData(String nameMang, String data,String where){
        String query,name;
        name = "\"";
        name += nameMang.replace('"', ' ') + "\"";
        query = "SELECT " + "*" + " FROM " +  DATABASE_TABLE + " WHERE " +  NAME_MANG + " LIKE " +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put(where, data);
            mSqLiteDatabase.update("ViewedHead", cv,  NAME_MANG + " LIKE " + name,null);
        }
        cursor.close();
    }

    public Cursor getViewedChapter(String NameTable){
        String query;
        query = "SELECT " + NameTable + ".*" + ", "+ LAST_CHAPTER + ", "+ NAME_LAST_CHAPTER + ", "+ LAST_PAGE + ", "+ DATA + " " +
                "FROM ViewedHead" +
                " INNER JOIN " + NameTable + " ON "+ NAME_MANG + " = " + NameTable +"."+ ClassDataBaseListMang.NAME_MANG + " ORDER BY date(" + DATA + ") DESC";
        Log.i("BD Viewed",query);

        return mSqLiteDatabase.rawQuery(query, null);
    }

    public Cursor getNotebook(){
        String query;
        query = "select name from sqlite_master where type = 'table'";
        Cursor cursorTable = mSqLiteDatabase.rawQuery(query, null);
        cursorTable.moveToFirst();

        query = "";
        Cursor cursor = null;
        for (int i = 0; i < cursorTable.getCount();i++){
            String table = cursorTable.getString(0);
            if (!(table.contains("metadata") || table.contains("sqlite") || table.contains("ViewedHead") || table.contains("DownloadMang"))){
                if (!query.isEmpty()){
                    query += " UNION ";
                }
                query += "SELECT " + table + ".*" + ", "+ LAST_PAGE + ", "+ LAST_CHAPTER + ", "+ NAME_LAST_CHAPTER +" FROM ViewedHead" +
                        " INNER JOIN " + table + " ON " + NAME_MANG + " = " + table + "." + ClassDataBaseListMang.NAME_MANG +
                        " WHERE " + NOTEBOOK + " > 0";

                Log.i("BD notebook",query);
            }
            cursorTable.moveToNext();
        }
        if (!query.isEmpty()) cursor = mSqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public Cursor getNotebook(String nameTable){
        String query;
        query = "SELECT " + nameTable + ".*" + ", "+ LAST_PAGE + ", "+ LAST_CHAPTER + ", "+ NAME_LAST_CHAPTER +" FROM ViewedHead" +
                " INNER JOIN " + nameTable + " ON " + NAME_MANG + " = " + nameTable + "." + ClassDataBaseListMang.NAME_MANG +
                " WHERE " + NOTEBOOK + " > 0";
        if (!query.isEmpty()) {
            return mSqLiteDatabase.rawQuery(query, null);
        }
        return null;
    }
    //Получаем есть ли на этом сайте закладки
    public boolean whetherThereIsABookmarkInSite(String nameTable){
        String query;
        query = "SELECT " + nameTable + ".*" + ", "+ LAST_PAGE + ", "+ LAST_CHAPTER + ", "+ NAME_LAST_CHAPTER +" FROM ViewedHead" +
                " INNER JOIN " + nameTable + " ON " + NAME_MANG + " = " + nameTable + "." + ClassDataBaseListMang.NAME_MANG +
                " WHERE " + NOTEBOOK + " > 0";
        try {
            if (!query.isEmpty()){
                //Посмотреть что за SuppressLint
                @SuppressLint("Recycle") Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
               // Log.i("Zap", String.valueOf(cursor.getCount()));
                return cursor.getCount() > 0;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
