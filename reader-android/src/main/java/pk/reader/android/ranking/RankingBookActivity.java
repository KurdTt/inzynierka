package pk.reader.android.ranking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import pk.reader.android.R;
import pk.reader.android.activity.BaseActivity;
import pk.reader.android.utils.ZoomOutPageTransformer;
import pk.reader.entities.Book;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;

@ContentView(R.layout.ranking_book)
public class RankingBookActivity extends BaseActivity {

    @InjectView(R.id.RankingBookPager)
    ViewPager rankingBookPager;

    private ArrayList<Book> shortDescriptionList = null;
    private int currentPosition;
    private boolean isFavourite;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnUp(true);

        Bundle bundle = getIntent().getExtras();
        shortDescriptionList = bundle.getParcelableArrayList("Books");
        int currentNumber = bundle.getInt("BookNumber");
        isFavourite = bundle.getBoolean("IsFavourite");

        currentPosition = currentNumber - 1;

        rankingBookPager.setAdapter(new RankingBookAdapter(getSupportFragmentManager()));
        rankingBookPager.setCurrentItem(currentPosition);
        rankingBookPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        rankingBookPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private class RankingBookAdapter extends FragmentPagerAdapter {

        public RankingBookAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return shortDescriptionList.size();
        }

        @Override
        public Fragment getItem(int pos){
            return RankingBookFragment.newInstance(shortDescriptionList, shortDescriptionList.get(pos), isFavourite);
        }
    }

}
