package com.zetadex.pdareader.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

public class PdaContentProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "com.pda.news";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
            + CONTENT_AUTHORITY);

    private static final String PATH_ARTICLES = "articles";
    public final static Uri ARTICLES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_ARTICLES).build();

    private static final String CONTENT_TYPE_ARTICLE = "vnd.android.cursor.dir/vnd.com.pda.news.article";
    private static final String CONTENT_ITEM_TYPE_ARTICLE = "vnd.android.cursor.item/vnd.com.pda.news.article";

    private final static int ARTICLES = 1;
    private final static int ARTICLES_ID = 2;

    private final static UriMatcher URI_MATCHER = buildMatcher();

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, "articles", ARTICLES);
        matcher.addURI(CONTENT_AUTHORITY, "articles/*", ARTICLES_ID);

        return matcher;

    }


    private PdaDbHelper mDataBaseHelper;

    @Override
    public boolean onCreate() {
        mDataBaseHelper = new PdaDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mDataBaseHelper.getReadableDatabase();

        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Columns.NewsColumns.TABLE_NAME);

        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case ARTICLES_ID:
                String id = uri.getPathSegments().get(1);
                builder.appendWhere(Columns.NewsColumns._ID + "=" + id);
                break;
        }

        Cursor c = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase sdb = mDataBaseHelper.getWritableDatabase();

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO `").append(Columns.NewsColumns.TABLE_NAME).append("`(`")
                .append(Columns.NewsColumns.CATEGORY).append("`, `")
                .append(Columns.NewsColumns.TITLE).append("`, `")
                .append(Columns.NewsColumns.DATA).append("`, `")
                .append(Columns.NewsColumns.PHOTO_URL).append("`, `")
                .append(Columns.NewsColumns.ARTICLE_URL).append("`) VALUES (?, ?, ?, ?, ?);");

        sdb.beginTransaction();
        SQLiteStatement stmt = sdb.compileStatement(statement.toString());

        int length = values.length;
        for (int i = 0; i < length; i++) {
            stmt.bindLong(1, values[i].getAsInteger(Columns.NewsColumns.CATEGORY));
            stmt.bindString(2, values[i].getAsString(Columns.NewsColumns.TITLE));
            stmt.bindNull(3); //data
            stmt.bindString(4, values[i].getAsString(Columns.NewsColumns.PHOTO_URL));
            stmt.bindString(5, values[i].getAsString(Columns.NewsColumns.ARTICLE_URL));

            stmt.executeInsert();
        }
        sdb.setTransactionSuccessful();
        sdb.endTransaction();

        //notify ui
        getContext().getContentResolver().notifyChange(uri, null);

        return length;
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case ARTICLES:
                return CONTENT_TYPE_ARTICLE;
            case ARTICLES_ID:
                return CONTENT_ITEM_TYPE_ARTICLE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        validateUri(uri);

        long id = db.insertOrThrow(Columns.NewsColumns.TABLE_NAME, null,
                values);

        Uri newUri = ContentUris.withAppendedId(ARTICLES_CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(uri, null);

        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sdb = mDataBaseHelper.getWritableDatabase();

        validateUri(uri);

        int rows = sdb.delete(Columns.NewsColumns.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    private void validateUri(Uri uri) {
        final int match = URI_MATCHER.match(uri);

        if (match != ARTICLES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //No implemented yet
        return 0;
    }
}
