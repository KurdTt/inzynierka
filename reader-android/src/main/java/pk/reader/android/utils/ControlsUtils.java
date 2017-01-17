package pk.reader.android.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import pk.reader.android.R;
import pk.reader.entities.Book;

public class ControlsUtils {

    public static void setRatingValue(Context context, Book book, TextView tvRating){
        if(book.getRatingValue() != null) {
            int color;
            Double ratingValue = Double.valueOf(book.getRatingValue().replace(",", "."));

            if (10.0 >= ratingValue && ratingValue > 7.0) {
                color = Color.rgb(69, 139, 0); //Green
            } else if (7.0 >= ratingValue && ratingValue > 4.0) {
                color = Color.rgb(255, 102, 51); //Orange
            } else {
                color = Color.rgb(190, 38, 37); //Red
            }

            String partOne = context.getString(R.string.rating) + ": ";
            String partTwo = book.getFullRatingValue() + "\n" + context.getString(R.string.numberOfVotes) + ": (" + book.getRatingCount() + ")";
            String text = partOne + partTwo;

            Spannable spannable = new SpannableString(text);

            spannable.setSpan(new ForegroundColorSpan(color), partOne.length(), (partOne + book.getFullRatingValue()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvRating.setText(spannable, TextView.BufferType.SPANNABLE);

        } else {
            tvRating.setText(context.getString(R.string.rating) + ": " + context.getString(R.string.empty));
        }
    }

}
