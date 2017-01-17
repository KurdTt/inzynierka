package pk.reader.android.application;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import pk.reader.Commons.ToastUtils;
import pk.reader.android.R;
import pk.reader.android.main.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private boolean serviceState;
    private SharedPreferences preferences;
    private Handler handler = new Handler();
    private Timer mTimer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTimer.scheduleAtFixedRate(new TimeTask(), 0, 5 * 1000);
        return START_NOT_STICKY;
    }

    class TimeTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            handler.post(new Runnable() {

                @Override
                public void run() {

                    serviceState = preferences.getBoolean(MainActivity.SHARED_PREF_SERVICE_STATE, false);

                    if(serviceState)
                        ToastUtils.ShortToast(getApplicationContext(), getApplicationContext().getString(R.string.serviceRunning));
                    }
                }

            );
        }
    }

}
