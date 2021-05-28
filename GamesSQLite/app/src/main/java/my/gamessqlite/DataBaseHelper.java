package my.gamessqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String GAME_TABLE = "Game_Table";
    public static final String GAME_WANTED_TABLE = "Game_Wanted_Table";
    public static final String COLUMN_GAME_NAME = "GAME_NAME";
    public static final String COLUMN_GAME_PLATFORM = "GAME_PLATFORM";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "GamesTracker.db", null, 1);
    }

    // This is called the first time a database is accessed. There should be code here to create a new database.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "create table " + GAME_TABLE + " (" + COLUMN_GAME_NAME + " TEXT, " + COLUMN_GAME_PLATFORM + " TEXT )";
        String createSecondTableStatement = "create table " + GAME_WANTED_TABLE + " (" + COLUMN_GAME_NAME + " TEXT, " + COLUMN_GAME_PLATFORM + " TEXT )";
        db.execSQL(createTableStatement);
        db.execSQL(createSecondTableStatement);
    }

    // This is called if the database version number changes. It prevents previous user apps from crashing.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean AddOne(GameInformation gameInformation, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if(gameInformation.getPlatform().equals("error")){
            return false;
        }
        cv.put(COLUMN_GAME_NAME, gameInformation.getName());
        cv.put(COLUMN_GAME_PLATFORM, gameInformation.getPlatform());

        long insert = db.insert(tableName, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void RemoveOne(GameInformation gameInformation, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "delete from " + tableName +
                " where " + COLUMN_GAME_NAME + " like " + '\'' + gameInformation.getName() + '\'' +
                " and " + COLUMN_GAME_PLATFORM + " like " + '\'' + gameInformation.getPlatform() + '\'';
        db.execSQL(queryString);
        db.close();
    }

    public List<GameInformation> GetEverything(String tableName) {
        String queryString = "select * from " + tableName + " order by " + COLUMN_GAME_NAME + " asc, " + COLUMN_GAME_PLATFORM + " asc";
        return AddDbToArray(queryString);
    }


    public List<GameInformation> Search(GameInformation gameInformation, String tableName) {
        String queryString = "select * from " + tableName + " where " + COLUMN_GAME_NAME + " like " + '\'' + '%' + gameInformation.getName() + '%' + '\'' + " order by " + COLUMN_GAME_PLATFORM + " asc";
        return AddDbToArray(queryString);
    }

    public boolean SearchForExactGame(String gameName, String platform, String tableName) {
        String queryString = "select * from " + tableName + " where " + COLUMN_GAME_NAME + " = " + '\'' + gameName + '\'' + " and " + COLUMN_GAME_PLATFORM + " = " + '\'' + platform + '\'';

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    private List<GameInformation> AddDbToArray(String queryString) {
        List<GameInformation> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                String gameName = cursor.getString(0);
                String platform = cursor.getString(1);
                GameInformation gameInformation = new GameInformation(gameName, platform);
                returnList.add(gameInformation);
            }
            while (cursor.moveToNext());
        } else {

        }
        cursor.close();
        db.close();
        return returnList;
    }
}
