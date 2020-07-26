package com.cybersecurity.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;
 

public class ConverterFactory implements XMLJSONConverterI {

	private static JSONParser jsonParser = new JSONParser();
	private static String nameTag = "name";
	private static String isInputFileValid; 
	private static String isFileGenerated; 
	private static String processStatus; 
	private static String processMsg;

	public Map createXMLJSONConverter(String jSonPath, String xmlPath) {

        Document doc = DocumentHelper.createDocument();
		try  {
 			FileReader reader = new FileReader(jSonPath);
			Object obj = jsonParser.parse(reader);
			String objType = getObjectType(obj);
			Element rootElement = doc.addElement(objType);
			formXML(obj, objType, doc, rootElement);
			generateFile(doc,xmlPath);
 		}  
		catch(ParseException e)
		{
			isInputFileValid = "No";
			processStatus ="Input File Error";
			isFileGenerated = "No";
			processMsg ="Invalid JSON file - "+e.getMessage();
 		} catch (Exception ex) {
			isInputFileValid = "No";
			processStatus ="General Error";
			processMsg = ex.getMessage();
 		}  
		return generateResponse(isInputFileValid, isFileGenerated, processStatus, processMsg);

		
	}

	public void generateFile(Document doc,String xmlPath) {
 		
		try
		{
        FileWriter out = new FileWriter(xmlPath);
        doc.write(out);
        out.close();
		isFileGenerated = "Yes";
		processStatus ="Success";
		processMsg = "XML File generated";
		} catch (Exception ex) {
		isInputFileValid = "Yes";
		isFileGenerated = "No";
		processStatus ="Output file generation error";
		processMsg = ex.getMessage();
		} 
        
	}

	public static String getObjectType(Object object) {
		// TODO Auto-generated method stub
		String name = "null";
		if (object != null)
			name = object.getClass().getSimpleName();

		if (name.equalsIgnoreCase("LONG"))
			name = "number";
		else if (name.equalsIgnoreCase("JSONArray"))
			name = "array";
		else if (name.equalsIgnoreCase("JSONObject"))
			name = "object";

		return name.toLowerCase();
	}

	public static void addChildNode(JSONArray jsonArray, Document doc, Element arrayElement, Element rootElement) {

		Iterator itr = jsonArray.iterator();

		while (itr.hasNext()) {
			Object val = itr.next();
			if (getObjectType(val).equals("object")) {
				Element subElement = arrayElement.addElement("object");
 				formXML(val, getObjectType(val), doc, subElement);
			} else {
				String nameItr = getObjectType((val));
				Element itEle = arrayElement.addElement(nameItr);
				if(val != null)
					itEle.addText(val.toString());
 			}

		}
 
	}

	public static void addChildNode(Object jsonObject, Document doc, Element objElement, Element rootElement) {

		if (jsonObject != null)
			objElement.addText((jsonObject.toString()));
 	}

	public static void formXML(Object obj, String objType, Document doc, Element rootElement) {
		isInputFileValid = "Yes";
		if (objType.equalsIgnoreCase("object")) {
			Set<String> keys = ((HashMap) obj).keySet();
			JSONObject jsonObject = (JSONObject) obj;

			keys.forEach(a -> {

				String name = getObjectType((jsonObject.get(a)));
				if (name.equalsIgnoreCase("array")) {
					Element arrayElement = rootElement.addElement(name);
					arrayElement.addAttribute(nameTag, a);// ("Hi");
					JSONArray jsonArray = (JSONArray) jsonObject.get(a);
 					addChildNode(jsonArray, doc, arrayElement, rootElement);
				} else if (name.equalsIgnoreCase("object")) {
					Element subElement = rootElement.addElement("object");
					subElement.addAttribute(nameTag, a);// ("Hi");
 					formXML((jsonObject.get(a)), getObjectType(jsonObject.get(a)), doc, subElement);
				} else {
					Element ObjElement = rootElement.addElement(name);
					ObjElement.addAttribute(nameTag, a);// ("Hi");
					addChildNode(jsonObject.get(a), doc, ObjElement, rootElement);
				}
			});
		} else {

			if (objType.equalsIgnoreCase("array")) {
				
				JSONArray jsonArray = (JSONArray) obj;

				addChildNode(jsonArray, doc, rootElement, rootElement);

				/*Iterator itr = jsonArray.iterator();
				while (itr.hasNext()) {
					Object val = itr.next();
					String nameItr = getObjectType((val));

					if (nameItr.equals("object")) {
						Element subElement = rootElement.addElement(nameItr);
		 				formXML(val, getObjectType(val), doc, subElement);
					} else {
						Element itEle = rootElement.addElement(nameItr);
						itEle.addText(val.toString());
		 			}
				}*/
			} else {
				if(obj != null)
					rootElement.addText(obj.toString());
			}
		}
	}
	
	public static Map generateResponse(String isInputFileValid,String isFileGenerated,String processStatus,String processMsg)
	{
		Map responseMap = new HashMap<String,String>();

		responseMap.put("isInputFileValid",isInputFileValid);
		responseMap.put("isFileGenerated", isFileGenerated);
		responseMap.put("processStatus", processStatus);
		responseMap.put("processMsg", processMsg);
		
		return responseMap;
	}
}