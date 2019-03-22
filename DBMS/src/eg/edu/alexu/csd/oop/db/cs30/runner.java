package eg.edu.alexu.csd.oop.db.cs30;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.SAXException;



public class runner {

	public static void main(String[] args) {
		
		FileBuilder build= FileBuilder.getInstance();
build.createDB("kimo4");
build.createDB("kimo5");
build.createDB("kimo6");

	}
	
}

