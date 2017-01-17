package pk.reader.android.search.title;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.commons.BookAdapter;
import pk.reader.android.ranking.RankingBookActivity;
import pk.reader.entities.Book;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.ranking_list)
public class SearchListActivity extends BaseActivity {

    @InjectView(R.id.RankingList)
    ListView rankingList;

    private List<Book> shortDescriptionList = new ArrayList<Book>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnUp(true);

        Bundle bundle = getIntent().getExtras();
        shortDescriptionList = bundle.getParcelableArrayList("Books");
        rankingList.setAdapter(new BookAdapter(getApplicationContext(), shortDescriptionList));

        rankingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchListActivity.this, RankingBookActivity.class);
                intent.putParcelableArrayListExtra("Books", (ArrayList<Book>) shortDescriptionList);
                intent.putExtra("BookNumber", shortDescriptionList.get(i).getNumber());
                intent.putExtra("IsFavourite", false);
                startActivity(intent);
            }
        });
    }
}
