/**
 * 
 */
package liquibase.ext.teradata.sqlgenerator;

import liquibase.database.Database;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.DropPrimaryKeyGenerator;
import liquibase.statement.core.DropPrimaryKeyStatement;
import liquibase.structure.core.PrimaryKey;

/**
 * Teradata doesn't really support it
 *
 */
public class DropPrimaryKeyGeneratorTeradata extends DropPrimaryKeyGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

	@Override
	public boolean supports(DropPrimaryKeyStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	@Override
	public Sql[] generateSql(DropPrimaryKeyStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		String schemaName = statement.getSchemaName();

		return new Sql[] {new UnparsedSql("DROP INDEX " + database.escapeObjectName(statement.getConstraintName(), PrimaryKey.class) +" ON " + database.escapeTableName(statement.getCatalogName(), schemaName, statement.getTableName()) ) };
	}
}
