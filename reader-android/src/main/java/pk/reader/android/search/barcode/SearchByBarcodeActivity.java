package pk.reader.android.search.barcode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pk.reader.Commons.NetworkUtils;
import pk.reader.Commons.ToastUtils;
import pk.reader.WebCrawler.WebCrawler;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.commons.SingleResultActivity;
import roboguice.util.RoboAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SearchByBarcodeActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView  mScannerView;
    private List<BarcodeFormat> barcodeFormats = new ArrayList<BarcodeFormat>();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setFinishOnUp(true);
        //Creating scanner View
        mScannerView = new ZXingScannerView(this);
        //Creating textview with information
        TextView cameraInfo = new TextView(this);
        cameraInfo.setText(getString(R.string.cameraOverBarcode));
        cameraInfo.setTextColor(Color.WHITE);
        cameraInfo.setBackgroundColor(getResources().getColor(R.color.actionBarColor));
        cameraInfo.setGravity(Gravity.CENTER);
        cameraInfo.setTextSize(20);
        cameraInfo.setPadding(0, 10, 0, 10);
        //Setting view
        setContentView(mScannerView);
        addContentView(cameraInfo, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        //Setting scanner format
        barcodeFormats.add(BarcodeFormat.EAN_13);
        mScannerView.setFormats(barcodeFormats);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        final Context context = getApplicationContext();

        if (!NetworkUtils.isNetworkAvailable(context)) {
            ToastUtils.ShortToast(context, getString(R.string.noConnection));
            mScannerView.startCamera();
        } else {
            String URL = "http://www.ksiegarniaorbita.pl/wyszukiwanie?pokazWyszukane=tak&tematyka=&tytul=&autor=&isbn=" + rawResult.getText() + "&wydawca=&jezyk=&grupa=&opis=&x=0&y=0";
            new SearchBook(URL).execute();
        }
    }

    private class SearchBook extends RoboAsyncTask<Integer> {

        private WebCrawler webCrawler = new WebCrawler();
        private ProgressDialog dialog;
        private String URL;

        protected SearchBook(String URL) {
            super(SearchByBarcodeActivity.this);
            this.URL = URL;
            dialog = new ProgressDialog(context);
        }

        @Override
        public Integer call() throws Exception {
            return webCrawler.crawl(URL);
        }

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(getString(R.string.pleaseWait));
            dialog.show();
        }

        @Override
        protected void onSuccess(Integer result) {
            if(result == 0) {
                Intent intent = new Intent(context, SingleResultActivity.class);
                intent.putExtra("Book", webCrawler.getCurrentBook());
                startActivity(intent);
            } else if (result == 1) {
                ToastUtils.ShortToast(context, getString(R.string.waitingTimeExceeded));
                mScannerView.startCamera();
            } else if (result == 2) {
                //todo Zrobić nową aktywność  z pytaniem, czy chcesz skanowac lub wrocic do Menu
                ToastUtils.ShortToast(context, "Nie znaleziono książki o podanym ISBN");
                mScannerView.startCamera();
            }
        }

        @Override
        protected void onFinally() {
            dialog.dismiss();
        }
    }


}
