package pk.reader.android.ranking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import pk.reader.Commons.NetworkUtils;
import pk.reader.Commons.ToastUtils;
import pk.reader.WebCrawler.WebCrawler;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.commons.BookAdapter;
import pk.reader.entities.Book;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.ranking_list)
public class RankingListActivity extends BaseActivity {

    @InjectView(R.id.RankingList)
    ListView rankingList;
    @InjectView(R.id.rankingListEmptyView)
    private View emptyListView;

    final Context context = RankingListActivity.this;

    private List<Book> shortDescriptionList = new ArrayList<Book>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnUp(true);
        rankingList.setEmptyView(emptyListView);
        SearchBooks();
    }

    private void SearchBooks(){
        if (!NetworkUtils.isNetworkAvailable(context))
            ToastUtils.ShortToast(context, getString(R.string.noConnection));
        else
            //Księgarnia ORBITA
            new DownloadBooks("http://www.ksiegarniaorbita.pl/oferta/Bestsellery,2,!o71").execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ranking_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                SearchBooks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class DownloadBooks extends AsyncTask<String, Void, Integer> {

        private WebCrawler webCrawler = new WebCrawler();
        private ProgressDialog dialog;
        private String url;

        public DownloadBooks(String url) {
            dialog = new ProgressDialog(context);
            this.url = url;
        }

        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(getString(R.string.pleaseWait));
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Integer success) {
            rankingList.setAdapter(null);
            rankingList.setOnItemClickListener(null);

            if (success == 0) {
                shortDescriptionList = webCrawler.getShortDescriptionList();
                final BookAdapter adapter = new BookAdapter(context, shortDescriptionList);
                rankingList.setAdapter(adapter);
                rankingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(context, RankingBookActivity.class);
                        intent.putParcelableArrayListExtra("Books", (ArrayList<Book>) shortDescriptionList);
                        intent.putExtra("BookNumber", shortDescriptionList.get(i).getNumber());
                        intent.putExtra("IsFavourite", false);
                        startActivity(intent);
                    }
                });
            } else {
                ToastUtils.ShortToast(context, getString(R.string.waitingTimeExceeded));
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }

        protected Integer doInBackground(final String... args) {
            return webCrawler.crawl(url);
        }
    }
}
