package uk.ac.bath.yy.srpn;

import java.util.*;
import java.util.function.BinaryOperator;
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

//TODO: doc is for people who don't see the code
// pass tests & write tests instead of mannually typing them
// write comment & tidy up code
// check slack
// added r, but not sure of precendence

public class SRPN {
    private static final Pattern PATTERN_TOKEN = Pattern.compile("(-?\\d+)|(?<operators>[=^*/+\\-%d#r]+)");
    private static final Comparator<String> OPERATOR_COMPARATOR = comparingInt(asList("=", "^", "*", "/", "%", "+", "-", "d", "r")::indexOf);

    private Stack<Double> stack = new BoundedStack<>(23);
    private boolean isComment;

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public void processCommand(String s) {
        try {
            if (isNumeric(s)) {
                stack.push((double) Integer.parseInt(s));
                return;
            }
            switch (s) {
                case "=":
                    System.out.println((int) (double) stack.peek());
                    break;
                case "d":
                    stack.forEach(e -> System.out.println((int) (double) e));
                    break;
                case "r":
                    Random rand = new Random();
                    int n = rand.nextInt();
                    stack.push( (double) n);
                    break;
                case "^":
                    binaryOperator(Math::pow);
                    break;
                case "+":
                    binaryOperator(Double::sum);
                    break;
                case "-":
                    binaryOperator((num1, num2) -> num1 - num2);
                    break;
                case "*":
                    binaryOperator((num1, num2) -> num1 * num2);
                    break;
                case "/":
                    binaryOperator((num1, num2) -> {
                        if (num2 == 0) {
                            throw new IllegalArgumentException("Divide by 0.");
                        }
                        return num1 / num2;
                    });
                    break;
                case "%":
                    binaryOperator((num1, num2) -> num1 % num2);
                    break;
            }
        } catch (RuntimeException err) {
            System.out.println(err.getMessage());
        }
    }

    private void binaryOperator(BinaryOperator<Double> operation) {
        if (stack.size() <= 1) {
            throw new IllegalStateException("Stack underflow.");
        }
        double num2 = stack.pop();
        double num1 = stack.pop();
        stack.push(clampInt(operation.apply(num1, num2)));
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


    public static class BoundedStack<E> extends Stack<E> {
        private final int sizeLimit;
        public BoundedStack(int sizeLimit) {
            this.sizeLimit = sizeLimit;
        }

        @Override
        public E push(E item) {
            if ( this.size() >= sizeLimit){
                throw new IllegalStateException("Stack overflow.");
            }
            return super.push(item);
        }
    }
}
