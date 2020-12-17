package cw.calculator;

import java.io.*;

public class Main {
    /**
     * {@link Calculator#processLine(String)}
     * @param args
     */
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while(true) {
                String command = reader.readLine();
                //Close on an End-of-file (EOF) (Ctrl-D on the terminal)
                if(command == null){
                    //Exit code 0 for a graceful exit
                    System.exit(0);
                }
                //Otherwise, (attempt to) process the character
                calculator.processLine(command);
            }
        }
        catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
