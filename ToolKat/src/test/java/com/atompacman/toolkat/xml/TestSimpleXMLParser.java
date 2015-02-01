package com.atompacman.toolkat.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.toolkat.test.TestFileDetector;
import com.atompacman.toolkat.xml.NodeContent;
import com.atompacman.toolkat.xml.NodeStructure;
import com.atompacman.toolkat.xml.SimpleXMLParser;
import com.atompacman.toolkat.xml.SimpleXMLParserException;

public class TestSimpleXMLParser {

	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void prepareTestClass() {
		TestFileDetector.setPackagePathToRemove("com.atompacman.toolkat");
		TestFileDetector.setTestDirectory("test");
	}
	
	
	
	//====================================== UNIT TESTS ==========================================\\

	@Test
	public void singleNode() throws SimpleXMLParserException {
		String testFile = TestFileDetector.detectSingleFileForCurrentTest();
		
		NodeStructure rootNode = new NodeStructure("Node");
		
		NodeContent fileContent = SimpleXMLParser.parse(testFile, rootNode);
		
		assertEquals(fileContent.getNbAttributes(), 0);
		assertEquals(fileContent.getChildNodes().size(), 0);
	}
	
	@Test
	public void singleNodeWithAtt() throws SimpleXMLParserException {
		String testFile = TestFileDetector.detectSingleFileForCurrentTest();
		
		NodeStructure rootNode = new NodeStructure("Node", "attribute");
		
		NodeContent fileContent = SimpleXMLParser.parse(testFile, rootNode);
		
		assertEquals(fileContent.getNbAttributes(), 1);
		String attValue = fileContent.getAttributeValue("attribute");
		assertTrue(attValue != null);
		assertEquals(attValue, "hello");
	}

	@Test
	public void simpleList() throws SimpleXMLParserException {
		String testFile = TestFileDetector.detectSingleFileForCurrentTest();
		
		NodeStructure rootNode = new NodeStructure("SimpleList");
		rootNode.addChildNode(new NodeStructure("Element", "id"));
		
		NodeContent fileContent = SimpleXMLParser.parse(testFile, rootNode);
		
		assertEquals(fileContent.getChildNodes().size(), 7);
		assertEquals(fileContent.getChildNodes().get(3).getAttributeValue("id"), "3");
	}
	
	@Test
	public void heterogenList() throws SimpleXMLParserException {
		String testFile = TestFileDetector.detectSingleFileForCurrentTest();
		
		NodeStructure rootNode = new NodeStructure("HeterogenList");
		rootNode.addChildNode(new NodeStructure("ElementA", "id"));
		rootNode.addChildNode(new NodeStructure("ElementB", "id"));
		rootNode.addChildNode(new NodeStructure("ElementC", "id"));
		
		NodeContent fileContent = SimpleXMLParser.parse(testFile, rootNode);
		
		assertEquals(fileContent.getChildNodes().get(2).getName(), "ElementA");
		assertEquals(fileContent.getChildNodes().get(4).getName(), "ElementB");
		assertEquals(fileContent.getChildNodes().get(6).getName(), "ElementC");
	}
	
	@Test
	public void recurrentName() throws SimpleXMLParserException {
		String testFile = TestFileDetector.detectSingleFileForCurrentTest();
		
		NodeStructure level1 = new NodeStructure("Goglu");
		NodeStructure level2 = new NodeStructure("Goglu", "yolo");
		NodeStructure level3 = new NodeStructure("Goglu", "wololo");
		NodeStructure level4 = new NodeStructure("Goglu");

		level1.addChildNode(level2);
		level2.addChildNode(level3);
		level3.addChildNode(level4);
		
		NodeContent fileContent = SimpleXMLParser.parse(testFile, level1);
		
		assertEquals(fileContent.getName(), "Goglu");
		NodeContent child = fileContent.getChildNodes().get(0);
		assertEquals(child.getName(), "Goglu");
		assertEquals(child.getAttributeValue("yolo"), "yes");
		child = child.getChildNodes().get(0);
		assertEquals(child.getName(), "Goglu");
		assertEquals(child.getAttributeValue("wololo"), "yes");
		child = fileContent.getChildNodes().get(0);
		assertEquals(child.getName(), "Goglu");
	}
	
	@Test
	public void completeTest() throws SimpleXMLParserException {
		String testFile = TestFileDetector.detectSingleFileForCurrentTest();
		
		NodeStructure rootNode = new NodeStructure("Goglu");
		NodeStructure bananeNode = new NodeStructure("banane", "role");
		NodeStructure serpentNode = new NodeStructure("Serpent", "taste");
		NodeStructure yoloNode = new NodeStructure("yolo");
		NodeStructure yodaNode = new NodeStructure("yoda", "coolness");
		
		rootNode.addChildNode(bananeNode);
		bananeNode.addChildNode(serpentNode);
		bananeNode.addChildNode(bananeNode);
		bananeNode.addChildNode(yodaNode);
		bananeNode.addChildNode(yoloNode);
		
		NodeContent fileContent = SimpleXMLParser.parse(testFile, rootNode);
		
		assertEquals(
				fileContent.getChildNodes().get(1).
				getChildNodes().get(0).
				getChildNodes().get(4).
				getAttributeValue("coolness"), "9600");
	}
}