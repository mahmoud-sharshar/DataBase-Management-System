package eg.edu.alexu.csd.oop.db.cs26;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import eg.edu.alexu.csd.oop.db.Column;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.QuerySyntax;
import eg.edu.alexu.csd.oop.db.Table;
import eg.edu.alexu.csd.oop.db.cs30.FileBuilder;
import eg.edu.alexu.csd.oop.db.cs47.*;
import parser.syntaxChecker;

public class DatabaseImp implements Database {
	private QuerySyntax controller;
	private static FileBuilder builder = FileBuilder.getInstance();
	private String path = null;
	private String databaseName = null;
	private ArrayList<String> tablesNames;
	private ArrayList<DataTable> tables;

	public DatabaseImp() {
		controller = new syntaxChecker();
		tables = new ArrayList<DataTable>();
		// builder = FileBuilder.getInstance();
		tablesNames = new ArrayList<String>();
	}
	@Override
	public ArrayList getColumnsNames(String query) {
		int operation = controller.isValid(query);
		if (operation == 0) {
			return null;
		}
		String tableName = controller.getTableName(query).toLowerCase();
		tablesNames = builder.getTables(databaseName);
		if (!checkTableName(tableName)) {
			return null;
		}
		switch (operation) {
		case 6:
		case 8:
		case 9:
			return controller.getColumnsName(query);
		case 5:
		case 7:
			int index = tablesNames.indexOf(tableName);
			DataTable t = tables.get(index);
			ArrayList<Column> columns = new ArrayList<>();
			columns = t.getColumnsFromXml(tableName, databaseName);
			ArrayList<String> colNames = new ArrayList<>();
			for (Column c : columns) {
				colNames.add(c.getColumnName());
			}
			return colNames;
			
		}
		return null;
	}
	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		if (dropIfExists) {
			builder.dropDB(databaseName);
		}
		try {
			executeStructureQuery("create database " + databaseName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File file = new File(databaseName);
		path = file.getAbsolutePath();
		return path;
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		int operation = controller.isValid(query);
		if (operation == 0) {
			throw new SQLException();
		}
		switch (operation) {
		case 1:
			databaseName = controller.getDBName(query);
			File file = new File(databaseName);
			path = file.getAbsolutePath();
			return builder.createDB(databaseName);
		case 2:
			databaseName = controller.getDBName(query);
			return builder.dropDB(databaseName);
		case 3:
			tablesNames = builder.getTables(databaseName);
			if (databaseName == null) {
				return false;
			}
			return createTable(query);
		case 4:
			tablesNames = builder.getTables(databaseName);
			if (databaseName == null) {
				return false;
			}
			return dropTable(query);
		}
		return false;
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {
		int operation = controller.isValid(query);
		if (operation == 0) {
			throw new SQLException();
		}
		if (databaseName == null) {
			return null;
		}
		tablesNames = builder.getTables(databaseName);
		switch (operation) {
		case 7:
			return selectAll(query);
		case 8:
			return selectFromTable(query);
		}
		return null;
	}

	private Object[][] selectAll(String query) {
		String tableName = controller.getTableName(query).toLowerCase();
		if (!checkTableName(tableName)) {
			return null;
		}
		int index = tablesNames.indexOf(tableName);
		DataTable t = tables.get(index);
		ArrayList<Column> columns = new ArrayList<>();
		columns = t.getColumnsFromXml(tableName, databaseName);
		ArrayList<String> colNames = new ArrayList<>();
		for (Column c : columns) {
			colNames.add(c.getColumnName());
		}
		ArrayList<String> condition = new ArrayList<String>();
		condition.add(controller.getCondition(query));
		return t.select(colNames, condition);
	}

	@Override
	public int executeUpdateQuery(String query) throws SQLException {
		int operation = controller.isValid(query);
		if (operation == 0) {
			throw new SQLException();
		}
		if (databaseName == null) {
			return (Integer) null;
		}
		tablesNames = builder.getTables(databaseName);
		switch (operation) {
		case 5:
			return insertAll(query);
		case 6:
			return insertIntoTable(query);
		case 9:
			return updateTable(query);
		case 13:
			return deleteFromTable(query);
		}
		return (Integer) null;
	}

	private int insertAll(String query) {
		String tableName = controller.getTableName(query).toLowerCase();
		if (!checkTableName(tableName)) {
			return (Integer) null;
		}
		int index = tablesNames.indexOf(tableName);
		DataTable t = tables.get(index);
		ArrayList<Column> columns = new ArrayList<>();
		columns = t.getColumnsFromXml(tableName, databaseName);
		ArrayList<String> colNames = new ArrayList<>();
		for (Column c : columns) {
			colNames.add(c.getColumnName());
		}
		return t.insert(colNames, controller.getInsertingValues(query));
	}

	private int deleteFromTable(String query) {
		String tableName = controller.getTableName(query).toLowerCase();
		if (!checkTableName(tableName)) {
			return (Integer) null;
		}
		int index = tablesNames.indexOf(tableName);
		DataTable t = tables.get(index);
		ArrayList<String> conditions = new ArrayList<String>();
		conditions.add(controller.getCondition(query));
		return t.deleteRows(conditions);
	}

	private int updateTable(String query) throws SQLException {
		String tableName = controller.getTableName(query).toLowerCase();
		if (!checkTableName(tableName)) {
			throw new SQLException();
		}
		int index = tablesNames.indexOf(tableName);
		DataTable t = tables.get(index);
		ArrayList<String> conditions = new ArrayList<String>();
		conditions.add(controller.getCondition(query));
		if (conditions.isEmpty())
			conditions.add(controller.getColumnsName(query).get(0) + " " + "!=" + " " + "henksa");
		return t.update(controller.getColumnsName(query), controller.getInsertingValues(query), conditions);
	}

	private int insertIntoTable(String query) {
		String tableName = controller.getTableName(query).toLowerCase();
		if (!checkTableName(tableName)) {
			return (Integer) null;
		}
		int index = tablesNames.indexOf(tableName);
		DataTable t = tables.get(index);

		return t.insert(controller.getColumnsName(query), controller.getInsertingValues(query));
	}

	private Object[][] selectFromTable(String query) {
		String tableName = controller.getTableName(query).toLowerCase();
		if (!checkTableName(tableName)) {
			return null;
		}
		int index = tablesNames.indexOf(tableName);
		DataTable t = tables.get(index);
		ArrayList<String> conditions = new ArrayList<String>();
		conditions.add(controller.getCondition(query));
		return t.select(controller.getColumnsName(query), conditions);
	}

	private boolean createTable(String query) {
		String tableName = controller.getTableName(query).toLowerCase();
		if (checkTableName(tableName)) {
			return false;
		}
		tablesNames.add(tableName);
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<String> colTypes = new ArrayList<String>();
		colNames = controller.getColumnsName(query);
		colTypes = controller.getColumnsType(query);
		String[] fields = new String[colNames.size()];
		String[] types = new String[colNames.size()];
		for (int i = 0; i < colNames.size(); i++) {
			Column column = new Col();
			fields[i] = colNames.get(i);
			types[i] = colTypes.get(i);
		}
		builder.createTable(types, fields, tableName, databaseName);
		return true;
	}

	/**
	 * check if the table exists in the DB or not. sawy.
	 * 
	 * @param tableName
	 * @return true if table exists, else return false.
	 */
	private boolean checkTableName(String tableName) {
		if (tablesNames != null) {
			tables = getTables(databaseName);
			for (String s : tablesNames) {
				if (s.equalsIgnoreCase(tableName))
					return true;
			}
		}
		return false;

	}

	private boolean dropTable(String query) {
		String tableName = controller.getTableName(query).toLowerCase();
		if (!checkTableName(tableName)) {
			return false;
		}
		File xmlFile = new File(path + File.separator + tableName + ".xml");
		File dtdFile = new File(path + File.separator + tableName + ".dtd");
		return builder.deleteDirectory(xmlFile) && builder.deleteDirectory(dtdFile);

	}

	private static ArrayList<DataTable> getTables(String dbName) {
		ArrayList<DataTable> tables = new ArrayList<DataTable>();
		ArrayList<String> tNames = builder.getTables(dbName);
		if (tNames != null) {
			for (int i = 0; i < tNames.size(); i++) {
				ArrayList<Column> colsTable = DataTable.getColumnsFromXml(tNames.get(i), dbName);
				DataTable newTable = new DataTable(dbName, tNames.get(i), colsTable);
				tables.add(newTable);
			}
		}
		return tables;
	}
}
