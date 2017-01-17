package pk.reader.android.dao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class BookFinderDaoGenerator {
    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "pk.reader.android.dao.db");

        Entity book = schema.addEntity("Book");
        book.addStringProperty("Author");
        book.addStringProperty("ISBN").primaryKey();
        book.addStringProperty("Title");
        book.addStringProperty("ShortDescription");
        book.addStringProperty("LongDescription");
        book.addStringProperty("ListImage");
        book.addStringProperty("ImageLink");

        new DaoGenerator().generateAll(schema, "../pd_proj/reader-android/src/main/java");
    }
}
