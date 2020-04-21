package liquibase.ext.teradata.change;

import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.sdk.database.MockDatabase;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class AddLookupTableChangeTeradataTest {

	@Test
	public void supports() {
		AddLookupTableChangeTeradata tera = new AddLookupTableChangeTeradata();
		Assert.assertThat(tera.supports(new TeradataDatabase()), Is.is(true));
		Assert.assertThat(tera.supports(new MockDatabase()), Is.is(false));
	}
}
