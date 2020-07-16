/**
 * 
 */
package liquibase.ext.teradata.sqlgenerator;

import java.util.Iterator;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.datatype.LiquibaseDataType;
import liquibase.datatype.core.DateTimeType;
import liquibase.datatype.core.DateType;
import liquibase.datatype.core.TimeType;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.logging.LogFactory;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.CreateTableGenerator;
import liquibase.statement.AutoIncrementConstraint;
import liquibase.statement.ForeignKeyConstraint;
import liquibase.statement.UniqueConstraint;
import liquibase.statement.core.CreateTableStatement;
import liquibase.util.StringUtil;

/**
 * To handle Teradata specific syntax for default values
 *
 */
public class CreateTableGeneratorTeradata extends CreateTableGenerator {

	@Override
	public int getPriority() {
		return PRIORITY_DATABASE;
	}

	@Override
	public boolean supports(CreateTableStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	/**
	 * Modified to handle Teradata syntax for default values
	 * 
	 * @see liquibase.sqlgenerator.core.CreateTableGenerator#generateSql(liquibase.statement.core.CreateTableStatement, liquibase.database.Database,
	 *      liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(CreateTableStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("CREATE TABLE ").append(database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName()))
				.append(" ");
		buffer.append("(");
		Iterator<String> columnIterator = statement.getColumns().iterator();

		boolean isSinglePrimaryKeyColumn = statement.getPrimaryKeyConstraint() != null && statement.getPrimaryKeyConstraint().getColumns().size() == 1;

		boolean isPrimaryKeyAutoIncrement = false;

		while (columnIterator.hasNext()) {
			String column = columnIterator.next();
			boolean isAutoIncrement = false;
			for (AutoIncrementConstraint constraint : statement.getAutoIncrementConstraints()) {
				if (constraint.getColumnName().equalsIgnoreCase(column)) {
					isAutoIncrement = true;
					break;
				}
			}

			buffer.append(database.escapeColumnName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), column));
			buffer.append(" ").append(statement.getColumnTypes().get(column).toDatabaseDataType(database));

			if (statement.getDefaultValue(column) != null) {
				Object defaultValue = statement.getDefaultValue(column);
				buffer.append(" DEFAULT ");
				LiquibaseDataType defaultValueType = DataTypeFactory.getInstance().fromObject(defaultValue, database);
				buffer.append((defaultValueType instanceof DateTimeType ? " TIMESTAMP "
						: (defaultValueType instanceof DateType ? " DATE " : (defaultValueType instanceof TimeType ? " TIME " : ""))));

				buffer.append(defaultValueType.objectToSql(defaultValue, database));
			}

			if (isAutoIncrement && (database.getAutoIncrementClause(null, null, null, true) != null) && (!database.getAutoIncrementClause(null, null, null, true).equals(""))) {
				if (database.supportsAutoIncrement()) {
					buffer.append(" ").append(database.getAutoIncrementClause(null, null, null, true)).append(" ");
				} else {
					Scope.getCurrentScope().getLog(getClass()).warning(database.getShortName() + " does not support autoincrement columns as request for "
							+ (database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName())));
				}
			}

			if (statement.getNotNullColumns().containsKey(column)) {
				buffer.append(" NOT NULL");
			}

			if (columnIterator.hasNext()) {
				buffer.append(", ");
			}
		}

		buffer.append(",");

		if (!(isSinglePrimaryKeyColumn && isPrimaryKeyAutoIncrement)) {

			if (statement.getPrimaryKeyConstraint() != null && !statement.getPrimaryKeyConstraint().getColumns().isEmpty()) {
				if (database.supportsPrimaryKeyNames()) {
					String pkName = StringUtil.trimToNull(statement.getPrimaryKeyConstraint().getConstraintName());
					if (pkName == null) {
						pkName = database.generatePrimaryKeyName(statement.getTableName());
					}
					if (pkName != null) {
						buffer.append(" CONSTRAINT ");
						buffer.append(database.escapeConstraintName(pkName));
					}
				}
				buffer.append(" PRIMARY KEY (");
				buffer.append(database.escapeColumnNameList(StringUtil.join(statement.getPrimaryKeyConstraint().getColumns(), ", ")));
				buffer.append(")");

				buffer.append(",");
			}
		}

		for (ForeignKeyConstraint fkConstraint : statement.getForeignKeyConstraints()) {
			buffer.append(" CONSTRAINT ");
			buffer.append(database.escapeConstraintName(fkConstraint.getForeignKeyName()));
			String referencesString = fkConstraint.getReferences();
			if (!referencesString.contains(".") && database.getDefaultSchemaName() != null) {
				referencesString = database.getDefaultSchemaName() + "." + referencesString;
			}
			buffer.append(" FOREIGN KEY (").append(
					database.escapeColumnName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), fkConstraint.getColumn()))
					.append(") REFERENCES ").append(referencesString);

			if (fkConstraint.isDeleteCascade()) {
				buffer.append(" ON DELETE CASCADE");
			}

			if (fkConstraint.isInitiallyDeferred()) {
				buffer.append(" INITIALLY DEFERRED");
			}
			if (fkConstraint.isDeferrable()) {
				buffer.append(" DEFERRABLE");
			}
			buffer.append(",");
		}

		for (UniqueConstraint uniqueConstraint : statement.getUniqueConstraints()) {
			if (uniqueConstraint.getConstraintName() != null) {
				buffer.append(" CONSTRAINT ");
				buffer.append(database.escapeConstraintName(uniqueConstraint.getConstraintName()));
			}
			buffer.append(" UNIQUE (");
			buffer.append(database.escapeColumnNameList(StringUtil.join(uniqueConstraint.getColumns(), ", ")));
			buffer.append(")");
			buffer.append(",");
		}

		String sql = buffer.toString().replaceFirst(",\\s*$", "") + ")";

		return new Sql[] { new UnparsedSql(sql) };
	}
}
