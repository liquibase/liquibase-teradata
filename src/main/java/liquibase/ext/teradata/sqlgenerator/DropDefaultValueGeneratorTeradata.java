/**
 * 
 */
package liquibase.ext.teradata.sqlgenerator;

import liquibase.database.Database;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.DropDefaultValueGenerator;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.DropDefaultValueStatement;

/**
 * Teradata syntax for drop default value
 *
 */
public class DropDefaultValueGeneratorTeradata extends DropDefaultValueGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

	@Override
	public boolean supports(DropDefaultValueStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	/**
	 * @see liquibase.sqlgenerator.core.DropDefaultValueGenerator#generateSql(liquibase.statement.core.DropDefaultValueStatement, liquibase.database.Database, liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(DropDefaultValueStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		return new Sql[]{
				new UnparsedSql("ALTER TABLE " + database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName())
						+ " ADD  " + database.escapeColumnName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " DEFAULT NULL " )
		};
	}


}
