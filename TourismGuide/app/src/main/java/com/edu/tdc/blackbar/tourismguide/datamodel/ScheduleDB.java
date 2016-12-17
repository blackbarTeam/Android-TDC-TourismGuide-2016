package com.edu.tdc.blackbar.tourismguide.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ASUS on 12/17/2016.
 */

public class ScheduleDB extends SQLiteOpenHelper {
    Context context;
    static String NAME_DB = "schedule.db";
    static int VERSION_DB = 1;
    static SQLiteDatabase db= null;
    public ScheduleDB(Context context) {
        super(context, NAME_DB, null, VERSION_DB);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE lichtrinh(location TEXT PRIMARY KEY, datefrom TEXT, dateto TEXT, time TEXT, note TEXT)";
        sqLiteDatabase.execSQL(sql);
    }

    public void createOrOpenDatabase() {

        db = getWritableDatabase();
        // Log.d("test", "create DB");

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
