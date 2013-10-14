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
package liquibase.ext.teradata.change;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.AddForeignKeyConstraintChange;
import liquibase.change.core.AddLookupTableChange;
import liquibase.change.core.AddPrimaryKeyChange;
import liquibase.database.Database;
import liquibase.ext.teradata.database.TeradataDatabase;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

/**
 * specific syntax for select as
 * @author xpoinsar
 *
 */
public class AddLookupTableChangeTeradata extends AddLookupTableChange {


    @Override
    protected String[] createSupportedDatabasesMetaData(String parameterName, DatabaseChangeProperty changePropertyAnnotation) {
        return new String[] {"teradata"};
    }

    @Override
    public boolean supports(Database database) {
        return database instanceof TeradataDatabase;
    }

    /**
	 * @see liquibase.change.core.AddLookupTableChange#generateStatements(liquibase.database.Database)
	 */
	@Override
	public SqlStatement[] generateStatements(Database database) {
		List<SqlStatement> statements = new ArrayList<SqlStatement>();

        String newTableCatalogName = getNewTableCatalogName() == null?database.getDefaultCatalogName():getNewTableCatalogName();
		String newTableSchemaName = getNewTableSchemaName() == null?database.getDefaultSchemaName():getNewTableSchemaName();

		String existingTableSchemaName = getExistingTableSchemaName() == null?database.getDefaultSchemaName():getExistingTableSchemaName();
        String existingTableCatalogName = getExistingTableCatalogName() == null?database.getDefaultCatalogName():getExistingTableCatalogName();

		SqlStatement[] createTablesSQL = {new RawSqlStatement("CREATE TABLE " + database.escapeTableName(newTableCatalogName, newTableSchemaName, getNewTableName())
				+ " ( "+getNewColumnName()+" NOT NULL) AS (SELECT DISTINCT " + getExistingColumnName() + " AS " + getNewColumnName() + " FROM " + database.escapeTableName(existingTableCatalogName, existingTableSchemaName, getExistingTableName()) + " WHERE " + getExistingColumnName() + " IS NOT NULL) WITH DATA")};


		statements.addAll(Arrays.asList(createTablesSQL));

		AddPrimaryKeyChange addPKChange = new AddPrimaryKeyChange();
		addPKChange.setSchemaName(newTableSchemaName);
		addPKChange.setTableName(getNewTableName());
		addPKChange.setColumnNames(getNewColumnName());
		statements.addAll(Arrays.asList(addPKChange.generateStatements(database)));

		AddForeignKeyConstraintChange addFKChange = new AddForeignKeyConstraintChange();
		addFKChange.setBaseTableSchemaName(existingTableSchemaName);
		addFKChange.setBaseTableName(getExistingTableName());
		addFKChange.setBaseColumnNames(getExistingColumnName());
		addFKChange.setReferencedTableSchemaName(newTableSchemaName);
		addFKChange.setReferencedTableName(getNewTableName());
		addFKChange.setReferencedColumnNames(getNewColumnName());

		addFKChange.setConstraintName(getFinalConstraintName());
		statements.addAll(Arrays.asList(addFKChange.generateStatements(database)));

		return statements.toArray(new SqlStatement[statements.size()]);
	}
}
