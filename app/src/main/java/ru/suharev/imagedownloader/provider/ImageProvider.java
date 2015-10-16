package ru.suharev.imagedownloader.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Provider для работы с БД, хранящей полученную информацию
 * Created by pasha on 02.10.2015.
 */
public class ImageProvider extends ContentProvider {

    private static final String DATABASE_NAME = "image_database";

    private static final String SORT_ORDER_ID = Columns._ID + " ASC";

    private static final String SCHEME = "content://";
    private static final String SLASH = "/";
    private static final String AUTHORITIES = "ru.suharev.imagedownloader.provider.ImageProvider";

    private static UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sMatcher.addURI(AUTHORITIES, Tables.IMAGE, UriCodes.URI_IMAGE);
    }

    private DatabaseHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (sortOrder == null) sortOrder = SORT_ORDER_ID;
        switch (sMatcher.match(uri)) {
            case UriCodes.URI_IMAGE:
                return db.query(Tables.IMAGE, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        switch (sMatcher.match(uri)){
            case UriCodes.URI_IMAGE:
                Long id  = db.insert(Tables.IMAGE, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        switch (sMatcher.match(uri)){
            case UriCodes.URI_IMAGE:
                int count =  db.delete(Tables.IMAGE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return count;
            default:
                return 0;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        switch (sMatcher.match(uri)){
            case UriCodes.URI_IMAGE:
                int count = db.update(Tables.IMAGE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return count;
            default:
                return 0;
        }
    }


    public static class UriCodes{

        public static final int URI_IMAGE = 1;
    }

    public static class Uris{

        public static final Uri URI_IMAGE = Uri.parse(SCHEME + AUTHORITIES + SLASH + Tables.IMAGE);

    }

    public static class Tables{

        public static final String IMAGE = "image_table";

    }

    public static class Columns implements BaseColumns{

        public static final String ID = "id";
        public static final String IMAGE_URI = "image_uri";
        public static final String TITLE = "title";

    }

    private static class DatabaseHelper extends SQLiteOpenHelper{

        static final int VERSION = 2;

        static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + Tables.IMAGE + " (" +
                Columns._ID + " INTEGER PRIMARY KEY, " +
                Columns.ID + " TEXT, " +
                Columns.IMAGE_URI + " TEXT, " +
                Columns.TITLE + " TEXT);";

        static final String DROP_TABLE_IMAGE = "DROP TABLE IF EXISTS " + Tables.IMAGE;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_IMAGE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE_IMAGE);
            onCreate(db);
        }
    }
}
