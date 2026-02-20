import com.healthmarketscience.jackcess.*;
import java.io.File;

public class CreateDatabase {
    public static void main(String[] args) throws Exception {

        // Define the database file path
        File dbFile = new File("C:/Databases/VUE_Exhibition.accdb");

        // Create parent directory if it doesn't exist
        dbFile.getParentFile().mkdirs();

        // Create the database and table
        Database db = DatabaseBuilder.create(Database.FileFormat.V2016, dbFile);

        Table table = new TableBuilder("Participants")
            .addColumn(new ColumnBuilder("RegistrationID", DataType.TEXT).setLengthInUnits(20))
            .addColumn(new ColumnBuilder("StudentName",    DataType.TEXT).setLengthInUnits(100))
            .addColumn(new ColumnBuilder("Faculty",        DataType.TEXT).setLengthInUnits(100))
            .addColumn(new ColumnBuilder("ProjectTitle",   DataType.TEXT).setLengthInUnits(200))
            .addColumn(new ColumnBuilder("ContactNumber",  DataType.TEXT).setLengthInUnits(20))
            .addColumn(new ColumnBuilder("EmailAddress",   DataType.TEXT).setLengthInUnits(100))
            .toTable(db);

        db.close();
        System.out.println("Database created successfully at: " + dbFile.getAbsolutePath());
    }
}