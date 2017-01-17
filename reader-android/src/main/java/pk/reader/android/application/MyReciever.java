package pk.reader.android.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import pk.reader.Commons.ToastUtils;
import pk.reader.android.R;

/**
 * Przykładowy BroadcastReciever
 */


public class MyReciever extends BroadcastReceiver {

    private boolean isFirstTime = true;

    /**
     * Funkcja, która jest uruchamiana przy zdarzeniu (tutaj jest to podłączenie/odłączene słuchawek)
     * @param context - Kontekst aplikacji
     * @param intent - Informacje, które są przekazane do tej klasy
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        //Zapobiegamy, by reciever uruchamiał się od razu po uruchomieniu aplikacji
        if(isFirstTime)
            isFirstTime = false;
        else {
            //Sprawdzamy, jaka akcja ma zostać przechwytywana
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                //Pobieramy stan słuchawek, gdy wystąpi bład - domyślnie wartość ujemna.
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        ToastUtils.ShortToast(context, context.getString(R.string.headsetIsUnplugged));
                        break;
                    case 1:
                        ToastUtils.ShortToast(context, context.getString(R.string.headsetIsPlugged));
                        break;
                    default:
                        ToastUtils.ShortToast(context, context.getString(R.string.recieverBadState));
                }
            }
        }
    }
}
