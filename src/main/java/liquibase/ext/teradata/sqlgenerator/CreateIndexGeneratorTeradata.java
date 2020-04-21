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
package liquibase.ext.teradata.sqlgenerator;

import java.util.Arrays;
import java.util.Iterator;

import liquibase.change.AddColumnConfig;
import liquibase.database.Database;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.CreateIndexGenerator;
import liquibase.statement.core.CreateIndexStatement;
import liquibase.structure.core.Index;

/**
 * specific syntax for create index
 *
 */
public class CreateIndexGeneratorTeradata extends CreateIndexGenerator {

	@Override
	public int getPriority() {
		return PRIORITY_DATABASE;
	}

	@Override
	public boolean supports(CreateIndexStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	/**
	 * @see liquibase.sqlgenerator.core.CreateIndexGenerator#generateSql(liquibase.statement.core.CreateIndexStatement, liquibase.database.Database,
	 *      liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(CreateIndexStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		StringBuilder buffer = new StringBuilder();

		buffer.append("CREATE ");
		if (statement.isUnique() != null && statement.isUnique().booleanValue()) {
			buffer.append("UNIQUE ");
		}
		buffer.append("INDEX ");

		if (statement.getIndexName() != null) {
			// String indexSchema = statement.getTableSchemaName();
			buffer.append(database.escapeObjectName(statement.getIndexName(), Index.class)).append(" ");
		}

		buffer.append("(");
		Iterator<AddColumnConfig> iterator = Arrays.asList(statement.getColumns()).iterator();
		while (iterator.hasNext()) {
			AddColumnConfig column = iterator.next();
			buffer.append(
					database.escapeColumnName(statement.getTableCatalogName(), statement.getTableSchemaName(), statement.getTableName(), column.getName()));
			if (iterator.hasNext()) {
				buffer.append(", ");
			}
		}
		buffer.append(")");

		buffer.append("ON ");
		buffer.append(database.escapeTableName(statement.getTableCatalogName(), statement.getTableSchemaName(), statement.getTableName()));

		return new Sql[] { new UnparsedSql(buffer.toString()) };
	}
}
