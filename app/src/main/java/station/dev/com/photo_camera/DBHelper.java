package station.dev.com.photo_camera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Develop on 5/24/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TzCam.db";
    public static final String IMG_TABLE_NAME = "Img_Path_List";
    public static final String IMG_TB_COL_ID="id";
    public static final String IMG_TB_COL_PATH= "img_path";
    public static final String IMG_TB_COL_GRUOPID= "grp_id";

    public static final String GROUP_ID_TABLE_NAME = "Group_Id";
    public static final String GROUP_COL_ID = "id";
    public static final String GROUP_COL_GROUPID = "grp_id";
    public static final String GROUP_COL_FTPDIR = "ftp_dir";

    public static final String TB_PHOTO_TIME = "Photo_Time";
    public static  final String TB_COL_ID = "id";
    public static  final String TB_COL_PHOTOS= "count";
    public static  final String TB_COL_date = "`date`";
    public static  final String TB_COL_TIME = "`time`";



    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "PocImage" + File.separator + DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table Img_Path_List " +
                        "(id integer primary key, img_path text,grp_id integer)"
        );

        db.execSQL("create table Group_Id " +
                "(id integer primary key, grp_id integer,ftp_dir text)");
        db.execSQL("create table Photo_Time " +
                "(id integer primary key, count integer,`date` text, `time` text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Img_Path_List");
        db.execSQL("DROP TABLE IF EXISTS Group_Id");
        db.execSQL("DROP TABLE IF EXISTS Photo_Time");
        onCreate(db);
    }

    public boolean insertPhotoInfo(int count, String date, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TB_COL_PHOTOS, count);
        contentValues.put(TB_COL_date, date);
        contentValues.put(TB_COL_TIME, time);
        db.insert("Img_Path_List", null, contentValues);

        return  true;
    }

    public boolean insertPath  (String img_path, int grp_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("img_path", img_path);
        contentValues.put("grp_id", grp_id);
        db.insert("Img_Path_List", null, contentValues);
        return true;
    }

    public boolean insertGroup(int grp_id, String ftp_dir){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("grp_id", grp_id);
        contentValues.put("ftp_dir", ftp_dir);
        db.insert("Group_Id" , null, contentValues);
        return true;
    }

    public Cursor getImgPathData(int grp_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Img_Path_List where grp_id="+grp_id+"", null );
        return res;
    }

    public ArrayList<String> getImgPathList(int grp_id){
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Img_Path_List where grp_id = "+grp_id, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(IMG_TB_COL_PATH)));
            res.moveToNext();
        }
        return array_list;
    }

    public Cursor getGroupData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Group_Id", null );
        return res;
    }



    public Integer deleteImgPath (Integer grp_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Img_Path_List",
                "grp_id = ? ",
                new String[] { Integer.toString(grp_id) });
    }

    public Integer deleteGroup(Integer grp_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Group_Id",
                "grp_id = ? ",
                new String[] { Integer.toString(grp_id) });
    }

/*
    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public ArrayList<String> getAllCotacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
String[] projection = {
    FeedEntry._ID,
    FeedEntry.COLUMN_NAME_TITLE,
    FeedEntry.COLUMN_NAME_UPDATED,
    ...
    };

// How you want the results sorted in the resulting Cursor
String sortOrder =
    FeedEntry.COLUMN_NAME_UPDATED + " DESC";

Cursor c = db.query(
    FeedEntry.TABLE_NAME,  // The table to query
    projection,                               // The columns to return
    selection,                                // The columns for the WHERE clause
    selectionArgs,                            // The values for the WHERE clause
    null,                                     // don't group the rows
    null,                                     // don't filter by row groups
    sortOrder                                 // The sort order
    );

To look at a row in the cursor, use one of the Cursor move methods, which you must always call before you begin reading values. Generally, you should start by calling moveToFirst(), which places the "read position" on the first entry in the results. For each row, you can read a column's value by calling one of the Cursor get methods, such as getString() or getLong(). For each of the get methods, you must pass the index position of the column you desire, which you can get by calling getColumnIndex() or getColumnIndexOrThrow(). For example:

cursor.moveToFirst();
long itemId = cursor.getLong(
    cursor.getColumnIndexOrThrow(FeedEntry._ID)
);
    */
}