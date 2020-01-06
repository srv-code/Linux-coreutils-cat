// Modelled after linux command: cat

package linux.util;

import linux.ExitValue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Cat
{
	private final float appVersion = 0.2f;
	private boolean 	verboseEnabled = false, 		// -v : Enables verbose mode
						showLineNumber = false, 	 	// -n : Shows line numbers
						showTotalCharacterCount = false;	// -c : Shows character counts//
	private int dataRepresentationNotation = -1; // -1 denotes ASCII characters to be used (default), any other representations will assign a new base value internally
	private final ArrayList<Path> fileList = new ArrayList<>();
	
	Cat() {} // Just to make it package-private
	
	private void setOptions(final String[] args)
			throws
				IllegalArgumentException   // for invalid option
	{
		// Fail-fast mechanism implemented
		// Check for valid options
		for(String arg : args)
		{
			switch(arg)
			{
				case "-v":
				case "--version":
					verboseEnabled = true;
					break;
				case "-n":
				case "--show-line-nos":
					showLineNumber = true;
					break;
				case "-c":
				case "--show-char-count":
					showTotalCharacterCount = true;
					break;
				case "-2":
				case "--binary":
					dataRepresentationNotation = 2;
					break;
				case "-8":
				case "--octal":
					dataRepresentationNotation = 8;
					break;
				case "-10":
				case "--decimal":
					dataRepresentationNotation = 10;
					break;
				case "-16":
				case "--hexadecimal":
					dataRepresentationNotation = 16;
					break;
				case "-h":
				case "--help":
					printHelpMenuAndExit();
				default:
					Path file = Paths.get(arg);
					if(Files.notExists(file))
						throw new IllegalArgumentException("File cannot be located: " + file);
					if(Files.isDirectory(file))
						throw new IllegalArgumentException("Cannot operate on a directory: " + file);
					fileList.add(file);
			}
		}
		if(fileList.size() == 0)
			fileList.add(null); // denoting System.in
	}
	
	private void printHelpMenuAndExit()
	{
		System.out.printf(
				"Purpose: Concatenates files and prints on the standard output \n" +
				"Usage: Cat [-<option1>[ -<option2...>]] [filename1[ filename2...]] \n" +
				"Version %.2f \n" +
				"Options: \n" +
				"  -v / --verbose : Enables verbose mode \n" +
				"  -n / --show-line-nos : Shows line numbers \n" +
				"  -c / --show-char-count : Shows character counts \n" +
				"  -2 / --binary : Shows file contents in binary representation instead of ASCII characters \n" +
				"  -8 / --octal : Shows file contents in octal representation instead of ASCII characters \n" +
				"  -10 / --decimal : Shows file contents in decimal representation instead of ASCII characters \n" +
				"  -16 / --hexadecimal : Shows file contents in hexadecimal representation instead of ASCII characters \n" +
				"  -h / --help : Shows this help menu \n",
				appVersion);
		ExitValue.showMessage();
		System.exit(ExitValue.NORMAL);
	}
	
	private void readAndPrint()
			throws IOException // In case any exception occurs while opening / reading the file
	{
		final String horizontalLineForIndividual = "-----------------------------";
		final String horizontalLineForTotal = "==============================";
		long 	individualCharCount = 0, totalCharCount = 0,
				individualLineCount = 0, totalLineCount = 0;
		for(Path file : fileList)
		{
			try (BufferedInputStream stream = new BufferedInputStream((file==null)?System.in:(Files.newInputStream(file)))) {
				if(verboseEnabled)
					System.out.printf("[Source: %s]: \n", ((file==null)?"<Standard Input Stream>":file));
				int readByte;
				boolean flagNewLineFound = true;
				while( (readByte=stream.read()) != -1 ) {
					if(showLineNumber)
						if(flagNewLineFound) {
							System.out.printf("%6d  ",  (verboseEnabled?individualLineCount:totalLineCount)+1);
							flagNewLineFound = false;
						}
					if(dataRepresentationNotation == -1)
						System.out.print((char)readByte);
					else {
						if(readByte == '\n')
							System.out.println();
						else
							System.out.print(Integer.toString(readByte, dataRepresentationNotation));
					}
					individualCharCount++;
					if(readByte == '\n') {
						individualLineCount++;
						totalLineCount++;
						flagNewLineFound = true;
					}
				}
				
				if(verboseEnabled)
					System.out.printf("%s \n" +
							"[Line count: %d] \n" +
							"[Character count: %d] \n" +
							"%1$s \n\n",
							horizontalLineForIndividual, individualLineCount, individualCharCount);
				totalCharCount += individualCharCount;
				individualLineCount = individualCharCount = 0;
			}
		}
		if(verboseEnabled || showTotalCharacterCount)
			System.out.printf("%s \n" +
					"[Total source count: %d] \n" +
					"[Total line count: %d] \n" +
					"[Total character count: %d]\n",
					horizontalLineForTotal, fileList.size(), totalLineCount, totalCharCount);
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		Cat cat = null;
		try {
			cat = new Cat();
			cat.setOptions(args);
			try { cat.readAndPrint(); }
			catch(Exception e) { throw new Exception(e); }
		} catch(IllegalArgumentException e) {
			System.err.println("Error: Invalid argument: " + e.getMessage());
			System.exit(ExitValue.ERROR);
		} catch(IOException e) {
			System.err.println("I/O Error: " + e.getMessage());
			System.exit(ExitValue.FILE);
		} catch(Throwable t) {
			Throwable errorToDisplay = t;
			Throwable causeError = t.getCause();
			if(causeError != null)
				errorToDisplay = causeError;
			System.err.println("Fatal Error: Unknown application error");
			System.err.println("!Contact developers!");
			System.err.printf("Error detail: %s (%s) %n", errorToDisplay.getClass().getSimpleName(), errorToDisplay.getMessage());
			System.err.println("Full error stacktrace:");
			errorToDisplay.printStackTrace();
			System.exit( ExitValue.FATAL );
		}
	}
}
