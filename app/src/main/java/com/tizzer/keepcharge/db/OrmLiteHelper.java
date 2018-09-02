package com.tizzer.keepcharge.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.entity.Bill;
import com.tizzer.keepcharge.entity.Store;

import java.sql.SQLException;

public class OrmLiteHelper extends OrmLiteSqliteOpenHelper {

    private static OrmLiteHelper ormLiteHelper;
    private static Dao<Store, Integer> storeDao;
    private static Dao<Bill, Integer> billDao;

    public static OrmLiteHelper getHelper(Context context) {
        if (ormLiteHelper == null) {
            try {
                ormLiteHelper = new OrmLiteHelper(context);
                storeDao = ormLiteHelper.getDao(Store.class);
                billDao = ormLiteHelper.getDao(Bill.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ormLiteHelper;
    }

    private OrmLiteHelper(Context context) {
        super(context, ConstantsValue.db_name, null, ConstantsValue.db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Store.class);
            TableUtils.createTable(connectionSource, Bill.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Store.class, true);
            TableUtils.dropTable(connectionSource, Bill.class, true);
            onCreate(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveStore(String name) {
        try {
            storeDao.create(new Store(name));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
