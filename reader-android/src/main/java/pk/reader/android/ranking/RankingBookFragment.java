package pk.reader.android.ranking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import pk.reader.Commons.NetworkUtils;
import pk.reader.Commons.ToastUtils;
import pk.reader.WebCrawler.WebCrawler;
import pk.reader.android.R;
import pk.reader.android.commons.PriceAdapter;
import pk.reader.android.dao.DBHelper;
import pk.reader.android.dao.db.BookDao;
import pk.reader.android.utils.ControlsUtils;
import pk.reader.android.utils.ListViewUtils;
import pk.reader.entities.Book;
import pk.reader.entities.Price;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class RankingBookFragment extends RoboFragment {

    @InjectView(R.id.RankingFragmentTitle)
    TextView fragmentTitle;
    @InjectView(R.id.RankingFragmentAuthor)
    TextView fragmentAuthor;
    @InjectView(R.id.RankingFragmentISBN)
    TextView fragmentISBN;
    @InjectView(R.id.RankingFragmentRating)
    TextView fragmentRating;
    @InjectView(R.id.RankingFragmentDescription)
    TextView fragmentDescription;
    @InjectView(R.id.RankingFragmentImage)
    ImageView fragmentImage;
    @InjectView(R.id.RankingFragmentFavourite)
    Button fragmentFavourite;

    @InjectView(R.id.RankingFragmentLayout)
    LinearLayout fragmentLayout;
    @InjectView(R.id.RankingFragmentEmptyView)
    RelativeLayout fragmentRelativeLayout;

    @InjectView(R.id.RankingFragmentPriceList)
    ListView fragmentPriceList;
    @InjectView(R.id.RankingFragmentPriceListDesc)
    TextView fragmentPriceListDesc;

    private Context context;
    private List<Book> shortDescriptionList;
    private List<Book> longDescriptionList = new ArrayList<Book>();
    private BookDao bookDao = DBHelper.getDaoSession().getBookDao();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ranking_book_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        RoboGuice.getInjector(context).injectViewMembers(this);
        Book book = getArguments().getParcelable("Book");
        shortDescriptionList = getArguments().getParcelableArrayList("Books");
        boolean isFavourite = getArguments().getBoolean("IsFavourite");

        if(isFavourite) {
            for (Book b : shortDescriptionList) {
                if (b.getISBN().equals(book.getISBN()))
                    setControls(b);
            }
        } else {

            if (!hasURL(book.getURL()))
                if (!NetworkUtils.isNetworkAvailable(context))
                    ToastUtils.ShortToast(context, getString(R.string.noConnection));
                else
                    new DownloadBook(book.getURL()).execute();
            else {
                if (longDescriptionList.size() > 0) {
                    Book longDesc = getBookByURL(book.getURL());
                    setControls(longDesc);
                }
            }
        }
    }

    private void setControls(final Book book){
        fragmentLayout.setVisibility(View.VISIBLE);
        fragmentRelativeLayout.setVisibility(View.GONE);
        fragmentTitle.setText(book.getTitle());

        if(book.getISBN() == null)
            fragmentISBN.setText(getString(R.string.isbn) + ": " + getString(R.string.empty));
        else
            fragmentISBN.setText(getString(R.string.isbn) + ": " + book.getISBN());

        if(book.getAuthor() == null)
            fragmentAuthor.setText(getString(R.string.unknownAuthor));
        else
            fragmentAuthor.setText(book.getAuthor());
        ControlsUtils.setRatingValue(context, book, fragmentRating);
        fragmentDescription.setText(book.getLongDescription());

        UrlImageViewHelper.setUrlDrawable(fragmentImage, book.getImageLink(), null, UrlImageViewHelper.CACHE_DURATION_INFINITE);

        ArrayList<Price> Prices = book.getPrices();

        if(Prices != null) {
            if(Prices.size() > 0) {
                fragmentPriceListDesc.setText(getString(R.string.pricesInBookstories));
                fragmentPriceList.setAdapter(new PriceAdapter(getActivity(), Prices));
                ListViewUtils.setListViewHeightBasedOnChildren(fragmentPriceList);
            } else {
                fragmentPriceListDesc.setText(getString(R.string.priceNotFound));
            }
        } else {
            fragmentPriceListDesc.setText(getString(R.string.priceNotFound));
        }

        if(bookDao.load(book.getISBN()) == null) {
            fragmentFavourite.setText(getString(R.string.addFavourites));
            fragmentFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_empty_icon, 0, 0, 0);
        } else {
            fragmentFavourite.setText(getString(R.string.removeFavourites));
            fragmentFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_fill_icon, 0, 0, 0);
        }

        fragmentFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookDao.load(book.getISBN()) == null) {
                    Book insertBook = Book.copyBook(book);
                    bookDao.insert(insertBook);
                    fragmentFavourite.setText(getString(R.string.removeFavourites));
                    fragmentFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_fill_icon, 0, 0, 0);
                } else {
                    bookDao.deleteByKey(book.getISBN());
                    fragmentFavourite.setText(getString(R.string.addFavourites));
                    fragmentFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_empty_icon, 0, 0, 0);
                }
            }
        });

    }
    private boolean hasURL(String URL){
        for(Book b : longDescriptionList){
            if(b.getURL().equals(URL))
                return true;
        }
        return false;
    }

    private Book getBookByURL(String URL){
        for(Book b : longDescriptionList){
            if(b.getURL().equals(URL))
                return b;
        }
        return null;
    }

    public static RankingBookFragment newInstance(List<Book> shortDescriptionList, Book book, boolean isFavourite){

        RankingBookFragment fragment = new RankingBookFragment();

        Bundle b = new Bundle();
        b.putParcelable("Book", book);
        b.putParcelableArrayList("Books", (ArrayList<Book>) shortDescriptionList);
        b.putBoolean("IsFavourite", isFavourite);

        fragment.setArguments(b);

        return fragment;
    }

    private class DownloadBook extends RoboAsyncTask<Integer> {

        private WebCrawler webCrawler = new WebCrawler();
        private ProgressDialog dialog;
        private String URL;

        protected DownloadBook(String URL) {
            super(RankingBookFragment.this.context);
            this.URL = URL;
            dialog = new ProgressDialog(context);
        }

        @Override
        public Integer call() throws Exception {
            webCrawler.setShortDescriptionList(shortDescriptionList);
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
            if (result == 0) {
                Book book = webCrawler.getCurrentBook();

                if(!longDescriptionList.contains(book))
                    longDescriptionList.add(book);
                setControls(book);
            } else {
                fragmentLayout.setVisibility(View.GONE);
                fragmentRelativeLayout.setVisibility(View.VISIBLE);
                ToastUtils.ShortToast(context, getString(R.string.waitingTimeExceeded));
            }
        }

        @Override
        protected void onFinally() {
            dialog.dismiss();
        }
    }
}
