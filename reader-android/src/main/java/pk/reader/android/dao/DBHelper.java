package pk.reader.android.dao;

import android.database.sqlite.SQLiteDatabase;
import pk.reader.android.application.MainApplication;
import pk.reader.android.dao.db.DaoMaster;
import pk.reader.android.dao.db.DaoSession;


public class DBHelper {

    private static final String DATABASE_NAME = "bookFinderDB";
    private final DaoSession daoSession;
    private static DBHelper instance = new DBHelper();

    private DBHelper() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainApplication.getsInstance().getApplicationContext(), DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        this.daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {

        if (instance == null) {
            instance = new DBHelper();
        }
        return instance.daoSession;
    }
}
