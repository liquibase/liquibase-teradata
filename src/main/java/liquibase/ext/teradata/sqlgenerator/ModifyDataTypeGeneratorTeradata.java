/**
 * 
 */
package liquibase.ext.teradata.sqlgenerator;

import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.ModifyDataTypeGenerator;
import liquibase.statement.core.ModifyDataTypeStatement;

/**
 * Teradata syntax for altering data type
 *
 */
public class ModifyDataTypeGeneratorTeradata extends ModifyDataTypeGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

    @Override
    public boolean supports(ModifyDataTypeStatement statement, Database database) {
        return database instanceof TeradataDatabase;
    }

	@Override
	public Sql[] generateSql(ModifyDataTypeStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		String alterTable = "ALTER TABLE " + database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName());

		// add "MODIFY"
		alterTable += " ADD ";

		// add column name
		alterTable += database.escapeColumnName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), statement.getColumnName());

		// alterTable += getPreDataTypeString(database); // adds a space if nothing else

		// add column type
		alterTable += " "+ DataTypeFactory.getInstance().fromDescription(statement.getNewDataType(), database).toDatabaseDataType(database).toSql();

		return new Sql[]{new UnparsedSql(alterTable)};
	}


}
