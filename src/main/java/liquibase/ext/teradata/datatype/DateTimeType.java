package liquibase.ext.teradata.datatype;

import liquibase.database.Database;
import liquibase.datatype.DatabaseDataType;
import liquibase.ext.teradata.database.TeradataDatabase;

public class DateTimeType extends liquibase.datatype.core.DateTimeType {

    @Override
    public boolean supports(Database database) {
        return database instanceof TeradataDatabase;
    }

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

    @Override
    public DatabaseDataType toDatabaseDataType(Database database) {
        return new DatabaseDataType("TIMESTAMP");
    }
}
