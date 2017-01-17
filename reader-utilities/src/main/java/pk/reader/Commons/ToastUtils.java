package pk.reader.Commons;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Przemys³aw Ksi¹¿ek
 * Class that contains functions associated with toasts.
 */
public class ToastUtils {
    /**
     * Shows short toast.
     * @param ctx Application context
     * @param Text Text to show up
     */
    public static void ShortToast(Context ctx, String Text){
        Toast.makeText(ctx, Text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows long toast.
     * @param ctx Application context
     * @param Text Text to show up
     */
    public static void LongToast(Context ctx, String Text){
        Toast.makeText(ctx, Text, Toast.LENGTH_LONG).show();
    }
}
