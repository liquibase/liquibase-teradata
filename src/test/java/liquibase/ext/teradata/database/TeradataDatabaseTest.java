package liquibase.ext.teradata.database;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

public class TeradataDatabaseTest {

    @Test
    public void getShortName() {
        assertEquals("teradata", new TeradataDatabase().getShortName());
    }
}
