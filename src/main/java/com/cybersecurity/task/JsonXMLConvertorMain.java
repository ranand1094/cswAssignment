package com.cybersecurity.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JsonXMLConvertorMain {

	public static void main(String[] args) throws Exception {
		XMLJSONConverterI xmlJsonConverter = new ConverterFactory();
		ConverterFactory convertFactory = new ConverterFactory();
		Map report = new HashMap<String, String>();
		boolean error = false;
		String jSonPath = "Is either empy or not a vaild";
		String xmlPath = "Is either empy or not a vaild";

		if (args.length < 2) {
			error = true;
			report = convertFactory.generateResponse("No", "No", "Execution Error",
					"Enter Source Json file and Destination path with file name");
		}

		if (args.length > 2) {
			error = true;
			report = convertFactory.generateResponse("No", "No", "Execution Error",
					"Only two parameters are accepted as input");
		}

		if (args.length == 2) {
			jSonPath = args[0].replace("\\", "\\\\");
			xmlPath = args[1].replace("\\", "\\\\");
			if (!jSonPath.endsWith(".json")) {
				error = true;
				report = convertFactory.generateResponse("No", "No", "File Extension Mismatch",
						"Input file should be of JSON type only");
				jSonPath = "Not a valid file";
			}
			if (!xmlPath.endsWith(".xml")) {
				error = true;
				report = convertFactory.generateResponse("No", "No", "File Extension Mismatch",
						"Output file should be of XML type only");
				xmlPath = "Not a valid file";
			}
			
			if(!error)
				report = xmlJsonConverter.createXMLJSONConverter(jSonPath, xmlPath);
		}

		String isInputFileValid = (String) report.get("isInputFileValid");
		String isFileGenerated = (String) report.get("isFileGenerated");
		String processStatus = (String) report.get("processStatus");
		String processMsg = (String) report.get("processMsg");
		String reportOP = "*************************JSON to XML Convertion Report************************"
				+ "\n\tProcess Status \t\t\t:- " + processStatus + "\n\tProcess Message \t\t:- " + processMsg
				+ "\n\tInput File path \t\t:- " + jSonPath + "\n\tInput File Validation passed? \t:- "
				+ isInputFileValid + "\n\tOutput File Generated? \t\t:- " + isFileGenerated
				+ "\n\tOutput File path \t\t:- " + xmlPath
				+ "\n********************************END of Report********************************";
		System.out.println(reportOP);
	}

}
