package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.net.*;
import java.io.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({ "unused" })
public class OBDII_Code_Finder {

	public static boolean intro = true;
	
	@SuppressWarnings("resource")
	public static void main(String args[]) throws IOException {
		
		Scanner userInput = new Scanner(System.in);
		List<String> headerNames = new ArrayList<String>();
		List<String> bodyText = new ArrayList<String>();
		
		String URL_Desc = "https://www.obd-codes.com/";
		String connectMode = "Online DB"; //Implemented later, currently only runs in onlineDB mode with phpMyAdmin
		String OBD_Code = "";
		String OBD_Code_Title = "";
		
		boolean hasSeverity = false;
		
		if (intro == true)
			doIntro(connectMode);
		
		boolean obdIsValid = false;
		
		System.out.print("Enter OBDII Code to check: ");
		String OBDII_Code = userInput.next(); //Collecting the OBDII code the user has entered
		System.out.print("Code Format: ");
		System.out.println(checkOBDFormat(OBDII_Code));
		
		if (checkOBDFormat(OBDII_Code).equals("OBDII Code is valid."))
			obdIsValid = true;
		
		if (obdIsValid != true)
			main(null);
		else
			OBD_Code = OBDII_Code;
		
		OBD_Code = OBD_Code.toUpperCase();
		
		checkCode(OBD_Code); //Until I add support for P0000 - P0099
		
		URL_Desc = URL_Desc + OBD_Code;
		String URL_For_Search = "https://alexprojects.000webhostapp.com/searchResults.php?obdii_codesearch=" + OBD_Code + "&submit=Search";
		
		headerNames = findHeaders(OBDII_Code, URL_Desc);
		hasSeverity = checkForSev(headerNames);
		
		if (connectMode.equals("Online DB")) {
			//OBD_Code_Title = findCodeTitleWithURL(OBDII_Code, URL_For_Search);
			//OBD_Code_Desc = findCodeSectionWithURL(OBDII_Code, URL_Desc, headerNames, headerNames.get(1));
		} else {
			System.out.println("Currently not implemented");
			main(null);
		}

		System.out.println("\nCode: " + OBD_Code + "\nTitle: " + findCodeTitleWithURL(OBD_Code, URL_For_Search) + "\n");
		
		bodyText.addAll(findCodeSectionWithURL(OBD_Code, URL_Desc, headerNames));
		
		for (int i = 0; i < headerNames.size() - 1; i++) {
			
			System.out.println(headerNames.get(i) + ": " + bodyText.get(i) + "\n");
			
		}
		
		System.out.print("Would you like to search another code?>");
		String choice = userInput.next();
		
		if (choice.equals("yes") || choice.equals("y"))
			main(null);
		else
			System.exit(0);

	}
	
	public static String findCodeTitleWithURL(String obd, String url) throws IOException {
		
		URL carOBDSearch = new URL(url);
		URLConnection carOBDSearchConnection = carOBDSearch.openConnection();
		
		BufferedReader carOBDSearchReader = new BufferedReader(new InputStreamReader(carOBDSearchConnection.getInputStream()));
		String inputLine = carOBDSearchReader.readLine();
		inputLine = inputLine.substring(6);

		carOBDSearchReader.close();
		
		return inputLine;
		
	}


	public static List<String> findCodeSectionWithURL(String obd, String url, List<String> headers) throws IOException {

		Elements unformattedText = null;
		Document doc = null;
		List<String> newBody = new ArrayList<String>();
		
		String urlForDesc = url;
		String codeSection = "";
		
		try {
			doc = Jsoup.connect(urlForDesc).get();
			unformattedText = doc.select(".main");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		for (int i = 0; i < headers.size() - 1; i++) {
			newBody.add(formatSection(unformattedText, headers.get(i), headers.get(i + 1)));
		}
		
		return newBody;
		
	}
	
	public static String formatSection(Elements text, String header1, String header2) {

		String rawText = text.text();
		String newText = "";
		
		int posToStart = 0;
		int posToEnd = 0;
		int tempCounter = 0;
		
		for (int i = 0; i < rawText.length(); i++) {
			
			if (rawText.substring(i, i + header1.length()).equals(header1)) {
				posToStart = i + header1.length() + 1;
				break;
			}
			
		}
		
		for (int i = 0; i < rawText.length(); i++) {
			
			if (rawText.substring(i, i + header2.length()).equals(header2)) {
				posToEnd = i;
				break;
			}
			
		}
		
		newText = rawText.substring(posToStart, posToEnd); 
		return newText;
		
	}
	
	@SuppressWarnings("static-access")
	public static String checkOBDFormat(String obd) {
		
		String charList = "pPbBcCuU";
		boolean obdIsValid = false;
		
		if (obd.length() != 5) {
			return "Incorrect code length";
		} //Check code length
		
		for (int i = 0; i < charList.length(); i++) {
			
			if (charList.valueOf(i).equals(obd.valueOf(0))) {
				obdIsValid = true;
				break;
			}
			
		}
		
		if (obdIsValid == true)
			return "OBDII Code is valid.";
		else
			return "OBDII Code is not valid.";
		
	}
	
	public static void doIntro(String mode) {
		System.out.println("Welcome to OBDII Lookup\n"
				+ "Created by: Alex Porter");	
		System.out.println("\nCurrently running in " + mode + "mode.");
		intro = false;
	}
	
	public static void checkCode(String code) throws IOException {
		
		int newCode = Integer.parseInt(code.substring(1));
		
		if (newCode >= 0 && newCode <=99) {
			System.out.println("\nCodes between P0000 - P0099 are currently not supported\n");
			main(null);
		}
		
	}
	
	public static boolean checkForSev(List<String> headers) {
		
		List<String> sevTitles = new ArrayList<String>();
		sevTitles.add("What is the severity of this DTC?");
		sevTitles.add("Symptoms / Severity");
		sevTitles.add("Severity");
		sevTitles.add("Code Severity");
		
		for (int i = 0; i < headers.size(); i++) {
			
			if (headers.get(i).contains("Severity") || headers.get(i).contains("severity")) {
				if (sevTitles.contains(headers.get(i))){
					return true;
				} else {
					return false;
				}
			}
			
		}
		
		return false;
		
	}
	
	public static List<String> findHeaders(String obd, String url) {
		
		List<String> headers = new ArrayList<String>();
		List<String> newHeaders = new ArrayList<String>();
		
		Elements unformattedHeaders = null;
		Document document = null;
		String urlForHeaders = url;
		
		boolean hasSev = checkForSev(headers);
		
		try {
			
			document = Jsoup.connect(urlForHeaders).get();
			unformattedHeaders = document.select("h2");
			
		}
		catch(Exception ex) {
			
			ex.printStackTrace();
			
		}
		
		for (int i = 0; i < unformattedHeaders.size(); i++) {
			
			headers.add(unformattedHeaders.get(i).text());
			
		}
		
		for (int i = 0; i < headers.size(); i++) {

			if (headers.get(i).contains("What does that mean?")) {
				newHeaders.add(headers.get(i));
			} else if (headers.get(i).contains("Symptoms") || headers.get(i).contains("symptoms")) {
				newHeaders.add(headers.get(i));
			} else if (headers.get(i).contains("Severity")) {
				if (hasSev == true) {
					newHeaders.add(headers.get(i));
				}
			} else if (headers.get(i).contains("Causes") || headers.get(i).contains("causes")) {
				newHeaders.add(headers.get(i));
				newHeaders.add(headers.get(i + 1));
			} 
			
		}
		
		return newHeaders;
		
	}
}
