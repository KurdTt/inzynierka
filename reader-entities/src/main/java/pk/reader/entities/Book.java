package pk.reader.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author Przemys³aw Ksi¹¿ek
 * Entity of a Book.
 */

public  class Book implements Parcelable {
    //region Fields
    /** Number of a book in a list (e.g. ranking book */
    private Integer Number;
    /** First name and last name of an author (or authors) */
    private String Author;
    /** 13 digital identity number of each book */
    private String ISBN;
    /** Title of a book */
    private String Title;
    /** URL of whole book description */
    private String URL;
    /** Short description of book (e.g. in list */
    private String ShortDescription;
    /** Proper description of a book */
    private String LongDescription;
    /** Rating value */
    private String RatingValue;
    /** Votes counter (how many people vote on single book) */
    private String RatingCount;
    /** Link to an image */
    private String ImageLink;
    /** List with prices */
    private ArrayList Prices;
    /** Link to list image */
    private String ListImage;
    //endregion
    //region Constructors
    public Book() { }

    public Book(Integer number, String author, String isbn, String title, String shortDesc, String longDesc) {
        this.Number = number;
        this.Author = author;
        this.ISBN = isbn;
        this.Title = title;
        this.ShortDescription = shortDesc;
        this.LongDescription = longDesc;
    }

    public Book(String author, String ISBN, String title, String shortDescription, String longDescription, String imageLink, String listImage) {
        Author = author;
        this.ISBN = ISBN;
        Title = title;
        ShortDescription = shortDescription;
        LongDescription = longDescription;
        ImageLink = imageLink;
        ListImage = listImage;
    }

    public static Book copyBook (Book book){
        Book b = new Book();
        b.setAuthor(book.getAuthor());
        b.setTitle(book.getTitle());
        b.setISBN(book.getISBN());
        b.setShortDescription(book.getShortDescription());
        b.setLongDescription(book.getLongDescription());
        b.setImageLink(book.getImageLink());
        b.setListImage(book.getListImage());
        return b;
    }

    //endregion
    //region Getters

    public Integer getNumber() {
        return Number;
    }

    public String getAuthor() {
        return Author;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return Title;
    }

    public String getURL() {
        return URL;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public String getLongDescription() {
        return LongDescription;
    }

    public String getRatingValue() {
        return RatingValue;
    }

    public String getFullRatingValue(){
        return RatingValue + " / 10";
    }

    public String getRatingCount() {
        return RatingCount;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public String getListImage() {
        return ListImage;
    }

    public ArrayList<Price> getPrices() {
        return Prices;
    }

    //endregion
    //region Setters

    public void setNumber(Integer number) {
        Number = number;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public void setLongDescription(String longDescription) {
        LongDescription = longDescription;
    }

    public void setRatingValue(String ratingValue) {
        RatingValue = ratingValue;
    }

    public void setRatingCount(String ratingCount) {
        RatingCount = ratingCount;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public void setListImage(String listImage) {
        ListImage = listImage;
    }

    public void setPrices(ArrayList prices) {
        Prices = prices;
    }

    //endregion
    //region Parcels
    //Those methods I need to send class instance(s) to another activity
    protected Book(Parcel in) {
        Number = in.readByte() == 0x00 ? null : in.readInt();
        Author = in.readString();
        ISBN = in.readString();
        Title = in.readString();
        URL = in.readString();
        ShortDescription = in.readString();
        LongDescription = in.readString();
        RatingValue = in.readString();
        RatingCount = in.readString();
        ImageLink = in.readString();
        ListImage = in.readString();
        Prices = new ArrayList<Price>();
        in.readTypedList(Prices, Price.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (Number == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(Number);
        }
        dest.writeString(Author);
        dest.writeString(ISBN);
        dest.writeString(Title);
        dest.writeString(URL);
        dest.writeString(ShortDescription);
        dest.writeString(LongDescription);
        dest.writeString(RatingValue);
        dest.writeString(RatingCount);
        dest.writeString(ImageLink);
        dest.writeString(ListImage);
        dest.writeTypedList(Prices);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    //endregion
}