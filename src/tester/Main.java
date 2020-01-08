package tester;

import linux.util.file.Cat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static boolean verboseEnabled = false; /* default value set */
    private static boolean showLineNumber = false; /* default value set */
    private static boolean showTotalCharacterCount = false; /* default value set */
    private static int     dataRepresentationNotation = -1; /* default value set, -1 denotes unicode characters to be used */
    private static final List<Path> fileList = new ArrayList<>();
    
    
    public static void main(String[] args) {
        try {
            setOptions(args);
            try { 
                new Cat(verboseEnabled, showLineNumber, showTotalCharacterCount,
                        dataRepresentationNotation, fileList)
                            .readAndPrint();
            } catch(Exception e) { 
                throw new Exception(e); 
            }
        } catch(IllegalArgumentException e) {
            System.err.println("Error: Invalid argument: " + e.getMessage());
            System.exit(StandardExitCodes.ERROR);
        } catch(IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            System.exit(StandardExitCodes.FILE);
        } catch(Throwable t) {
            Throwable errorToDisplay = t;
            Throwable causeError = t.getCause();
            if(causeError != null)
                errorToDisplay = causeError;
            System.err.println("Fatal Error: Unknown application error");
            System.err.println("!Contact developers!");
            System.err.printf("Error detail: %s (%s) %n", errorToDisplay.getClass().getSimpleName(), errorToDisplay.getMessage());
            System.err.println("Full error stacktrace:");
            errorToDisplay.printStackTrace(System.err);
            System.exit( StandardExitCodes.FATAL );
        }
    }
    
    /**
     * @throws IllegalArgumentException For invalid option.
     */
    private static void setOptions(final String[] args) throws IllegalArgumentException {
        /* Fail-fast mechanism implemented */
        /* Check for valid options */
        for(String arg : args) {
            switch(arg) {
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
        if(fileList.isEmpty())
            fileList.add(null); /* denoting System.in */
    }
    
    private static void printHelpMenuAndExit() {
        System.out.printf(
                "Purpose:  Concatenates files and prints on the standard output \n" +
                "Usage:    Cat [-<option1>[ -<option2...>]] [filename1[ filename2...]] \n" +
                "Version:  %.2f \n" +
                "Options: \n" +
                "    --verbose,     -v     Enables verbose mode \n" +
                "    --line-nos,    -n     Shows line numbers \n" +
                "    --char-count,  -c     Shows character counts \n" +
                "    --binary,      -2     Shows file contents in binary representation instead of unicode characters \n" +
                "    --octal,       -8     Shows file contents in octal representation instead of unicode characters \n" +
                "    --decimal,     -10    Shows file contents in decimal representation instead of unicode characters \n" +
                "    --hexadecimal, -16    Shows file contents in hexadecimal representation instead of unicode characters \n" +
                "    --help,        -h     Shows this help menu \n",
                Cat.APP_VERSION);
        StandardExitCodes.showMessage();
        System.exit(StandardExitCodes.NORMAL);
    }
}
