package uk.ac.bath.yy.srpn;

import java.io.*;

public class Main {
    // main method
    // reads in input from the command line
    // and passes this input to the processCommand method in uk.ac.bath.yy2376.srpn.SRPN

    /**
     * {@link SRPN#processLine(String)}
     * @param args
     */
    public static void main(String[] args) {
        // Code to take input from the command line
        // This input is passed to the processLine, processCommand
        // method in uk.ac.bath.yy2376.srpn.SRPN.java
        SRPN srpn = new SRPN();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            //Keep on accepting input from the command-line
            while(true) {
                String command = reader.readLine();
                //Close on an End-of-file (EOF) (Ctrl-D on the terminal)
                if(command == null){
                    //Exit code 0 for a graceful exit
                    System.exit(0);
                }
                //Otherwise, (attempt to) process the character
                srpn.processLine(command);
//                srpn.processCommand(command);
            }

        }
        catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
