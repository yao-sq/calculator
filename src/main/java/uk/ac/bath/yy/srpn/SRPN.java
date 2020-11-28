package uk.ac.bath.yy.srpn;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;

/**
 * Task: write a program which matches the functionality of SRPN as closely as possible, but not adding/enhancing existing features
 *
 * Notation:
 * RPN - reverse polish notation calculator
 * SRPN - with the extra feature that all arithmetic is saturated,
 * i.e. when it reaches the maximum value that can be stored in a variable, it stays at the maximum rather than wrapping around.
 *
 * Program class for an SRPN calculator
 *
 * Feature 1: input at least two numbers and perform one operation correctly and output.
 * Feature 2: handle multiple numbers and multiple operations.
 * Feature 3: correctly handle saturation. (my bug with * saturation)
 * Feature 4: includes the less obvious features of SRPN. These include but are not limited to
 *              Stack underflow +
 *              divide by 0 +
 *
 *
 *
 * Exploring if there are other features of the SRPN calculator that are not covered in the tests provided
 * the following cases produces different result acroos my implementation and legacy SRPN:
 * 1 - 2/3
 * 1/10 * 10
 * 10 * 1/10
 *
 * what does 'r' do?
 * what does 'd' do? -'d' is intended to output all the elements in a stack;
 * does the input "1+1" produce the same output as "1 + 1"(note the spaces)?
 *
 */

public class SRPN {
    private static final Pattern PATTERN_TOKEN = Pattern.compile("(-?\\d+)|(?<operators>[=^*/+\\-%d#]+)");
    private static final Comparator<String> OPERATOR_COMPARATOR = comparingInt(asList("=", "d", "^", "*", "/", "%", "+", "-")::indexOf);

    Stack<Double> stack = new Stack<>();
    boolean isComment;

    public boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }



//    public int checkSaturationForPlus(int num1, int num2, int result) {
//        if (num1>0 && num2>0 && result < 0)
//            return Integer.MAX_VALUE;
//        else if (num1<0 && num2<0 && result > 0)
//            return Integer.MIN_VALUE;
//        return result;
//    }


//    public int checkSaturationForMinus(int num1, int num2, int result) {
//        if (num1==Integer.MAX_VALUE && num2==Integer.MIN_VALUE)
//            return Integer.MAX_VALUE;
//        if (num1>0 && num2<0 && result <0)
//            return Integer.MAX_VALUE;
//        else if (num1<0 && num2>0 && result>0)
//            return Integer.MIN_VALUE;
//        return result;
//    }

    public void processCommand(String s) {
        if (!s.equals("=")) {
            if ( isNumeric(s)) {
                stack.push((double) Integer.parseInt(s));
            }
            else {
                double num2;
                double num1;
                double result;
                switch (s) {
                    case "d":
                        stack.forEach( e -> System.out.println((int) (double) e));
                        break;
                    case "^":
                        if (stack.size() <=1){
                            System.out.println("Stack underflow.");
                        }
                        else {
                            num2 = stack.pop();
                            num1 = stack.pop();
                            stack.push(clampInt(Math.pow( num1, num2)));
                        }
                        break;
                    case "+":
                        if (stack.size() <=1){
                            System.out.println("Stack underflow.");
                        }
                        else {
                            num2 = stack.pop();
                            num1 = stack.pop();
                            stack.push(clampInt(num1 + num2));
                        }
                        break;
                    case "-":
                        if (stack.size() <=1){
                            System.out.println("Stack underflow.");
                        }
                        else {
                            num2 = stack.pop();
                            num1 = stack.pop();
                            stack.push(clampInt(num1 - num2));
                        }
                        break;
                    case "*":
                        if (stack.size() <=1){
                            System.out.println("Stack underflow.");
                        }
                        else {
                            num2 = stack.pop();
                            num1 = stack.pop();
                            stack.push(clampInt(num1 * num2));
                        }
                        break;
                    case "/":
                        if (stack.size() <=1){
                            System.out.println("Stack underflow.");
                        }
                        else {
                            num2 = stack.pop();
                            num1 = stack.pop();
                            if ( (int) num2==0) {
                                System.out.println("Divide by 0.");
                            }
                            else{
                                stack.push(clampInt(num1 / num2));
                            }
                        }
                        break;
                    case "%":
                        if (stack.size() <=1){
                            System.out.println("Stack underflow.");
                        }
                        else {
                            num2 = stack.pop();
                            num1 = stack.pop();
                            stack.push(clampInt(num1 % num2));
                        }
                        break;
                }
            }
        } else {
            System.out.println((int) (double) stack.peek());
//            return stack.peek();

        }
    }

    private double clampInt(double number) {
        return Math.min(Math.max(number, Integer.MIN_VALUE), Integer.MAX_VALUE);
    }

    public void processLine(String s) {
        // String to be scanned to find the pattern
        String line = s;
        line = line.replaceAll("#.*#", "");


//        String patternToken = "(?<number>\\d*)?|(?<operator>\\+|-|\\*|/|%|=|d)?";
//        String patternToken = "(-?\\d+)|[=^*/+\\-%d#]+";

        // Now create Matcher object
        Matcher m = PATTERN_TOKEN.matcher(line);
        while (m.find()){
            if (m.group().equals("#")){
                isComment = !isComment;
                continue;
            }

            if (isComment) {
                continue;
            }

            String[] commands = {m.group()};
            if (m.group("operators") != null) {
                commands = m.group("operators").split("");
                Arrays.sort(commands, OPERATOR_COMPARATOR);
            }
            for (String command : commands) {
//                    System.out.println("Found: " + command);
                processCommand(command);
            }
        }

    }
}
