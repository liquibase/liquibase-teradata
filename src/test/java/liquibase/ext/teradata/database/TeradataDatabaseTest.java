package liquibase.ext.teradata.database;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TeradataDatabaseTest {

    @Test
    public void getShortName() {
        assertEquals("teradata", new TeradataDatabase().getShortName());
    }
}
