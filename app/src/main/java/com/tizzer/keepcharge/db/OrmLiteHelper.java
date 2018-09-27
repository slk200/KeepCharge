package com.tizzer.keepcharge.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.entity.Bill;
import com.tizzer.keepcharge.entity.Store;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrmLiteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "OrmLiteHelper";
    private static OrmLiteHelper ormLiteHelper;
    private static Dao<Store, Integer> storeDao;
    private static Dao<Bill, Integer> billDao;

    private OrmLiteHelper(Context context) {
        super(context, ConstantsValue.DB_NAME, null, ConstantsValue.DB_VERSION);
    }

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

    /**
     * 新增店铺
     *
     * @param name
     * @return
     */
    public int saveStore(String name) {
        try {
            List<Store> stores = storeDao.queryForEq("name", name);
            if (stores != null && !stores.isEmpty()) {
                return 0;
            } else {
                return storeDao.create(new Store(name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取店铺id
     *
     * @param storeName
     * @return
     */
    public int getStoreId(String storeName) {
        try {
            List<Store> store = storeDao.queryForEq("name", storeName);
            return store.get(0).getId();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取所有店铺昨日收支概况
     *
     * @return
     */
    public List<StoreBean> getAllStoreBean() {
        List<StoreBean> storeBeans = new ArrayList<>();
        /**
         * 获取当前日期的前一天
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //当前日期减一天
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String date = dateFormat.format(calendar.getTime());
        try {
            List<Store> stores = storeDao.queryForAll();
            for (Store store : stores) {
                GenericRawResults<String[]> strings1 = billDao.queryRaw("select sum(money) from tb_bill where sid=? and type=1 and date between ? and ?",
                        String.valueOf(store.getId()), date + " 00:00:00", date + " 23:59:59");
                String[] firstResult1 = strings1.getFirstResult();
                double money1 = firstResult1[0] == null ? 0 : Double.valueOf(firstResult1[0]);
                GenericRawResults<String[]> strings2 = billDao.queryRaw("select sum(money) from tb_bill where sid=? and type=0 and date between ? and ?",
                        String.valueOf(store.getId()), date + " 00:00:00", date + " 23:59:59");
                String[] firstResult2 = strings2.getFirstResult();
                double money2 = firstResult2[0] == null ? 0 : Double.valueOf(firstResult2[0]);
                storeBeans.add(new StoreBean(store.getId(), store.getName(), money1, money2, 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storeBeans;
    }

    /**
     * 获取店铺的账单列表
     *
     * @param sid
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    public List<BillBean> getBills(int sid, int rangeStart, int rangeEnd) {
        List<BillBean> billBeans = new ArrayList<>();
        try {
            GenericRawResults<String[]> strings = billDao.queryRaw("select id,money,note,date,type from tb_bill where sid=? order by date desc limit ?,?",
                    String.valueOf(sid), String.valueOf(rangeStart), String.valueOf(rangeEnd));
            List<String[]> results = strings.getResults();
            for (String[] element : results) {
                BillBean billBean = new BillBean();
                billBean.setId(Integer.parseInt(element[0]));
                billBean.setMoney(Double.parseDouble(element[1]));
                billBean.setNote(element[2]);
                billBean.setTime(element[3]);
                billBean.setType(element[4].equals("1"));
                billBeans.add(billBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return billBeans;
    }

    /**
     * 录入新帐单
     *
     * @param bill
     * @return
     */
    public int recordBill(Bill bill) {
        try {
            return billDao.create(bill);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 修改账单金额
     *
     * @param id
     * @param money
     * @return
     */
    public int updateBillMoney(int id, String money) {
        try {
            return billDao.updateRaw("update tb_bill set money=? where id=?", money, String.valueOf(id));
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 修改账单金额
     *
     * @param id
     * @param type
     * @return
     */
    public int updateBillType(int id, boolean type) {
        try {
            return billDao.updateRaw("update tb_bill set type=? where id=?", type ? "1" : "0", String.valueOf(id));
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取所有店铺
     *
     * @return
     */
    public List<Store> getAllStoreEntity() {
        List<Store> stores = new ArrayList<>();
        try {
            stores = storeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stores;
    }

    /**
     * 获取相应账单
     *
     * @param sid
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> getBills(int sid, String startDate, String endDate) {
        List<String[]> results = new ArrayList<>();
        try {
            GenericRawResults<String[]> strings = billDao.queryRaw("select date,money,note,type from tb_bill where sid=? and date between ? and ?",
                    String.valueOf(sid), startDate, endDate);
            results = strings.getResults();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

}
