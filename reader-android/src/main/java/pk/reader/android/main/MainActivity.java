package pk.reader.android.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.application.MyReciever;
import pk.reader.android.application.MyService;
import pk.reader.android.favourite.FavouriteListActivity;
import pk.reader.android.ranking.RankingListActivity;
import pk.reader.android.search.barcode.SearchByBarcodeActivity;
import pk.reader.android.search.title.SearchByTitleActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author Przemysław Książek
 * Main activity in whole project. User starts here.
 */

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @InjectView(R.id.BarcodeSearchButton)
    private Button barcodeSearchButton;
    @InjectView(R.id.TitleSearchButton)
    private Button titleSearchButton;
    @InjectView(R.id.FavouriteButton)
    private Button favouriteButton;
    @InjectView(R.id.RankingButton)
    private Button rankingButton;

    public static final String SHARED_PREF_NAME = "pk.reader.app";
    public static final String SHARED_PREF_SERVICE_STATE = "serviceState";

    private SharedPreferences prefs;

    private final IntentFilter filter =
            new IntentFilter(Intent.ACTION_HEADSET_PLUG);

    private MyReciever reciever = new MyReciever();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFinishOnUp(false);

        prefs = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        registerReceiver(reciever, filter);

        startService(new Intent(this, MyService.class));

        barcodeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(SearchByBarcodeActivity.class);
            }
        });
        rankingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openActivity(RankingListActivity.class);
            }
        });
        titleSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(SearchByTitleActivity.class);
            }
        });
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(FavouriteListActivity.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_settings).setChecked(prefs.getBoolean(SHARED_PREF_SERVICE_STATE, false));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_settings){

            if(item.isChecked()){
                item.setChecked(false);
                prefs.edit().putBoolean(SHARED_PREF_SERVICE_STATE, false).apply();
            } else {
                item.setChecked(true);
                prefs.edit().putBoolean(SHARED_PREF_SERVICE_STATE, true).apply();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(reciever);
        super.onDestroy();
    }
}

