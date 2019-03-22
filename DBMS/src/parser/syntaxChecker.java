package parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.QuerySyntax;

public class syntaxChecker implements QuerySyntax {

	private String[] QueriesMatching;

	public syntaxChecker() {
		QueriesMatching = new String[13];
		// create databse
		QueriesMatching[0] = "(?i)\\s*create\\s+database\\s+(if\\s+not\\s+exists\\s+)?.+\\s*;?\\s*";
		// drop database
		QueriesMatching[1] = "(?i)\\s*drop\\s+database\\s+(if\\s+exists\\s+)?.+\\s*;?\\s*";
		// create table
		QueriesMatching[2] = "(?i)\\s*create\\s+table\\s+(if\\s+not\\s+exists\\s+)?\\w+\\s*\\((\\s*\\w+\\s+(int|varchar)\\s*(\\s+not\\s+null\\s*)?,?)+\\)\\s*;?\\s*";
		// drop table
		QueriesMatching[3] = "(?i)\\s*drop\\s+table\\s+(if\\s+exists\\s+)?\\w+\\s*;?\\s*";
		// insert into without determining columns
		QueriesMatching[4] = "(?i)\\s*insert\\s+into\\s+\\w+\\s+values\\s*\\((\\s*'?\\w+'?\\s*,?)+\\)"; // columns
		// insert into with determining columns
		QueriesMatching[5] = "(?i)\\s*insert\\s+into\\s+\\w+\\s*\\(.+\\)\\s*values\\s*\\(.+\\)";
		// select all in tables
		QueriesMatching[6] = "(?i)\\s*select\\s*\\*\\s*from\\s+\\w+(\\s+where\\s+\\w+\\s*(=|<|>|<=|>=)\\s*\\w+)?\\s*";
		// select specific columns
		QueriesMatching[7] = "(?i)\\s*select\\s+.+(?<=\\w{1,20}\\s{1,20})from\\s+\\w+\\s*(\\s+where\\s+\\w+\\s*(>|<|=|or|and)\\s*'?\\w+'?)?\\s*;?\\s*";
		// update columns in table
		QueriesMatching[8] = "(?i)\\s*update\\s+\\w+\\s+set\\s+(\\s*\\w+\\s*='?\\w+'?,?)+\\s*(\\s+where\\s+\\w+\\s*(=|>|<|<=|>=)\\s*'?\\w+'?)?";
		// drop column from table
		QueriesMatching[9] = "(?i)\\s*alter\\s+table\\s+\\w+\\s+drop\\s+\\w+\\s*;?\\s*";
		// show tables;
		QueriesMatching[10] = "(?i)\\s*show\\s+tables\\s*;?\\s*";
		// add columns to table
		QueriesMatching[11] = "(?i)\\s*alter\\s+table\\s+\\w+\\s+add\\s*\\(?(\\w+\\s+(int|varchar)\\s*,?\\s*)+\\)?(?<=\\w{1,100}\\s{0,100}\\)?);?\\s*";
		// delete from table
		QueriesMatching[12] = "(?i)\\s*delete\\s+from\\s+\\w+(\\s+where\\s+\\w+(=|>|<|>=|<=)'?\\w+'?)?";
	}

	@Override
	public int isValid(String query) {
		for (int i = 0; i < QueriesMatching.length; i++) {
			if (query.matches(QueriesMatching[i])) {
				return i + 1;
			}
		}
		return 0;
	}

	@Override
	public String getDBName(String query) {
		Pattern DBpattern = Pattern.compile("(?i)(?<=(create|drop)\\s{1,20}database\\s{1,20}).+(?=\\s*;?)");
		Matcher DBmatcher = DBpattern.matcher(query);
		if (DBmatcher.find()) {
			return DBmatcher.group();
		}
		return null;
	}

	@Override
	public String getTableName(String query) {
		Pattern tablePattern = Pattern.compile("(?i)(?<=(create|drop|alter)\\s{1,20}table\\s{1,20})\\w+");
		Pattern tableSelect = Pattern.compile("(?i)(?<=(from|into|update)\\s{1,100})\\w+");
		Matcher selectMatcher = tableSelect.matcher(query);
		Matcher tableMatcher = tablePattern.matcher(query);
		if (tableMatcher.find())
			return tableMatcher.group();
		if (selectMatcher.find())
			return selectMatcher.group();
		return null;
	}

