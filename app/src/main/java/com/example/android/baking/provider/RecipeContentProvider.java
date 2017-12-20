package com.example.android.baking.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.baking.provider.RecipeContract.RecipeEntry;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeContentProvider extends ContentProvider {

    public static final int RECIPES = 100;
    public static final int RECIPE_WITH_ID = 101;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES + "/#", RECIPE_WITH_ID);
        return uriMatcher;
    }

    private RecipeDbHelper recipeDbHelper;

    @Override
    public boolean onCreate() {
        recipeDbHelper = new RecipeDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = recipeDbHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        Uri returnedUri;
        switch (match) {
            case RECIPES:
                long id = db.insert(RecipeEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(RecipeEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnedUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = recipeDbHelper.getReadableDatabase();

        int match = URI_MATCHER.match(uri);
        Cursor cursor;

        switch (match) {
            case RECIPES:
                cursor = db.query(RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECIPE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                cursor = db.query(RecipeEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = recipeDbHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        int rowsDeleted;
        switch (match) {
            case RECIPE_WITH_ID:
                String id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RecipeEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            Context context = getContext();
            if (context != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = recipeDbHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        int rowsUpdated;
        switch (match) {
            case RECIPES:
                rowsUpdated = db.update(RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case RECIPE_WITH_ID:
                if (selection == null) {
                    selection = RecipeEntry._ID + "=?";
                } else {
                    selection += " AND " + RecipeEntry._ID + "=?";
                }

                String id = uri.getLastPathSegment();

                if (selectionArgs == null) {
                    selectionArgs = new String[]{id};
                } else {
                    ArrayList<String> selectionArgsList = new ArrayList<>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(id);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                rowsUpdated = db.update(RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            Context context = getContext();
            if (context != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
