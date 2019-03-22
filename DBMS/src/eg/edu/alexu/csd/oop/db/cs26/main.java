package eg.edu.alexu.csd.oop.db.cs26;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Assert;

import eg.edu.alexu.csd.oop.db.cs30.FileBuilder;
import eg.edu.alexu.csd.oop.db.cs47.DataTable;
import parser.syntaxChecker;

public class main {

	public static void main(String[] args) {
		DatabaseImp db = new DatabaseImp();
		db.createDatabase("A7AAA", true);
		syntaxChecker checker = new syntaxChecker();
		try {
			db.executeStructureQuery("CREATE TABLE table_name1(column_name1 varchar, column_name2 int, column_name3 varchar)");
			boolean created = db.executeStructureQuery("CREATE DATABASE TestDB");
			Assert.assertEquals("Failed to create TestDB internally using query", true, created);

			db.executeStructureQuery("CREATE TABLE table_name2(column_name1 varchar, column_name2 int, column_name3 varchar)");

			int count1 = db.executeUpdateQuery("INSERT INTO table_name2(column_NAME1, COLUMN_name3, column_name2) VALUES ('value1', 'value3', 4)");
			Assert.assertNotEquals("Insert returned zero rows", 0, count1);
			int count2 = db.executeUpdateQuery("INSERT INTO table_name2(column_NAME1, COLUMN_name3, column_name2) VALUES ('value1', 'value3', 5)");
			Assert.assertNotEquals("Insert returned zero rows", 0, count2);
			int count3 = db.executeUpdateQuery("INSERT INTO table_name2(column_name1, COLUMN_NAME3, column_NAME2) VALUES ('value2', 'value4', 6)");
			Assert.assertNotEquals("Insert returned zero rows", 0, count3);

			int count4 = db.executeUpdateQuery("DELETE From table_name2  WHERE coLUmn_NAME1 = VAluE1");
			Assert.assertEquals("Delete returned wrong number", 2, count4);

			int count5 = db.executeUpdateQuery("UPDATE table_name2 SET column_name1= 11111111, COLUMN_NAME2=22222222, column_name3=333333333 WHERE coLUmn_NAME2=4");
			Assert.assertEquals("Update returned wrong number", 0, count5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("fuck again");
			e.printStackTrace();
		}

	}

}
