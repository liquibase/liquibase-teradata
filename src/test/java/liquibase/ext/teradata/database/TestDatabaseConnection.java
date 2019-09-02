package liquibase.ext.teradata.database;

import liquibase.database.MockDatabaseConnection;
import liquibase.exception.DatabaseException;

public class TestDatabaseConnection extends MockDatabaseConnection {

	public TestDatabaseConnection(){
		super();
	}

	@Override
	public String getDatabaseProductName() throws DatabaseException {
		return "Teradata";
	}
}
