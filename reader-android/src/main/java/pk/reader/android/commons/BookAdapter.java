package pk.reader.android.commons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import pk.reader.android.R;
import pk.reader.entities.Book;

import java.util.List;

public class BookAdapter extends BaseAdapter {

    private Context context;
    private List<Book> shortDescriptionList;

    public BookAdapter(Context context, List<Book> shortDescriptionList) {
        this.context = context;
        this.shortDescriptionList = shortDescriptionList;
    }

    @Override
    public int getCount() {
        return shortDescriptionList.size();
    }

    @Override
    public Object getItem(int position) {
        return shortDescriptionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return shortDescriptionList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        BookHolder holder = new BookHolder();

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.ranking_list_item, parent, false);
            TextView tvNumber = (TextView) v.findViewById(R.id.RankingListNumber);
            TextView tvTitle = (TextView) v.findViewById(R.id.RankingListTitle);
            TextView tvAuthor = (TextView) v.findViewById(R.id.RankingListAuthor);
            TextView tvDesc = (TextView) v.findViewById(R.id.RankingListDescription);
            ImageView ivImage = (ImageView) v.findViewById(R.id.RankingListImage);
            holder.bookNumber = tvNumber;
            holder.bookTitle = tvTitle;
            holder.bookAuthor = tvAuthor;
            holder.bookDescription = tvDesc;
            holder.bookImage = ivImage;
            v.setTag(holder);
        } else
            holder = (BookHolder) v.getTag();

        Book b = shortDescriptionList.get(position);

        //Number
        if(b.getNumber() != null)
            holder.bookNumber.setText(String.valueOf(b.getNumber()));

        //Title
        holder.bookTitle.setText(b.getTitle());

        //Author
        if(b.getAuthor().isEmpty())
            holder.bookAuthor.setText(context.getString(R.string.unknownAuthor));
        else
            holder.bookAuthor.setText(b.getAuthor());

        //Description
        if(b.getShortDescription().equals("-"))
            holder.bookDescription.setText(context.getString(R.string.empty));
        else
            holder.bookDescription.setText(b.getShortDescription());

        //Image
        UrlImageViewHelper.setUrlDrawable(holder.bookImage, b.getListImage(), null, UrlImageViewHelper.CACHE_DURATION_INFINITE);

        return v;
    }

    private static class BookHolder {
        public TextView bookNumber;
        public TextView bookTitle;
        public TextView bookAuthor;
        public TextView bookDescription;
        public ImageView bookImage;
    }

}
