package com.tizzer.keepcharge.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.entity.Bill;
import com.tizzer.keepcharge.entity.Fact;
import com.tizzer.keepcharge.entity.Store;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrmLiteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "OrmLiteHelper";
    private static OrmLiteHelper ormLiteHelper;
    private static Dao<Store, Integer> storeDao;
    private static Dao<Bill, Integer> billDao;
    private static Dao<Fact, Integer> factDao;

    private OrmLiteHelper(Context context) {
        super(context, ConstantsValue.DB_NAME, null, ConstantsValue.DB_VERSION);
    }

    public static OrmLiteHelper getHelper(Context context) {
        if (ormLiteHelper == null) {
            try {
                ormLiteHelper = new OrmLiteHelper(context);
                storeDao = ormLiteHelper.getDao(Store.class);
                billDao = ormLiteHelper.getDao(Bill.class);
                factDao = ormLiteHelper.getDao(Fact.class);
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
            TableUtils.createTable(connectionSource, Fact.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Store.class, true);
            TableUtils.dropTable(connectionSource, Bill.class, true);
            TableUtils.dropTable(connectionSource, Fact.class, true);
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
                int id = storeDao.create(new Store(name));
                saveFact(id, 0, 0);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 插入新店铺的收营概况
     *
     * @param sid
     * @param income
     * @param payment
     */
    public void saveFact(int sid, double income, double payment) {
        try {
            factDao.create(new Fact(sid, income, payment));
        } catch (SQLException e) {
            e.printStackTrace();
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
    public List<StoreBean> getSituation() {
        List<StoreBean> storeBeans = new ArrayList<>();
        try {
            List<Store> stores = storeDao.queryForAll();
            Log.e(TAG, "getSituation: " + stores);
            if (stores != null && !stores.isEmpty()) {
                for (Store store : stores) {
                    Fact fact = factDao.queryForId(store.getId());
                    Log.e(TAG, "getSituation: " + fact);
                    storeBeans.add(new StoreBean(store.getId(), store.getName(), fact.getIncome() - fact.getPayment(),
                            fact.getIncome(), fact.getPayment(), 1));
                }
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
     * 修改账单类型
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

    /**
     * 获取某个店铺的收营概况
     *
     * @param id
     * @return
     */
    public Fact getSituation(int id) {
        Fact fact = null;
        try {
            fact = factDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fact;
    }

    /**
     * 修改店铺收营概况（由于录入）
     *
     * @param sid
     * @param type
     * @param money
     */
    public void updateFactByRecord(int sid, boolean type, String money) {
        try {
            if (type) {
                factDao.updateRaw("update tb_fact set income=income+? where id=?", money, String.valueOf(sid));
            } else {
                factDao.updateRaw("update tb_fact set payment=payment+? where id=?", money, String.valueOf(sid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改店铺的收营概况（由于修改金额）
     */
    public void updateFactByMoney(int sid, boolean type, String dValue) {
        try {
            if (type) {
                factDao.updateRaw("update tb_fact set income=income+? where id=?", dValue, String.valueOf(sid));
            } else {
                factDao.updateRaw("update tb_fact set payment=payment+? where id=?", dValue, String.valueOf(sid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改店铺的收营概况（由于修改类型）
     */
    public void updateFactByType(int sid, boolean type, String money) {
        try {
            if (type) {
                factDao.updateRaw("update tb_fact set income=income+?,payment=payment-? where id=?", money, money, String.valueOf(sid));
            } else {
                factDao.updateRaw("update tb_fact set income=income-?,payment=payment+? where id=?", money, money, String.valueOf(sid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
