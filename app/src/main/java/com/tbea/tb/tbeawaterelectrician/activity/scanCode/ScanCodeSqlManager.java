package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.entity.ScanCode;

import java.util.ArrayList;


/**
 * Created by cy on 2017/2/4.
 */

public class ScanCodeSqlManager {
    private static ScanCodeSqlManager instance;
    private static SQLiteDatabase sqliteDB;
    private static SQLiteHelper databaseHelper;

    public static  long insert(ScanCode obj){
        ContentValues cv =new ContentValues();
        cv.put(ScanCode.Scan_ID, obj.getId());
        cv.put(ScanCode.Name, obj.getName());
        cv.put(ScanCode.Price, obj.getPrice());
        cv.put(ScanCode.Picture, obj.getPicture());
        cv.put(ScanCode.Specification, obj.getSpecification());
        cv.put(ScanCode.Rebatemoney, obj.getRebatemoney());
        cv.put(ScanCode.Scantime, obj.getScantime());
        cv.put(ScanCode.Scanaddress, obj.getScanaddress());
        cv.put(ScanCode.Distributor, obj.getDistributor());
        cv.put(ScanCode.Commodityname, obj.getCommodityname());
        cv.put(ScanCode.Commodityspec, obj.getCommodityspec());
        cv.put(ScanCode.Manufacturedate, obj.getManufacturedate());
        return getInstance().sqliteDB().insertOrThrow(
                SQLiteHelper.TB_NAME,
                null, cv);
    }

    public static  long update(ScanCode obj){
        String where = ScanCode.Scan_ID + " = '" + obj.getId()
                + "'";
        ContentValues cv =new ContentValues();
        cv.put(ScanCode.Scan_ID, obj.getId());
        cv.put(ScanCode.Name, obj.getName());
        cv.put(ScanCode.Price, obj.getPrice());
        cv.put(ScanCode.Picture, obj.getPicture());
        cv.put(ScanCode.Specification, obj.getSpecification());
        cv.put(ScanCode.Rebatemoney, obj.getRebatemoney());
        cv.put(ScanCode.Scantime, obj.getScantime());
        cv.put(ScanCode.Scanaddress, obj.getScanaddress());
        cv.put(ScanCode.Distributor, obj.getDistributor());
        cv.put(ScanCode.Commodityname, obj.getCommodityname());
        cv.put(ScanCode.Commodityspec, obj.getCommodityspec());
        cv.put(ScanCode.Manufacturedate, obj.getManufacturedate());
        return getInstance().sqliteDB().update(SQLiteHelper.TB_NAME,cv,where,null);
    }

    public static  long delect(String id){
        String where = ScanCode.Scan_ID + " = '" + id
                + "'";
        return getInstance().sqliteDB().delete(SQLiteHelper.TB_NAME,where,null);
    }

    public static  long delectAll(){
        return getInstance().sqliteDB().delete(SQLiteHelper.TB_NAME,null,null);
    }

    public static ArrayList<ScanCode> queryAll() {
        ArrayList<ScanCode> al = null;
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(false,
                    SQLiteHelper.TB_NAME, null, null,
                    null, null, null, ScanCode.Scantime + " asc",
                    null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    return null;
                }
                al = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor
                            .getColumnIndex(ScanCode.Scan_ID));
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Name));
                    String price = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Price));
                   String picture = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Picture));
                    String specification = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Specification));
                    String rebatemoney = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Rebatemoney));
                    String scantime = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Scantime));
                    String scanaddress = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Scanaddress));
                    String distributor = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Distributor));
                    String commodityname = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Commodityname));
                    String commodityspec = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Commodityspec));
                    String manufacturedate = cursor.getString(cursor
                            .getColumnIndexOrThrow(ScanCode.Manufacturedate));

                    ScanCode obj = new ScanCode();
                    obj.setId(id);
                    obj.setName(name);
                    obj.setPrice(price);
                    obj.setPicture(picture);
                    obj.setSpecification(specification);
                    obj.setRebatemoney(rebatemoney);
                    obj.setScantime(scantime);
                    obj.setScanaddress(scanaddress);
                    obj.setDistributor(distributor);
                    obj.setCommodityname(commodityname);
                    obj.setCommodityspec(commodityspec);
                    obj.setManufacturedate(manufacturedate);
                    al.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return al;
    }

    public ScanCodeSqlManager() {
        openDatabase(MyApplication.instance, MyApplication.getVersionCode());
    }

    private void openDatabase(Context context, int databaseVersion) {
        databaseHelper = new SQLiteHelper(context,this , databaseVersion);
        System.out.println("已经打开databaseHelper");
        sqliteDB = databaseHelper.getWritableDatabase();
        System.out.println("已经打开sqliteDB");

    }

    public void destroy() {
        try {
            if (databaseHelper != null) {
                databaseHelper.close();
            }
            closeDB();
        } catch (Exception e) {
        }
    }

    private void open(boolean isReadonly) {
        if (sqliteDB == null) {
            if (isReadonly) {
                sqliteDB = databaseHelper.getReadableDatabase();
            } else {
                sqliteDB = databaseHelper.getWritableDatabase();/*DatabaseManager.getInstance().openDatabase()*/;
            }
        }
    }

    public final void reopen() {
        closeDB();
        open(false);
    }

    private void closeDB() {
        if (sqliteDB != null) {
            //DatabaseManager.getInstance().closeDatabase();
            sqliteDB.close();
            sqliteDB = null;
        }
    }

    protected final SQLiteDatabase sqliteDB() {
        open(false);
        return sqliteDB;
    }

    public static ScanCodeSqlManager getInstance() {
        if (instance == null) {
            instance = new ScanCodeSqlManager();
        }
        return instance;
    }

    static class SQLiteHelper extends SQLiteOpenHelper {
        public static final String TB_NAME = "ScanCode";
        /**数据库名称*/
        static final String DATABASE_NAME = "ScanCode.msg.db";


        public SQLiteHelper(Context context, ScanCodeSqlManager manager , String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
            super(context, name, factory, version);
        }

        public SQLiteHelper(Context context, ScanCodeSqlManager manager ,int version) {
            this(context, manager ,DATABASE_NAME, null, version);
        }

        /**
         * 创建新表
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    TB_NAME + "(" +
                    ScanCode.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ScanCode.Scan_ID + " TEXT UNIQUE ON CONFLICT REPLACE," +
                    ScanCode.Name + " varchar," +
                    ScanCode.Price + " varchar," +
                    ScanCode.Picture + " varchar," +
                    ScanCode.Specification + " varchar," +
                    ScanCode.Rebatemoney + " varchar," +
                    ScanCode.Scantime + " varchar," +
                    ScanCode.Scanaddress + " varchar," +
                    ScanCode.Distributor + " varchar," +
                    ScanCode.Commodityname + " varchar," +
                    ScanCode.Commodityspec + " varchar," +
                    ScanCode.Manufacturedate + " varchar" +
                    ")");
        }

        /**
         * 当检测与前一次创建数据库版本不一样时，先删除表再创建新表
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
            onCreate(db);
        }

    }
}
