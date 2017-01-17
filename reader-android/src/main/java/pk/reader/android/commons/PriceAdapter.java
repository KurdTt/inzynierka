package pk.reader.android.commons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import pk.reader.android.R;
import pk.reader.entities.Price;

import java.util.ArrayList;

public class PriceAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Price> priceList;

    public PriceAdapter(Context context, ArrayList<Price> priceList) {
        this.context = context;
        this.priceList = priceList;
    }

    @Override
    public int getCount() {
        return priceList.size();
    }

    @Override
    public Object getItem(int position) {
        return priceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return priceList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        PriceHolder holder = new PriceHolder();

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.price_list_item, parent, false);
            ImageView ivImage = (ImageView) v.findViewById(R.id.priceListBookstoreImage);
            TextView tvPrice = (TextView) v.findViewById(R.id.priceListBookPrice);
            Button bShopLink = (Button) v.findViewById(R.id.priceListGoToShop);
            holder.BookstoreImage = ivImage;
            holder.BookPrice = tvPrice;
            holder.GoToShop = bShopLink;
            v.setTag(holder);
        } else
            holder = (PriceHolder) v.getTag();

        final Price p = priceList.get(position);

        UrlImageViewHelper.setUrlDrawable(holder.BookstoreImage, p.getBookstoreImageURL());

        holder.BookPrice.setText(p.getPrice());

        holder.GoToShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.titleOpenBrowser))
                        .setMessage(context.getString(R.string.questionOpenBrowser))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getLinkToBook()));
                                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(browserIntent);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), null);

                adb.show();
            }
        });

        return v;
    }

    private class PriceHolder{
        ImageView BookstoreImage;
        TextView BookPrice;
        Button GoToShop;
    }

}
