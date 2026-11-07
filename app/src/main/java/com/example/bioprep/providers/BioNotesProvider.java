package com.example.bioprep.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

/**
 * Biology Notes Content Provider conforming directly to Lab 08 Topic 10.
 * Implements full CRUD with SQLiteOpenHelper DatabaseHelper, UriMatcher, and projection mapping.
 */
public class BioNotesProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.example.bioprep.provider.notes";
    public static final String URL = "content://" + PROVIDER_NAME + "/notes";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DETAILS = "details";
    public static final String CATEGORY = "category";
    public static final String CREATED_AT = "created_at";

    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;

    private static final UriMatcher uriMatcher;
    private static final HashMap<String, String> NOTES_PROJECTION_MAP = new HashMap<>();

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "notes", NOTES);
        uriMatcher.addURI(PROVIDER_NAME, "notes/#", NOTE_ID);
    }

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public static final String DATABASE_NAME = "BioNotes.db";
    public static final String NOTES_TABLE_NAME = "notes";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_DB_TABLE =
            " CREATE TABLE " + NOTES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " title TEXT NOT NULL, " +
                    " details TEXT NOT NULL, " +
                    " category TEXT, " +
                    " created_at TEXT);";

    /**
     * DatabaseHelper implementing SQLiteOpenHelper.
     */
    public static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
            // Pre-populate with initial Biology notes from Stitch UI
            db.execSQL("INSERT INTO " + NOTES_TABLE_NAME + " (title, details, category, created_at) VALUES " +
                    "('Photosynthesis & Respiration', 'Important stages of photosynthesis, the Calvin Cycle, light-dependent reactions, and cellular respiration.', 'উদ্ভিদ শারীরতত্ত্ব', 'Oct 25, 2026');");
            db.execSQL("INSERT INTO " + NOTES_TABLE_NAME + " (title, details, category, created_at) VALUES " +
                    "('Mitosis & Meiosis', 'Important stages of cell division, chromosome behavior, and differences between mitosis and meiosis.', 'কোষ বিভাজন', 'Oct 18, 2026');");
            db.execSQL("INSERT INTO " + NOTES_TABLE_NAME + " (title, details, category, created_at) VALUES " +
                    "('Structure of DNA & Replication', 'Understanding the double helix, nucleotide bonds, base pairings, and mechanism of replication.', 'কোষ ও এর গঠন', 'Oct 12, 2026');");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db != null);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NOTES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case NOTES:
                qb.setProjectionMap(NOTES_PROJECTION_MAP);
                break;
            case NOTE_ID:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = _ID + " DESC";
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (getContext() != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = db.insert(NOTES_TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(_uri, null);
            }
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                count = db.update(NOTES_TABLE_NAME, values, selection, selectionArgs);
                break;
            case NOTE_ID:
                count = db.update(NOTES_TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                count = db.delete(NOTES_TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(NOTES_TABLE_NAME, _ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case NOTES:
                return "vnd.android.cursor.dir/vnd.example.notes";
            case NOTE_ID:
                return "vnd.android.cursor.item/vnd.example.notes";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}

