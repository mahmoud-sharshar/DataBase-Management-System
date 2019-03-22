package parser;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs26.DatabaseImp;

public class ConsoleUI {

	private Database dataBase;

	public ConsoleUI() {
		dataBase = new DatabaseImp();
		System.out.println("          DataBase Mangement System");
		System.out.print("SQL>");
		Scanner userInput = new Scanner(System.in);
		String input = userInput.nextLine();
		while (input != null) {
			String lower = input.toLowerCase();
			if (lower.contains("use")) {
				String use = dataBaseUsed(input);
				if (use != null) {
					dataBase.createDatabase(use, false);
					System.out.println("DataBase changed!");
				} else
					System.out.println("syntax error");
			} else if (lower.contains("drop") || lower.contains("create")) {
				try {
					if (dataBase.executeStructureQuery(input)) {
						System.out.println("Query OK");
					} else
						System.out.println("syntax error or No dataBase selected");
				} catch (SQLException e) {
					System.out.println("Exception ,syntax error");
				}
			} else if (lower.contains("update") || lower.contains("delete") || lower.contains("insert")) {
				try {
					if (dataBase.executeUpdateQuery(input) == -1)
						System.out.println("No dataBase selected");
					else
						System.out.println("Query Ok , " + dataBase.executeUpdateQuery(input) + " rows edited");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Syntax Error");
				}
			} else if (lower.contains("select")) {
				try {
					Object[][] content = dataBase.executeQuery(input);
					if (content == null)
						System.out.println("No DataBaseSelected");
					else {
						if (content.length == 0) {
							System.out.println("empty set");
						} else {

							for (int i = 0; i < content.length; i++) {
								System.out.println("----------------------------------");
								for (int j = 0; j < content[0].length; j++) {
									System.out.print("| " + content[i][j].toString());
								}
								System.out.println();
							}
							System.out.println("----------------------------------");
						}
					}
				} catch (SQLException e) {
					System.out.println("syntax error");

				}
			}else 
				System.out.println("syntax error");
			System.out.print("SQL>");
			input = userInput.nextLine();
		}
	}

	private String dataBaseUsed(String query) {
		Pattern p = Pattern.compile("(?i)(?<=use\\s{1,100})\\w+(?=\\s*)");
		Matcher m = p.matcher(query);
		if (m.find())
			return m.group();
		else
			return null;

	}
}
