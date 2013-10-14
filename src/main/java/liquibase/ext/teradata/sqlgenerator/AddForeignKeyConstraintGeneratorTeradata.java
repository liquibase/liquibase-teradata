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

import liquibase.database.Database;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AddForeignKeyConstraintGenerator;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.AddForeignKeyConstraintStatement;

/**
 * Specific syntax for add foreign key
 *
 */
public class AddForeignKeyConstraintGeneratorTeradata extends AddForeignKeyConstraintGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

	@Override
	public boolean supports(AddForeignKeyConstraintStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	/**
	 * @see liquibase.sqlgenerator.core.AddForeignKeyConstraintGenerator#generateSql(liquibase.statement.core.AddForeignKeyConstraintStatement, liquibase.database.Database, liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(AddForeignKeyConstraintStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ")
		.append(database.escapeTableName(statement.getBaseTableCatalogName(), statement.getBaseTableSchemaName(), statement.getBaseTableName()))
		.append(" ADD CONSTRAINT ");
		sb.append(database.escapeConstraintName(statement.getConstraintName()));
		sb.append(" FOREIGN KEY (")
		.append(database.escapeColumnNameList(statement.getBaseColumnNames()))
		.append(") REFERENCES ");
		if (statement.isDeferrable() || statement.isInitiallyDeferred()) {
			if (statement.isInitiallyDeferred()) {
				sb.append(" WITH NO CHECK OPTION ");
			}
		}else{
			sb.append(" WITH CHECK OPTION " );
		}
		sb.append(database.escapeTableName(statement.getReferencedTableCatalogName(), statement.getReferencedTableSchemaName(), statement.getReferencedTableName()))
		.append("(")
		.append(database.escapeColumnNameList(statement.getReferencedColumnNames()))
		.append(")");

		return new Sql[]{
				new UnparsedSql(sb.toString())
		};
	}
}
