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
import liquibase.sqlgenerator.core.RenameTableGenerator;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.RenameTableStatement;
import liquibase.structure.core.Table;

/**
 * Teradata specific syntax for table renaming
 *
 */
public class RenameTableGeneratorTeradata extends RenameTableGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

	@Override
	public boolean supports(RenameTableStatement statement, Database database) {
		return database instanceof TeradataDatabase;
	}

	@Override
	public Sql[] generateSql(RenameTableStatement statement, Database database,
			SqlGeneratorChain sqlGeneratorChain) {
		return new Sql[] {
				new UnparsedSql("RENAME TABLE " + database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getOldTableName()) +
                        " TO " + database.escapeObjectName(statement.getNewTableName(), Table.class))};
	}
}
