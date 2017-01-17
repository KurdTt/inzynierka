package pk.reader.android.application;

import android.app.Application;

public class MainApplication extends Application {

    private static MainApplication sInstance;

    public static MainApplication getsInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
