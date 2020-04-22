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

import java.util.List;

import liquibase.database.Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.DropIndexGenerator;
import liquibase.statement.core.DropIndexStatement;
import liquibase.structure.core.Index;
import liquibase.util.StringUtil;

/**
 * drop index specific syntax
 *
 */
public class DropIndexGeneratorTeradata extends DropIndexGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

	@Override
	public boolean supports(DropIndexStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	/**
	 * @see liquibase.sqlgenerator.core.DropIndexGenerator#generateSql(liquibase.statement.core.DropIndexStatement, liquibase.database.Database, liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(DropIndexStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		List<String> associatedWith = StringUtil.splitAndTrim(statement.getAssociatedWith(), ",");
		if (associatedWith != null) {
			if (associatedWith.contains(Index.MARK_PRIMARY_KEY)|| associatedWith.contains(Index.MARK_UNIQUE_CONSTRAINT)) {
				return new Sql[0];
			} else if (associatedWith.contains(Index.MARK_FOREIGN_KEY) ) {
				if (!(database instanceof OracleDatabase || database instanceof MSSQLDatabase)) {
					return new Sql[0];
				}
			}
		}

		String schemaName = statement.getTableSchemaName();

		return new Sql[] {new UnparsedSql("DROP INDEX " + database.escapeObjectName(statement.getIndexName(), Index.class) +" ON " + database.escapeTableName(statement.getTableCatalogName(), schemaName, statement.getTableName()) ) };
	}



}