	@Override
	public ArrayList<String> getColumnsName(String query) {
		ArrayList<String> columnsNames = new ArrayList<>();
		Pattern create = Pattern.compile("(?i)(?<=(\\(|,)\\s*)\\w+(?=\\s+\\w+)");// create and add columns
		Pattern update = Pattern.compile("(?i)\\w+(?=\\s*\\=)");
		Pattern insert = Pattern.compile(
				"(?i)(?<=insert\\s{1,30}into\\s{1,30}\\w{1,30}\\s{0,30}\\().+(?=\\s*\\)\\s*values)");
		Pattern s = Pattern.compile("(?i)(?<=select\\s{1,30}).+(?=\\s+from)");
		Matcher createMatcher = create.matcher(query);
		Matcher updateMatcher = update.matcher(query);
		Matcher inMatcher = insert.matcher(query);
		Matcher sMatcher = s.matcher(query);
		while (createMatcher.find()) {
			columnsNames.add(createMatcher.group().trim());
		}

		while (updateMatcher.find() && !query.toLowerCase().contains("select")) {
			columnsNames.add(updateMatcher.group().trim());
		}

		if (inMatcher.find()) {
			if (inMatcher.group().contains(",")) {
				String[] arr = inMatcher.group().split(",");
				for (int i = 0; i < arr.length; i++) {
					columnsNames.add(arr[i].trim());
				}
			} else
				columnsNames.add(inMatcher.group());
		}
		boolean flagSelect = sMatcher.find();
		if (flagSelect) {
			if (sMatcher.group().contains(",")) {
				String[] arr = sMatcher.group().split(",");
				for (int i = 0; i < arr.length; i++) {
					columnsNames.add(arr[i].trim());
				}
			} else
				columnsNames.add(sMatcher.group());
		}
		if (query.toLowerCase().contains("where") && !flagSelect) {
			columnsNames.remove(columnsNames.size() - 1);
		}
		return columnsNames;
	}

	@Override
	public ArrayList<String> getColumnsType(String query) {
		ArrayList<String> types = new ArrayList<>();
		Pattern typePattern = Pattern.compile("(?i)(?<=(\\(|,)\\s{0,100}\\w{1,100}\\s{1,100})(int|varchar)");
		Matcher typeMatcher = typePattern.matcher(query);
		while (typeMatcher.find()) {
			types.add(typeMatcher.group().trim());
		}
		return types;
	}

	@Override
	public ArrayList<String> getInsertingValues(String query) {
		ArrayList<String> values = new ArrayList<>();
		Pattern insert = Pattern.compile("(?i)(?<=values\\s{0,100}\\().+(?=\\))");
		Pattern update = Pattern.compile("(?i)(?<=\\=\\s{0,100}'?)\\w+");
		
		Matcher inserMatcher = insert.matcher(query);
		Matcher updateMatcher = update.matcher(query);
		if (inserMatcher.find()) {
			String[] arr = inserMatcher.group().split(",");
			for (int i = 0; i < arr.length; i++) {
				values.add(arr[i].trim());
			}
		}

		while (updateMatcher.find()) {
			values.add(updateMatcher.group());
		}if(query.toLowerCase().contains("where"))
			values.remove(values.size()-1);
		return values;
	}

	@Override
	public String getCondition(String query) {
		Pattern conditionPatter = Pattern
				.compile("(?i)(?<=where\\s{1,100}).+");
		Pattern before = Pattern.compile("(?i)\\w+(?=\\s*(\\=|\\<|\\>|\\<\\=|\\>\\=))");
		Pattern after = Pattern.compile("(?i)(?<=(\\=|\\<|\\>|\\<\\=|\\>\\=)\\s*).+");
		Pattern operator = Pattern.compile("(\\=|\\<|\\>|\\<\\=|\\>\\=)");

		String condition = null;
		String conditionWithSpaces = null;
		Matcher conditionMatcher = conditionPatter.matcher(query);
		if (conditionMatcher.find()) {
			condition = conditionMatcher.group().trim();
		Matcher beforeMatcher = before.matcher(condition);
		Matcher afterMatcher = after.matcher(condition);
		Matcher operMatcher = operator.matcher(condition);
		beforeMatcher.find();
		afterMatcher.find();
		operMatcher.find();
		conditionWithSpaces = beforeMatcher.group().trim() + " " + operMatcher.group() + " "
				+ afterMatcher.group().trim();
		}

		return conditionWithSpaces;
	}

	@Override
	public String getDropedColumn(String query) {
		Pattern dropColumn = Pattern.compile("(?i)(?<=drop\\s{1,30})\\w+(?=\\s*;?)");
		Matcher dropMatcher = dropColumn.matcher(query);
		if (dropMatcher.find()) {
			return dropMatcher.group();
		}
		return null;
	}

}
