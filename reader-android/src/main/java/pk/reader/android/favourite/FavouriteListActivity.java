package pk.reader.android.favourite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.commons.BookAdapter;
import pk.reader.android.dao.DBHelper;
import pk.reader.android.dao.db.BookDao;
import pk.reader.android.ranking.RankingBookActivity;
import pk.reader.entities.Book;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.favourite_list)
public class FavouriteListActivity extends BaseActivity {

    @InjectView(R.id.FavouriteList)
    ListView favouriteList;
    @InjectView(R.id.favouriteListEmptyView)
    RelativeLayout emptyViewList;

    private BookAdapter adapter;
    private List<Book> books;
    private BookDao bookDao = DBHelper.getDaoSession().getBookDao();
    private final Context context = FavouriteListActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnUp(true);

        if(bookDao.loadAll().size() == 0) {
            favouriteList.setEmptyView(emptyViewList);
        } else {
            books = bookDao.loadAll();
            adapter = new BookAdapter(context, books);
            favouriteList.setAdapter(adapter);

            for(int i = 0; i < books.size() ; i++)
                books.get(i).setNumber(i+1);

            favouriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(FavouriteListActivity.this, RankingBookActivity.class);
                    intent.putParcelableArrayListExtra("Books", (ArrayList<Book>) books);
                    intent.putExtra("BookNumber", books.get(i).getNumber());
                    intent.putExtra("IsFavourite", true);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            books = bookDao.loadAll();

            for(int i = 0; i < books.size() ; i++)
                books.get(i).setNumber(i+1);

            favouriteList.setAdapter(new BookAdapter(context, books));
        } else {
            favouriteList.setAdapter(null);
            favouriteList.setEmptyView(emptyViewList);
        }
    }
}
