package pk.reader.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Price implements Parcelable {

    private String price;
    private String linkToBook;
    private String bookstoreImageURL;

    public Price() { }

    //region Getters&Setters

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLinkToBook() {
        return linkToBook;
    }

    public void setLinkToBook(String linkToBook) {
        this.linkToBook = linkToBook;
    }

    public String getBookstoreImageURL() {
        return bookstoreImageURL;
    }

    public void setBookstoreImageURL(String bookstoreImageURL) {
        this.bookstoreImageURL = bookstoreImageURL;
    }
    //endregion

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.price);
        dest.writeString(this.linkToBook);
        dest.writeString(this.bookstoreImageURL);
    }

    protected Price(Parcel in) {
        this.price = in.readString();
        this.linkToBook = in.readString();
        this.bookstoreImageURL = in.readString();
    }

    public static final Parcelable.Creator<Price> CREATOR = new Parcelable.Creator<Price>() {
        public Price createFromParcel(Parcel source) {
            return new Price(source);
        }

        public Price[] newArray(int size) {
            return new Price[size];
        }
    };
}
