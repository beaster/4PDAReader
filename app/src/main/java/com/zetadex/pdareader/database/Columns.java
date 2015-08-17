package com.zetadex.pdareader.database;

import android.provider.BaseColumns;

public class Columns {

    // paramSQLiteDatabase.execSQL("create table news_table (id integer primary key autoincrement,category integer,title text,data text,photo text,url text);");

    public static final class NewsColumns implements BaseColumns {

        public final static String TABLE_NAME = "news";

        public final static String CATEGORY = "category";

        public final static String TITLE = "title";

        public final static String DATA = "data";

        public final static String PHOTO_URL = "photo_url";

        public final static String ARTICLE_URL = "article_url";

        public final static String CREATE_STATEMENT = "CREATE TABLE " + NewsColumns.TABLE_NAME + " (" + NewsColumns._ID + " integer primary key autoincrement," +
                NewsColumns.CATEGORY + " integer," + NewsColumns.TITLE + " text," + NewsColumns.DATA + " text," + NewsColumns.PHOTO_URL + " text," + NewsColumns.ARTICLE_URL + " text)";

    }

}
