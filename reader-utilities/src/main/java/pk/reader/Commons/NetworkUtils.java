package pk.reader.Commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Przemys³aw Ksi¹¿ek
 * Class that contains functions associated with network.
 */
public class NetworkUtils {
    /**
     * Check if internet connection is open (both Wi-Fi and private network)
     * @param context Application context
     * @return Network state
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
