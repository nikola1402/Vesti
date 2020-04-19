package com.example.nikol.vesti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseBroker {

    public static final String ROW_ID = "_id";
    private static final String C_TITLE = "title";
    private static final String C_DESC = "description";
    private static final String C_URL = "url";
    private static final String C_URL_IMAGE = "urlToImage";

    private static final String DATABASE_NAME = "bazaVesti";
    private static final String DATABASE_TABLE = "tabelaVesti";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "DBAdapter";

    private static final String DATABASE_CREATE =
            "create table "+ DATABASE_TABLE +" ("+ ROW_ID +" integer primary key autoincrement, "+ C_TITLE +" varchar, "+ C_DESC +" varchar, "+ C_URL +" varchar, "+ C_URL_IMAGE +" varchar);";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseBroker(Context ctx) {

        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    //---otvara bazu---
    public DatabaseBroker open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---zatvara bazu---
    public void close() {
        DBHelper.close();
    }

    //---unos vesti u bazu---
    public long insertArticle(String title, String description, String url, String urlToImage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(C_TITLE, title);
        contentValues.put(C_DESC, description);
        contentValues.put(C_URL, url);
        contentValues.put(C_URL_IMAGE, urlToImage);
        return db.insert(DATABASE_TABLE, null, contentValues);
    }

    //---brisanje zeljene vesti---
    public boolean deleteArticle(String url) {
        return db.delete(DATABASE_TABLE, C_URL + "='" + url+"'", null) > 0;
    }

    //---vraca sve vesti---
    public Cursor getAllArticles() {
        Cursor res = db.rawQuery("select * from "+ DATABASE_TABLE, null);
        return res;
    }

    //trazi odredjenu vest u bazi kako bi se sprecilo dupliranje
    public String checkArticle(String url) {

        Cursor res = db.rawQuery("select title from " + DATABASE_TABLE + " where url='"+url+"'", null);

        String t = new String();
        if(res.moveToFirst()){
            t = res.getString(res.getColumnIndex("title"));
        }
        return t;
    }


    //---vraca odabranu vest---
    public Cursor getArticle(long rowId) throws SQLException {

        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {ROW_ID, C_TITLE, C_DESC, }, ROW_ID + "=" + rowId, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}
