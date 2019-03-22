package eg.edu.alexu.csd.oop.db.cs47;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import conditions.conditionFactory;
import eg.edu.alexu.csd.oop.db.Column;
import eg.edu.alexu.csd.oop.db.Command;
import eg.edu.alexu.csd.oop.db.Table;
import eg.edu.alexu.csd.oop.db.cs30.FileBuilder;

public class DataTable implements Table {

	private String dBName;
	private String tableName;
	private ArrayList<Column> columns;
	private String[][] rows;
	private static FileBuilder parser = FileBuilder.getInstance();
	private String[] partsOfCondition;
	private Object[][] selectedValues;
	private ConditionHandler conditionHandler;
	private conditionFactory conFactory;
	private Command con;
	private int res;

	public DataTable(String dBName, String tableName, ArrayList<Column> columns) {
		this.dBName = dBName;
		this.tableName = tableName;
		this.columns = columns;
		rows = null;
		partsOfCondition = null;
		conditionHandler = new ConditionHandler();
		conFactory = new conditionFactory();
		con = null;
		res = 0;
	}

	@Override
	public Object[][] select(ArrayList<String> columnsNames, ArrayList<String> sconditions) {
		// System.err.println(columns = getColumnsFromXml(this.tableName, this.dBName));
		// TODO Auto-generated method stub
		Object[][] rowValues = getTableRows();
		List<String> colNames = Arrays.asList(getFields()); // it will change to read from xml file
		if (sconditions.get(0) == null)
		{
			int numOfRows = rowValues.length;
			selectedValues = new Object[numOfRows][columnsNames.size()];
			int indexForRows = 0;
			int numOfCols = colNames.size();
			for (int i = 0; i < numOfRows; i++) {
				int indexforCols = 0;
				for (int j = 0; j < numOfCols; j++) {
					
					if (columnsNames.contains(colNames.get(j))) {

						selectedValues[indexForRows][indexforCols] = rowValues[i][j];
						indexforCols++;
					}
				}
				indexForRows++;
			}
			
			return selectedValues ;
		}
		// it will change if there is and & or conditions
		for(int k=0; k < sconditions.size();k++) {
			partsOfCondition = sconditions.get(k).split(" ");
			String columnName = partsOfCondition[0].toLowerCase();
			String operator   = partsOfCondition[1];
			String value      = partsOfCondition[2];
			int indexOfCol = colNames.indexOf(columnName);
			int numOfRows = rowValues.length;
			int numOfCols = colNames.size();
			con = conFactory.createCondition(conditionHandler, operator);
			int indexForRows = 0;
			for (int i = 0; i < numOfRows; i++) {
				String test = (String) rowValues[i][indexOfCol].toString();
				/*if (test.contains("'")) {
					test = test.substring(1, test.length() - 1).toLowerCase();
				}*/
				if (con.execute(test, value.toLowerCase())) {
					indexForRows++;
				}
			}
			int i1 = columnsNames.size();
			selectedValues = new Object[indexForRows][columnsNames.size()];
			indexForRows = 0;
			for (int i = 0; i < numOfRows; i++) {
				String test = (String) rowValues[i][indexOfCol].toString();
			/*	if (test.contains("'")) {
					test = test.substring(1, test.length() - 1).toLowerCase();
				}*/
				if (con.execute(test, value.toLowerCase())) {
					int indexforCols = 0;
					for (int j = 0; j < numOfCols; j++) {
						if (columnsNames.contains(colNames.get(j))) {

							selectedValues[indexForRows][indexforCols] = rowValues[i][j];
							indexforCols++;
						}
					}
					indexForRows++;
				}
			}
			
		}
		return selectedValues;
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public int insert(ArrayList<String> columnsNames, ArrayList<String> values) {

		res = 0;
		columns = getColumnsFromXml(this.tableName, this.dBName);
		// TODO Auto-generated method stub
		// i need a function to get a column from xml file
		// check that all columns have a value to insert
		if (columnsNames.size() != values.size()) {

			return res;
		}
		// check that all columns names exist in the table
		for (String col : columnsNames) {
			if (!checkColumnExistence(col)) {
				return res;
			}
		}
		// check that all values'data types are the same of columns'data types
		for (int i = 0; i < columnsNames.size(); i++) {
			if (!checkColumnDataType(columnsNames.get(i), values.get(i))) {
				return res;
			}
		}
		for (int i = 0; i < columnsNames.size(); i++) {
			String s = new String();
			s = columnsNames.get(i).toLowerCase();
			columnsNames.set(i, s);
		}
		// insert new values to selected columns and make others null
		for (int i = 0; i < columns.size(); i++) {
			String colName = columns.get(i).getColumnName().toLowerCase();
			if (!columnsNames.contains(colName)) {
				addNewValueToCol(colName, " ");
			} else {
				for (int j = 0; j < columnsNames.size(); j++) {
					if (columnsNames.get(j).equals(colName)) {
						addNewValueToCol(colName, values.get(j));
					}
				}
			}
		}

		parser.write(getContent(), getFields(), this.tableName, this.dBName);
		columns = getColumnsFromXml(this.tableName, this.dBName);
		return 1;
	}

	@Override
	public int update(ArrayList<String> columnsNames, ArrayList<String> values, ArrayList<String> uconditions) {

		res = 0;
		columns = getColumnsFromXml(this.tableName, this.dBName);
		for (int i = 0; i < columnsNames.size(); i++) {
			String s = new String();
			s = columnsNames.get(i).toLowerCase();
			columnsNames.set(i, s);
		}
		// check that all size columns have a value to insert
		
		if (columnsNames.size() != values.size()) {

			return res;
		}
		// check that all columns names exist in the table
		for (String col : columnsNames) {
			if (!checkColumnExistence(col)) {
				return res;
			}
		}
		// check that all values'data types are the same of columns'data types
		for (int i = 0; i < columnsNames.size(); i++) {
			if (!checkColumnDataType(columnsNames.get(i), values.get(i))) {

				return res;
			}
		}

		Object[][] rowValues = getTableRows();
		if(rowValues== null) {
			return 0 ;
		}
		List<String> colNames = Arrays.asList(getFields());
		if(uconditions.get(0) == null) {
			int numOfRows = rowValues.length;
			for (int i = 0; i < numOfRows; i++) {
				for (int j = 0; j < columnsNames.size(); j++) {
					getColumn(columnsNames.get(j)).getRows().set(i, values.get(j));
				}

			}
			return numOfRows ;
			
		}
		uconditions.forEach(condition -> {
			partsOfCondition = condition.split(" ");
			String colName = partsOfCondition[0].toLowerCase();
			String oper = partsOfCondition[1];
			String val = partsOfCondition[2];

			int indexOfCol = colNames.indexOf(colName);
			int numOfRows = rowValues.length;
			con = conFactory.createCondition(conditionHandler, oper);
			for (int i = 0; i < numOfRows; i++) {
				String test = (String) rowValues[i][indexOfCol].toString();
				/*if (test.contains("'")) {
					test = test.substring(1, test.length() - 1).toLowerCase();
				}*/
				if (con.execute(test, val.toLowerCase())) {
					res++;
					for (int j = 0; j < columnsNames.size(); j++) {
						getColumn(columnsNames.get(j)).getRows().set(i, values.get(j));
					}
				}
			}
		});
	    Object[][] s = this.getContent();
		parser.write(getContent(), getFields(), this.tableName, this.dBName);
		return res;
	}

	@Override
	public Boolean deleteColumn(ArrayList<String> columnsNames) {

		// TODO Auto-generated method stub
		columns = getColumnsFromXml(this.tableName, this.dBName);
		for (String col : columnsNames) {
			if (!checkColumnExistence(col)) {

				return false;
			}
		}

		for (int i = 0; i < columnsNames.size(); i++) {
			columns.remove(this.getColumn(columnsNames.get(i)));
		}
		this.getFields();
		parser.write(getContent(), getFields(), this.tableName, this.dBName);

		return true;
	}

	@Override
	public int deleteRows(ArrayList<String> dconditions) {
		if (this.getTableRows() == null) {
			return 0;
		}
		int numOfCurrentRows = this.getContent().length;
		res = 0;
		
		if(dconditions.get(0) == null) {
			parser.read(this.tableName, this.dBName);
			String[] types = parser.getTypesList(); 
			String[] fields = parser.getArrayList();
			parser.createTable(types, fields, this.tableName, this.dBName);
			return numOfCurrentRows ; 
		}
		for (int k = 0; k < dconditions.size(); k++) {
			partsOfCondition = dconditions.get(k).split(" ");
			String colName = partsOfCondition[0].toLowerCase();
			String oper = partsOfCondition[1];
			String val = partsOfCondition[2];
			switch (oper) {
			case "=":
				oper = "!=";
				break;
			case "!=":
				oper = "=";
				break;
			case ">":
				oper = "<=";
				break;
			case "<":
				oper = ">=";
				break;
			case "<=":
				oper = ">";
				break;
			case ">=":
				oper = "<";
				break;
			}

			List<String> colNames = Arrays.asList(getFields());
			ArrayList<String> input = new ArrayList<>();
			colNames.forEach(c -> {
				input.add(c);
			});
			ArrayList<String> cond = new ArrayList<>();
			cond.add(colName + " " + oper + " " + val);
           System.out.println("VAlUE" + val);
			this.select(input, cond);
			if (selectedValues.length > 0) {
				for (int i = 0; i < selectedValues[0].length; i++) {
					for (int j = 0; j < selectedValues.length; j++) {
						ArrayList<Object> rowValues = new ArrayList<>();
						rowValues.add(selectedValues[j][i]);
						columns.get(i).setRows(rowValues);
					}
				}

				
				parser.write(this.select(input, cond), getFields(), this.tableName, this.dBName);
				this.getColumnsFromXml(this.tableName, this.dBName);
				
				res = this.getContent().length;
			} else {

				String[] names = parser.getArrayList();
				String[] types = parser.getTypesList();
				parser.createTable(types, names, this.tableName, this.dBName);
				res = 0;

			}

			//
		}
		return numOfCurrentRows - selectedValues.length;
	}

	public ArrayList<Object> getRowsValues(String columnName) {
		columns = getColumnsFromXml(this.tableName, this.dBName);
		for (Column col : columns) {
			if (columnName.equals(col.getColumnName())) {
				return col.getRows();
			}
		}
		return null;
	}

	public Boolean checkColumnExistence(String columnName) {
		columns = getColumnsFromXml(this.tableName, this.dBName);
		for (Column col : columns) {
			if (columnName.equalsIgnoreCase(col.getColumnName())) {
				return true;
			}
		}
		return false;
	}

	public Boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Boolean checkColumnDataType(String columnName, String value) {
		columns = getColumnsFromXml(this.tableName, this.dBName);
		for (Column col : columns) {
			if (columnName.equals(col.getColumnName())) {
				if (col.getDataType().equalsIgnoreCase("int")) {
					if (!isInteger(value))
						return false;
				}
			}
		}
		return true;

	}

	public Object[][] getTableRows() {
		return parser.read(this.tableName, this.dBName);
	}

	public Column getColumn(String columnName) {

		for (Column col : columns) {
			if (columnName.equals(col.getColumnName())) {
				return col;
			}
		}
		return null;
	}

	public void addNewValueToCol(String columnName, String value) {
		Column col = getColumn(columnName);
		col.addNewRecord(value);
	}

	public String[] getFields() {
		columns = getColumnsFromXml(this.tableName, this.dBName);
		String[] colNames = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			colNames[i] = columns.get(i).getColumnName();
		}
		return colNames;
	}

	public Object[][] getContent() {
		// columns = getColumnsFromXml(this.tableName, this.dBName);
		if (!columns.isEmpty()) {
			Object[][] content = new Object[columns.get(0).getRows().size()][columns.size()];
			for (int i = 0; i < columns.size(); i++) {
				ArrayList<Object> values = columns.get(i).getRows();
				for (int j = 0; j < values.size(); j++) {

					content[j][i] = values.get(j);
				}
			}
			return content;
		} else {
			return null;
		}
	}

	public static ArrayList<Column> getColumnsFromXml(String tname, String dname) {
		ArrayList<Column> cols = new ArrayList<>();
		Object[][] values = parser.read(tname, dname);
		String[] names = parser.getArrayList();
		String[] types = parser.getTypesList();
		for (int i = 0; i < names.length; i++) {
			Column newCol = new Col();
			newCol.setColumnName(names[i]);
			newCol.setDataType(types[i]);
			newCol.setTableName(dname);
			if (values != null) {
				for (int j = 0; j < values.length; j++) {
					newCol.addNewRecord(values[j][i].toString());
				}
			}
			cols.add(newCol);
		}
		return cols;
	}

	public void setdBName(String dBName) {
		this.dBName = dBName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

}
