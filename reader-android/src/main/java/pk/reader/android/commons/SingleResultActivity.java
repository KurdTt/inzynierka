package pk.reader.android.commons;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.utils.ControlsUtils;
import pk.reader.android.utils.ListViewUtils;
import pk.reader.entities.Book;
import pk.reader.entities.Price;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;

@ContentView(R.layout.result_single)
public class SingleResultActivity extends BaseActivity {

    @InjectView(R.id.SingleResultTitle)
    TextView srTitle;
    @InjectView(R.id.SingleResultISBN)
    TextView srISBN;
    @InjectView(R.id.SingleResultAuthor)
    TextView srAuthor;
    @InjectView(R.id.SingleResultRating)
    TextView srRating;
    @InjectView(R.id.SingleResultDescription)
    TextView srDescription;
    @InjectView(R.id.SingleResultImage)
    ImageView srImage;
    @InjectView(R.id.SingleResultPriceListDesc)
    TextView srPriceListDesc;
    @InjectView(R.id.SingleResultPriceList)
    ListView srPriceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnUp(true);

        Bundle bundle = getIntent().getExtras();
        Book book = bundle.getParcelable("Book");
        setControls(book);
    }

    private void setControls(Book book){

        final Context context = SingleResultActivity.this;

        srTitle.setText(book.getTitle());

        if(book.getISBN() == null)
            srISBN.setText(getString(R.string.isbn) + ": " + getString(R.string.empty));
        else
            srISBN.setText(getString(R.string.isbn) + ": " + book.getISBN());

        if(book.getAuthor().isEmpty())
            srAuthor.setText(getString(R.string.unknownAuthor));
        else
            srAuthor.setText(book.getAuthor());


        ControlsUtils.setRatingValue(context, book, srRating);
        srDescription.setText(book.getLongDescription());
        UrlImageViewHelper.setUrlDrawable(srImage, book.getImageLink(), null, UrlImageViewHelper.CACHE_DURATION_INFINITE);

        ArrayList<Price> Prices = book.getPrices();

        if(Prices != null) {
            if(Prices.size() > 0) {
                srPriceListDesc.setText(getString(R.string.pricesInBookstories));
                srPriceList.setAdapter(new PriceAdapter(context, Prices));
                ListViewUtils.setListViewHeightBasedOnChildren(srPriceList);
            } else {
                srPriceListDesc.setText(getString(R.string.priceNotFound));
            }
        } else {
            srPriceListDesc.setText(getString(R.string.priceNotFound));
        }

    }
    
}
