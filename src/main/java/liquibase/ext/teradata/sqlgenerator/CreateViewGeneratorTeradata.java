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
import liquibase.sqlgenerator.core.CreateViewGenerator;
import liquibase.statement.core.CreateViewStatement;

/**
 * specific syntax for replacing view
 *
 */
public class CreateViewGeneratorTeradata extends CreateViewGenerator {

	@Override
	public int getPriority() {
		return PRIORITY_DATABASE;
	}

	@Override
	public boolean supports(CreateViewStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see liquibase.sqlgenerator.core.CreateViewGenerator#generateSql(liquibase.statement.core.CreateViewStatement, liquibase.database.Database,
	 * liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(CreateViewStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		String createClause = (statement.isReplaceIfExists() ? "REPLACE " : "CREATE ") + "VIEW";

		return new Sql[] {
				new UnparsedSql(createClause + " " + database.escapeViewName(statement.getCatalogName(), statement.getSchemaName(), statement.getViewName())
						+ " AS " + statement.getSelectQuery()) };
	}
}
