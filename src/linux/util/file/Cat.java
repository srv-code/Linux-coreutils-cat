/* Modelled after linux command: cat */

package linux.util.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class Cat {
    public static final float APP_VERSION = 1.0f;
    
    private boolean  verboseEnabled;
    private boolean  showLineNumber;
    private boolean  showTotalCharacterCount;
    private int      dataRepresentationNotation; /* -1 denotes unicode characters to be used (default), any other representations will assign a new base value internally */
    private final ArrayList<Path> fileList = new ArrayList<>();
    
    
    /**
     * Main handler
     * @throws IOException In case any exception occurs while opening / reading the file
     */
    public void readAndPrint() throws IOException {
        final String horizontalLineForIndividual = "-----------------------------";
        final String horizontalLineForTotal = "==============================";
        long 	individualCharCount = 0, totalCharCount = 0,
                individualLineCount = 0, totalLineCount = 0;
        for(Path file : fileList) {
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
}
