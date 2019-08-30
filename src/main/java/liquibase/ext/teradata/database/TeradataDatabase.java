/**
 * Copyright 2010 Open Pricer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package liquibase.ext.teradata.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.database.OfflineConnection;
import liquibase.exception.DatabaseException;
import liquibase.executor.ExecutorService;
import liquibase.statement.core.RawSqlStatement;

/**
 * Teradata implementation for liquibase
 *
 */
public class TeradataDatabase extends AbstractJdbcDatabase {

	private String databaseName=null;

	protected String getDatabaseName(){
		if (null==databaseName && getConnection() != null && (!(getConnection() instanceof OfflineConnection))) {
			try {
				databaseName = (String) ExecutorService.getInstance().getExecutor(this).queryForObject(new RawSqlStatement("SELECT DATABASE"), String.class);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		return databaseName;
	}

	@Override
	public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
		return "Teradata".equals(conn.getDatabaseProductName());
	}

	@Override
	public String getDefaultDriver(String url) {
		if (url.startsWith("jdbc:teradata:"))
			return "com.teradata.jdbc.TeraDriver";
		else
			return null;
	}

    @Override
    public String getShortName() {
        return "teradata";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return "Teradata";
    }

    @Override
    public Integer getDefaultPort() {
        return 1025;
    }

    @Override
	public boolean supportsInitiallyDeferrableColumns() {
		return true;
	}

	@Override
	public String getCurrentDateTimeFunction() {
		return "CURRENT_TIMESTAMP";
	}

	@Override
	public boolean supportsTablespaces() {
		return false;
	}

	@Override
	public int getPriority() {
		return PRIORITY_DATABASE;
	}

	@Override
	public boolean supportsDDLInTransaction() {
		return false;
	}

	@Override
	public String getDefaultCatalogName() {
		return getDatabaseName();
    }

	@Override
	public String getDefaultSchemaName() {
		return getDatabaseName();
	}

	/**
	 * No sequence in Teradata
	 */
	@Override
	public boolean supportsSequences() {
		return false;
	}

	/**
	 * Most frequent reserved keywords (full list in "Fundamentals" manual)
	 */
	@Override
	public boolean isReservedWord(String string) {
		boolean reserved =false;
		reserved = reserved || "VALUE".equalsIgnoreCase(string);
		reserved = reserved || "PASSWORD".equalsIgnoreCase(string);
		reserved = reserved || "TITLE".equalsIgnoreCase(string);
		reserved = reserved || "ENABLED".equalsIgnoreCase(string);
		reserved = reserved || "RANK".equalsIgnoreCase(string);
		reserved = reserved || "POSITION".equalsIgnoreCase(string);
		reserved = reserved || "YEAR".equalsIgnoreCase(string);
		reserved = reserved || "ACCOUNT".equalsIgnoreCase(string);
		return reserved;
	}

	/**
	 * Use JDBC escape syntax
	 */
	@Override
	public String getDateTimeLiteral(Timestamp date) {
		return "'"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date)+"'";
	}

	/**
	 * Use JDBC escape syntax
	 */
	@Override
	public String getDateLiteral(Date date) {
		return "'"+new SimpleDateFormat("yyyy-MM-dd").format(date)+"'";
	}

	/**
	 * Use JDBC escape syntax
	 */
	@Override
	public String getTimeLiteral(Time date) {
		return "'"+new SimpleDateFormat("hh:mm:ss.SSS").format(date)+"'";
	}




}
