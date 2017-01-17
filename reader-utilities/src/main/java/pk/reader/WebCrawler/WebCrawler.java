package pk.reader.WebCrawler;

import android.text.TextUtils;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pk.reader.entities.Book;
import pk.reader.entities.Price;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Przemys�aw Ksi��ek
 * Retrieves data from a recieved URL.
 * HTML is parsed by JSoup library.
 */
public class WebCrawler {

    private static final int REQUEST_TIME = 15000; //In milliseconds

    /** List of retrieved books */
    private List<Book> ShortDescriptionList = new ArrayList<Book>();
    /** Retrieved single book */
    private Book CurrentBook = new Book();

    //region Getters
    public List<Book> getShortDescriptionList() {
        return ShortDescriptionList;
    }

    public Book getCurrentBook() {
        return CurrentBook;
    }
    //endregion
    //region Setters
    public void setShortDescriptionList(List<Book> shortDescriptionList) {
        ShortDescriptionList = shortDescriptionList;
    }
    //endregion

    public WebCrawler() { }

    /**
     * Crawls (retrieves data from) page by given URL.
     * @param url Link to the page (e.g. http://www.example.com)
     * @return True if success, false if failure
     */
    public int crawl(String url) {

        String pageContent = retrieveHtmlContent(url);

        //FIXING PARSER FOR "Ceneo" PORTAL.
        //It can't read space at the start
        pageContent = pageContent.replaceAll(" js_conv", "js_conv");

        //Check if string is empty or not
        //if string is not empty (has content)
        if (!TextUtils.isEmpty(pageContent)) {
            //Checking patterns for:
            //Parsing whole page
            Document doc = Jsoup.parse(pageContent);
            //region Lubimyczytac.pl
            //First Pattern (List of book)
            if (Pattern.matches("http://lubimyczytac.pl/gfk", url) ||                                        //Match for ranking
                    Pattern.matches("http://lubimyczytac\\.pl/szukaj/ksiazki\\?phrase=[\\p{L}+]*", url)) { //Match for searching

                int CurrentNumber = 1;

                for (Element e : doc.select("li[class=book border-szary border-dotted-top space-10-t spacer-10-b]")) {
                    String Title = e.getElementsByClass("bookTitle").text();
                    String Url = e.getElementsByClass("bookTitle").attr("href");
                    String ShortDescription = e.select("div[class=book-description space-10-t]").text();
                    String ImageLink = e.select("img").attr("src");
                    String Author = "";
                    for (Element el : e.getElementsByAttributeValueMatching("href", Pattern.compile("http://lubimyczytac.pl/autor/*")))
                        Author += el.text() + ", ";
                    Author = Author.substring(0, Author.length() - 2);

                    Book newBook = new Book();
                    newBook.setNumber(CurrentNumber);
                    newBook.setTitle(Title);
                    newBook.setAuthor(Author);
                    newBook.setURL(Url);
                    newBook.setShortDescription(ShortDescription);
                    newBook.setImageLink(ImageLink);

                    ShortDescriptionList.add(newBook);
                    CurrentNumber += 1;

                }
                //If there's one book, just crawl for it's details
                if (ShortDescriptionList.size() == 1) {
                    return crawl(ShortDescriptionList.get(0).getURL());
                }
                //Next pattern (single book)
            } else if (Pattern.matches("http://lubimyczytac.pl/ksiazka/\\d+/[a-zA-Z\\-\\d]*", url)) {

                try {

                    String ratingValue = doc.select("span[itemprop=ratingValue").first().text();
                    String ratingCount = doc.select("span[itemprop=ratingCount").first().text();

                    String desc;
                    try {
                        desc = doc.select("div[id=sBookDescriptionLong").first().text();
                    } catch (NullPointerException e) {
                        desc = doc.select("p[class=description regularText").first().text();
                    }
                    String imageLink = doc.select("div[class=book-cover-size-220x315").select("img").attr("src");


                    for (Book sd : ShortDescriptionList) {
                        if (sd.getURL().equals(url)) {
                            CurrentBook.setNumber(sd.getNumber());
                            CurrentBook.setTitle(sd.getTitle());
                            CurrentBook.setAuthor(sd.getAuthor());
                            CurrentBook.setLongDescription(desc);
                            CurrentBook.setURL(sd.getURL());
                            CurrentBook.setRatingCount(ratingCount);
                            CurrentBook.setRatingValue(ratingValue);
                            CurrentBook.setImageLink(imageLink);
                        }
                    }
                } catch (Exception E) {
                    for (Book sd : ShortDescriptionList) {
                        if (sd.getURL().equals(url)) {
                            CurrentBook.setNumber(sd.getNumber());
                            CurrentBook.setTitle(sd.getTitle());
                            CurrentBook.setAuthor(sd.getAuthor());
                            CurrentBook.setLongDescription("-");
                            CurrentBook.setURL(sd.getURL());
                            CurrentBook.setRatingCount(null);
                            CurrentBook.setRatingValue(null);
                        }
                    }
                }
            }
            //endregion
            //region Ksi�garnia ORBITA
            //First Pattern (List of book)
            if (Pattern.matches("http://www.ksiegarniaorbita.pl/oferta/Bestsellery,2,!o71", url) ||                                        //Match for ranking
                    Pattern.matches("http://www.ksiegarniaorbita.pl/wyszukiwanie\\?pokazWyszukane=tak&tematyka=&tytul=[\\p{L}%\\d+]*&autor=&isbn=&wydawca=&jezyk=&grupa=&opis=&x=0&y=0", url) ||
                    Pattern.matches("http://www.ksiegarniaorbita.pl/wyszukiwanie\\?pokazWyszukane=tak&tematyka=&tytul=&autor=&isbn=\\d{13}&wydawca=&jezyk=&grupa=&opis=&x=0&y=0", url)) { //Match for searching

                int CurrentNumber = 1;

                final String PageName = "http://www.ksiegarniaorbita.pl";

                List<String> shortDescriptions = new ArrayList<String>();

                if(doc.select("tr[class=wyniki-row-2]").first() == null){
                    return 2;
                } else {

                    for (Element element : doc.select("tr[class=wyniki-row-2]")) {
                        for (Element e : element.select("td")) {
                            shortDescriptions.add(e.select("p[class=opis-list").text());
                        }
                    }

                    for (Element element : doc.select("tr[class=wyniki-row-1]")) {
                        for (Element e : element.select("td")) {
                            String Title = e.getElementsByClass("sTytul").text();
                            String Url = PageName + e.getElementsByClass("sTytul").attr("href");

                            //Exclude all fake books
                            if(Url.contains("artykul"))
                                continue;

                            String ListImage = PageName + e.select("img").attr("src");
                            String Author = "";
                            try {
                                for (Element el : e.getElementsByAttributeValueMatching("href", Pattern.compile("/autor/*")))
                                    Author += el.text() + ", ";
                                Author = Author.substring(0, Author.length() - 2);
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }

                            Book newBook = new Book();
                            newBook.setNumber(CurrentNumber);
                            newBook.setTitle(Title);
                            newBook.setAuthor(Author);
                            newBook.setURL(Url);
                            newBook.setShortDescription(shortDescriptions.get(CurrentNumber - 1));
                            newBook.setListImage(ListImage);

                            ShortDescriptionList.add(newBook);
                            CurrentNumber += 1;
                        }
                    }
                    //If there's one book, just crawl for it's details
                    if (ShortDescriptionList.size() == 1) {
                        return crawl(ShortDescriptionList.get(0).getURL());
                    }
                }
                //Next pattern (single book)
            } else if (Pattern.matches("http://www.ksiegarniaorbita.pl/ksiazka/[\\p{L}%\\d,+-]*;jsessionid=[\\p{L}\\d.]*", url) ||
                    Pattern.matches("http://www.ksiegarniaorbita.pl/ksiazka/[\\p{L}-]*/[\\p{L}%\\d,+-]*;jsessionid=[\\p{L}\\d.]*", url) ||
                    Pattern.matches("http://www.ksiegarniaorbita.pl/artykul/[\\p{L}-]*/[\\p{L}%\\d,+-]*;jsessionid=[\\p{L}\\d.]*", url)) {

                final String PageName = "http://www.ksiegarniaorbita.pl";

                try {

                    Element e = doc.select("div[class=center-area").first();
                    String ISBN = e.select("span[class=sksiazki]").text();

                    ISBN = checkISBN(ISBN);

                    String desc = e.select("p[class=book-desc").first().text();
                    String imageLink = PageName + e.select("img").attr("src");

                    for (Book sd : ShortDescriptionList) {
                        if (sd.getURL().equals(url)) {
                            CurrentBook.setNumber(sd.getNumber());
                            CurrentBook.setISBN(ISBN);
                            CurrentBook.setTitle(sd.getTitle());
                            CurrentBook.setAuthor(sd.getAuthor());
                            CurrentBook.setShortDescription(sd.getShortDescription());
                            CurrentBook.setLongDescription(desc);
                            CurrentBook.setURL(sd.getURL());
                            CurrentBook.setRatingCount(null);
                            CurrentBook.setRatingValue(null);
                            CurrentBook.setListImage(sd.getListImage());
                            CurrentBook.setImageLink(imageLink);
                        }
                    }

                    if (ISBN != null)
                        return crawl("http://www.ceneo.pl/Ksiazki;szukaj-" + ISBN.replaceAll("-", ""));
                } catch (Exception E) {
                    for (Book sd : ShortDescriptionList) {
                        if (sd.getURL().equals(url)) {
                            CurrentBook.setNumber(sd.getNumber());
                            CurrentBook.setISBN(null);
                            CurrentBook.setTitle(sd.getTitle());
                            CurrentBook.setAuthor(sd.getAuthor());
                            CurrentBook.setLongDescription("-");
                            CurrentBook.setURL(sd.getURL());
                            CurrentBook.setRatingCount(null);
                            CurrentBook.setRatingValue(null);
                        }
                    }
                }
                //Getting books prices
            }
            //endregion
            //region CENEO
            else if (Pattern.matches("http://www.ceneo.pl/Ksiazki;szukaj-\\d{13}", url)) {
                final String ShopURL = "http://www.ceneo.pl";

                if(doc.select("div[class=alert alert-block alert-message fade in message-box]").first() == null) {

                    //Getting link with prices
                    String linkToBook = ShopURL + doc.select("a[class=js_conv]").first().attr("href");
                    //Getting content
                    String pageWithPricesContent = retrieveHtmlContent(linkToBook);

                    if (!TextUtils.isEmpty(pageWithPricesContent)) {
                        //if it's not empty

                        /* List of book prices */
                        ArrayList<Price> bookPrices = new ArrayList<Price>();

                        Document docWithPrices = Jsoup.parse(pageWithPricesContent);
                        try {
                            Element lowPrices = docWithPrices.select("section[class=product-offers-group]").get(1);

                            List<Element> priceTable = lowPrices.select("tr[class=product-offer js_product-offer]");

                            for (int i = 0; i < 3; i++) {
                                Element priceRow = priceTable.get(i);

                                Price price = new Price();

                                price.setPrice(priceRow.select("a[class=product-price go-to-shop]").first().text());

                                if (!TextUtils.isEmpty(priceRow.select("a[class=btn btn-primary btn-m btn-cta go-to-shop]").attr("href")))
                                    price.setLinkToBook(ShopURL + priceRow.select("a[class=btn btn-primary btn-m btn-cta go-to-shop]").attr("href"));
                                else
                                    price.setLinkToBook(ShopURL + priceRow.select("a[class=btn btn-primary btn-small btn-cta go-to-shop]").attr("href"));

                                price.setBookstoreImageURL("http:" + priceRow.select("img[class=js_lazy]").attr("data-original"));

                                bookPrices.add(price);

                            }

                            CurrentBook.setPrices(bookPrices);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    } else {
                        //if is empty
                        return 2;
                    }
                }
            }
            //endregion
            return 0;
        }
        //if is empty
        else{
            return 1;
        }
    }

    private String checkISBN(String ISBN){
        Pattern pattern = Pattern.compile("(\\d{3}-\\d{2}-\\d{3}-\\d{4}-\\d)");
        Matcher matcher = pattern.matcher(ISBN);
        if (matcher.find()) {
            ISBN = matcher.group(1);
        } else {
            Pattern pattern2 = Pattern.compile("(\\d{3}-\\d{2}-\\d{2}-\\d{5}-\\d)");
            Matcher matcher2 = pattern2.matcher(ISBN);
            if (matcher2.find())
                ISBN = matcher2.group(1);
            else {
                Pattern pattern3 = Pattern.compile("(\\d{3}-\\d{2}-\\d{4}-\\d{3}-\\d)");
                Matcher matcher3 = pattern3.matcher(ISBN);
                if(matcher3.find()){
                    ISBN = matcher3.group(1);
                } else {
                    Pattern pattern4 = Pattern.compile("(\\d{3}-\\d{2}-\\d{5}-\\d{2}-\\d)");
                        Matcher matcher4 = pattern4.matcher(ISBN);
                        if(matcher4.find()){
                            ISBN = matcher4.group(1);
                        } else {
                            Pattern pattern5 = Pattern.compile("(\\d{2}-\\d{4}-\\d{3}-\\d)");
                            Matcher matcher5 = pattern5.matcher(ISBN);
                            if(matcher5.find()){
                                ISBN = matcher5.group(1);
                            } else {
                                ISBN = null;
                            }
                    }
                }
            }
        }
        return ISBN;
    }

    /**
     * Retrieves HTML from URL, if connection timeout or read timeout is bigger than
     * REQUEST_TIME, then function returns empty string.
     * @param Url Link to page (e.g. http://www.example.com)
     * @return HTML of web site (success) or empty string (failure)
     */
    private String retrieveHtmlContent(String Url) {

        URL httpUrl = null;

        try {
            httpUrl = new URL(Url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        StringBuilder pageContent = new StringBuilder();
        try {
            //Checking if URL is not null
            if (httpUrl != null) {
                //Opening connection
                HttpURLConnection conn = (HttpURLConnection) httpUrl
                        .openConnection();
                //Setting requests time
                conn.setConnectTimeout(REQUEST_TIME);
                conn.setReadTimeout(REQUEST_TIME);
                //Getting response code
                int responseCode = conn.getResponseCode();
                //If code is not equal OK status, then throw Exception
                if (responseCode != HttpStatus.SC_OK) {
                    throw new IllegalAccessException(
                            "Http connection failed");
                }
                //Code below reads retrieved page and adds it to StringBuffer
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        pageContent.append(line);
                    }
                }
                //If readTimeout is bigger than REQUEST_TIME, return empty string
                catch (SocketTimeoutException e){
                    return "";
                }
            }
        }
        //If any error, return empty string
        catch (Exception e) {
            return "";
        }
        return pageContent.toString();
    }
}
