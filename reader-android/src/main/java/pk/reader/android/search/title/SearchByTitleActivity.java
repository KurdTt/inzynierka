package pk.reader.android.search.title;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import pk.reader.Commons.NetworkUtils;
import pk.reader.Commons.ToastUtils;
import pk.reader.WebCrawler.WebCrawler;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.commons.SingleResultActivity;
import pk.reader.entities.Book;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.search_title)
public class SearchByTitleActivity extends BaseActivity {

    @InjectView(R.id.searchTitleEditText)
    private EditText typedPhrase;
    @InjectView(R.id.searchTitleButton)
    private Button searchButton;
    @InjectView(R.id.searchTitleDeleteText)
    private ImageView deleteButton;

    private List<Book> searchedBooks = new ArrayList<Book>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnUp(true);

        searchButton.setOnClickListener(new OnSearchButtonClicked());
        deleteButton.setOnClickListener(new DeleteListener());
    }

    private class OnSearchButtonClicked implements android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            Context context = getApplicationContext();
            String title = typedPhrase.getText().toString();

            if(title.isEmpty()){
                ToastUtils.ShortToast(context, getString(R.string.noTitle));
            } else if(title.length() <= 2) {
                ToastUtils.ShortToast(context, getString(R.string.titleRequirements));
            } else {

                title = title.replaceAll(" ", "+");

                if(title.charAt(title.length() - 1) == '+')
                    title = title.substring(0, title.length() - 1);

                title = removeDiacriticalMarks(title);

                String queryString = "http://www.ksiegarniaorbita.pl/wyszukiwanie?pokazWyszukane=tak&tematyka=&tytul=" + title + "&autor=&isbn=&wydawca=&jezyk=&grupa=&opis=&x=0&y=0";

                if (!NetworkUtils.isNetworkAvailable(context))
                    ToastUtils.ShortToast(context, getString(R.string.noConnection));
                else
                    new SearchBooks(queryString).execute();
            }
        }
    }

    private class DeleteListener implements android.view.View.OnClickListener{

        @Override
        public void onClick(View v) {
            typedPhrase.setText("");
        }
    }

    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("ł", "l").replaceAll("Ł", "L");
    }

    private class SearchBooks extends RoboAsyncTask<Integer> {

        private WebCrawler webCrawler = new WebCrawler();
        private ProgressDialog dialog;
        private String URL;

        protected SearchBooks(String URL) {
            super(SearchByTitleActivity.this);
            this.URL = URL;
            dialog = new ProgressDialog(context);
        }

        @Override
        public Integer call() throws Exception {
            searchedBooks.clear();
            webCrawler.setShortDescriptionList(searchedBooks);
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
            if(result == 0){
                if(searchedBooks.size() == 1){
                    Book book = webCrawler.getCurrentBook();
                    Intent intent = new Intent(context, SingleResultActivity.class);
                    intent.putExtra("Book", book);
                    startActivity(intent);

                } else if(searchedBooks.size() > 1) {
                    Intent intent = new Intent(context, SearchListActivity.class);
                    intent.putParcelableArrayListExtra("Books", (ArrayList<Book>) searchedBooks);
                    startActivity(intent);
                }
            } else if (result == 2){
                ToastUtils.ShortToast(context, getString(R.string.noResults));
            } else
                ToastUtils.ShortToast(context, getString(R.string.waitingTimeExceeded));
        }

        @Override
        protected void onFinally() {
            dialog.dismiss();
        }
    }
}
