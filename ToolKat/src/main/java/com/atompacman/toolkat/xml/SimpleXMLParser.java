package com.atompacman.toolkat.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.atompacman.toolkat.exception.Throw;

public class SimpleXMLParser {

	//==================================== STATIC FIELDS =========================================\\

	private static Node currNode;
	private static NodeStructure currStructNode;
	private static NodeContent currContentNode;



	//==================================== STATIC METHODS ========================================\\

	//----------------------------------------- PARSE --------------------------------------------\\

	public static NodeContent parse(String fileName, NodeStructure rootNodeStructure) 
			throws SimpleXMLParserException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(fileName));

			currNode = doc.getFirstChild();
			currStructNode = rootNodeStructure;
			currContentNode = new NodeContent(rootNodeStructure.getName());

			parseNode();
		} catch (Exception e) {
			Throw.a(SimpleXMLParserException.class, "Could not "
					+ "parse file at \"" + fileName + "\"", e);
		}
		return currContentNode;
	}

	private static void parseNode() throws SimpleXMLParserException {
		verifyNodeName();
		verifyNbAttributes();
		parseAttributes();

		NodeList childNodes = currNode.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); ++i) {
			currNode = childNodes.item(i);
			if (!(currNode instanceof Element)) {
				continue;
			}
			String childName = currNode.getNodeName();

			NodeStructure parentStructure = currStructNode;
			NodeContent parentContent = currContentNode;

			currStructNode = currStructNode.getChildNode(childName);
			currContentNode = new NodeContent(childName);

			parseNode();

			parentContent.addChildNode(currContentNode);
			currStructNode = parentStructure;
			currContentNode = parentContent;
		}
	}

	private static void parseAttributes() throws SimpleXMLParserException {
		NamedNodeMap attributes = currNode.getAttributes();
		for (String attName : currStructNode.getAttributeNames()) {
			Node attValue = attributes.getNamedItem(attName);
			if (attValue == null) {
				Throw.a(SimpleXMLParserException.class, "Expected an attribute named \"" + 
						attName + "\" for node \"" + currNode.getNodeName() + "\"");
			}
			currContentNode.setAttributeValue(attName, attValue.getTextContent());
		}
	}

	private static void verifyNodeName() throws SimpleXMLParserException {
		if (!currNode.getNodeName().equals(currStructNode.getName())) {
			Throw.a(SimpleXMLParserException.class, "Expected a node named \"" + 
					currStructNode.getName() + "\" but got \"" + currNode.getNodeName() + "\"");
		}
	}

	private static void verifyNbAttributes() throws SimpleXMLParserException {
		int actualNbAtt = currNode.getAttributes().getLength();
		int expectNbAtt = currStructNode.getNbAttributes();
		if (actualNbAtt != expectNbAtt) {
			Throw.a(SimpleXMLParserException.class, "Expected \"" + expectNbAtt + 
					"\" attributes "+ "for node named \"" + currStructNode.getName() 
					+ "\" but got \"" + actualNbAtt + "\"");
		}
	}
}
