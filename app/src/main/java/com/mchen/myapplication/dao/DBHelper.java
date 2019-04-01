package com.mchen.myapplication.dao;

import android.content.Context;
import android.database.Cursor;

import com.mchen.myapplication.Times;

import java.util.List;


/**
 * Created by Suzy on 2017/4/10.
 */

public class DBHelper {

    public static final String DB_NAME = "times.db";

    private volatile static DBHelper instance;

    private DaoSession session;
    private TimesDao timesDao;

    private DBHelper(Context context) {
        session = getDaoSession(context);
        timesDao = session.getTimesDao();
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    private DaoMaster getDaoMaster(Context context) {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        return daoMaster;
    }

    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    private DaoSession getDaoSession(Context context) {
        DaoMaster daoMaster = getDaoMaster(context);
        return daoMaster.newSession();
    }

    public void addTimes(Times times) {
        timesDao.insertWithoutSettingPk(times);
    }

    public List<Times> getAllTimes() {
        return timesDao.loadAll();
    }

    public Cursor loadAllCursor() {
        Cursor cursor = timesDao.getDatabase().rawQuery("select * from " + TimesDao.TABLENAME, null);
        return cursor;
    }

    public long getCount() {
        return timesDao.count();
    }

    public void delete() {
        timesDao.deleteAll();
    }
}
