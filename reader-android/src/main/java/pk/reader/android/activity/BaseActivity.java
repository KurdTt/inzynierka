package pk.reader.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import roboguice.RoboGuice;
import roboguice.activity.RoboActionBarActivity;

/**
 * @author Przemysław Książek
 * Every activity must be extended by this one.
 * It contains Appcompat ActionBar with RoboGuice.
 */
public class BaseActivity extends RoboActionBarActivity {

    private boolean finishOnUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RoboGuice.setUseAnnotationDatabases(false);
        super.onCreate(savedInstanceState);
    }

    /**
     * Set menu icon behavior depending on state
     * @param finishOnUp Set it on true if you want to close activity by action bar icon
     */
    protected void setFinishOnUp(boolean finishOnUp) {
        this.finishOnUp = finishOnUp;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(finishOnUp);
    }

    /**
     * Opens a new activity
     * @param c Class of activity to open
     */
    protected void openActivity(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (finishOnUp) {
            this.finish();
            return true;
        } else {
            return super.onSupportNavigateUp();
        }
    }

}
