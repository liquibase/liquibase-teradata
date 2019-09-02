package liquibase.ext.teradata.database;

import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class TeradataDatabaseTest {

	private TeradataDatabase testDB;
	private Date now;

	@Before
	public void setUp() {
		testDB = new TeradataDatabase();
		now = new Date();
	}

	@Test
	@Ignore
	public void getDatabaseName() {
		assertThat(testDB.getDatabaseName(), CoreMatchers.notNullValue());
	}

	@Test
	public void isCorrectDatabaseImplementation() throws DatabaseException {
		DatabaseConnection con = new TestDatabaseConnection();
		Assert.assertThat(testDB.isCorrectDatabaseImplementation(con), Is.is(true));
	}

	@Test
	public void getDefaultDriver() {
		assertThat(testDB.getDefaultDriver(null), CoreMatchers.nullValue());
		assertThat(testDB.getDefaultDriver("jdbc:oracle:thin"), CoreMatchers.nullValue());
		assertThat(testDB.getDefaultDriver("jdbc:teradata:"), Is.is(IsEqual.equalTo("com.teradata.jdbc.TeraDriver")));
	}

	@Test
	public void getShortName() {
		assertThat( testDB.getShortName(), IsEqual.equalTo("teradata"));
	}

	@Test
	public void getDefaultDatabaseProductName() {
		assertThat( testDB.getDefaultDatabaseProductName(), IsEqual.equalTo("Teradata"));
	}

	@Test
	public void getDefaultPort() {
		assertThat(testDB.getDefaultPort(), IsEqual.equalTo(1025));
	}

	@Test
	public void supportsInitiallyDeferrableColumns() {
		assertThat(testDB.supportsInitiallyDeferrableColumns(), IsEqual.equalTo(true));
	}

	@Test
	public void getCurrentDateTimeFunction() {
		assertThat(testDB.getCurrentDateTimeFunction(), IsEqual.equalTo("CURRENT_TIMESTAMP"));
	}

	@Test
	public void supportsTablespaces() {
		assertThat(testDB.supportsTablespaces(), CoreMatchers.is(false));
	}

	@Test
	public void supportsDDLInTransaction() {
		assertThat(testDB.supportsDDLInTransaction(), CoreMatchers.is(false));
	}

	@Test
	public void getDefaultCatalogName() {
	}

	@Test
	public void getDefaultSchemaName() {
	}

	@Test
	public void supportsSequences() {
		assertThat(testDB.supportsDDLInTransaction(), CoreMatchers.is(false));
	}

	@Test
	public void isReservedWord() {
		Assert.assertTrue(TeradataReservedWord.values().length > 300);
		Assert.assertThat(testDB.isReservedWord("MONTH"), CoreMatchers.is(true));
		Assert.assertThat(testDB.isReservedWord("REPLCONTROL"), CoreMatchers.is(true));
		Assert.assertThat(testDB.isReservedWord("REPLICATION"), CoreMatchers.is(true));
		for (int i = 0; i < TeradataReservedWord.values().length; i++) {
			Assert.assertThat(testDB.isReservedWord(TeradataReservedWord.values()[i].name()), CoreMatchers.is(true));
		}
	}

	@Test
	public void getDateTimeLiteral() {
		Timestamp ts = new Timestamp(now.getTime());
		Assert.assertThat(testDB.getDateTimeLiteral(ts), IsEqual.equalTo(String.format("'%s'", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(ts))));
	}

	@Test
	public void getDateLiteral() {
		Assert.assertThat(testDB.getDateLiteral(new java.sql.Date(now.getTime())), IsEqual.equalTo(
				"'" + new SimpleDateFormat("yyyy-MM-dd").format(now) + "'"));
	}

	@Test
	public void getTimeLiteral() {
		// "'" + new SimpleDateFormat("hh:mm:ss.SSS").format(date) + "'"
		Time nowTime = new Time(now.getTime());
		Assert.assertThat(testDB.getTimeLiteral(nowTime), IsEqual.equalTo(
				"'" + new SimpleDateFormat("hh:mm:ss.SSS").format(nowTime) + "'"));
	}
}