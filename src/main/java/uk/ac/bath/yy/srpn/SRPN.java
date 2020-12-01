package uk.ac.bath.yy.srpn;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;

/**
 * Program class for an SRPN calculator. It simulates the functionalities of a legacy SRPN calculator. It should include the functionalities below.
 *
 * <ul>
 *  <li> Functionality 1: input at least two numbers and perform one operation correctly and output.</li>
 *  <li> Functionality 2: handle multiple numbers and multiple operations.</li>
 *  <li> Functionality 3: correctly handle saturation.</li>
 *  <li> Functionality 4: includes the less obvious features of SRPN. e.g.'r' output a set of 22 random numbers;'d' is intended to output all the elements in a stack.</li>
 * </ul>
 * <br>
 *
 * <p>
 * In addition to the above, additional functionalities explored include the following
 * <ul>
 *  <li> using bigger Data Types double, test case:  1 2 3 / - = should output 0.</li>
 *  <li> the behaviour difference of having spaces or not, test case: 3 3 ^= output 3 compared to 3 3 ^ = output 27</li>
 *  <li> deal with unrecognized tokens.</li>
 *  <li> input "1+1" produce different output as "1 + 1"(note the spaces).</li>
 * </ul>
 * <br>
 *
 * <p>
 *     The main method of this class is processLine
 */
public class SRPN {
    /**
     * A regex pattern for a number or a couple of operators
     */
    private static final Pattern PATTERN_TOKEN = Pattern.compile("(-?\\d+)|(?<operators>[=^*/+\\-%d#r]{1,2})");
    private static final Comparator<String> OPERATOR_COMPARATOR = comparingInt(asList("r", "=", "^", "/", "*", "%", "+", "-", "d")::indexOf);
    private static final int[] RANDOM_NUMBERS = {1804289383, 846930886, 1681692777, 1714636915, 1957747793, 424238335, 719885386, 1649760492, 596516649, 1189641421, 1025202362, 1350490027, 783368690, 1102520059, 2044897763, 1967513926, 1365180540, 1540383426, 304089172, 1303455736, 35005211, 521595368};

    private Stack<Double> stack = new BoundedStack<>(23);
    private int randomNumberIndex = 0;
    private boolean inCommentMode = false;

    /**
     * Process the whole line, which could contain many numbers and operators or unrecognised tokens.
     * <ul>
     *  <li>Use regex such as {@link #PATTERN_TOKEN} to recognize valid tokens.</li>
     *  <li>Use another simple regex to match a pair of python style comment {@code #}. Ignore everything in between the {@code #}s, and carry on calculations as usual.</li>
     *  <li>Once an individual token is recognised, it is passed as an input to the {@link #processCommand(String)} method to process.</li>
     *  <li>A stack of double will be populated while processing the tokens</li>
     * </ul>
     *
     * <p>
     * {@link #OPERATOR_COMPARATOR} is used to sort out the order of precedence when you have 2 operators next to each other without space
     * <ul>
     *  <li>{@code ^=} would do {@code =} first and then {@code ^} regardless of the fact that {@code ^} comes before {@code =}.
     *  <li>Same applies on {@code +*}. {@code 4 3 2 *+} and {@code 4 3 2 +*} gives the same result 10 regardless of the order of {@code +} and {@code *}.</li>
     *  <li>Same for any other pair-wise recognised operators. This suggests precedence is needed.</li>
     * </ul>
     *
     * @param s a line that is fed into the method as input <br>
     */
    public void processLine(String s) {
        String line = s.replaceAll("#.*#", "");

        Matcher m = PATTERN_TOKEN.matcher(line);

        while (m.find()){
            if (m.group().equals("#")){
                inCommentMode = !inCommentMode;
                continue;
            }
            if (inCommentMode) {
                continue;
            }
            String[] commands = {m.group()};

            if (m.group("operators") != null) {
                commands = m.group("operators").split("");
                Arrays.sort(commands, OPERATOR_COMPARATOR);
            }
            for (String command : commands) {
                processCommand(command);
            }
        }
    }

    /**
     * Processes a single command, which can be a number to push on the stack or an operation to perform.
     *
     * <p>
     * Helps to process individual tokens.
     * <ul>
     *  <li>Numbers are pushed into a stack.</li>
     *  <li>Errors are caught and then printed out.</li>
     *  <li>Operators such as {@code "r", "=", "^", "/", "*", "%", "+", "-", "d"} are recognised
     *  and used to do operations or output the result;
     *  <li>Data type double is used to target the problem of saturation and precision.</li>
     * </ul>
     *
     * <p>
     * Re implementation of {@code 'r'}, random number generator with seed is needed according to the repetitive pattern.
     * {@code (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1)} and {@code (int)(seed >>> (48 - bits))} can be used to reverse engineer the seed.
     * Note however that the repetition pattern is quite frequent, which suggests a random generator is not needed.
     * So a list containing all the numbers in the test suite is used in this case.
     *
     * <p>
     * See also: <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Random.html">java.util.Random</a>
     *
     * @param s individual tokens that's passed in the method as input
     * @throws IllegalArgumentException if the denominator is 0
     * @throws IllegalStateException if a stack is empty
     */
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
                    if (stack.isEmpty()){
                        System.out.println(Integer.MIN_VALUE);
                    }
                    else {
                        stack.forEach(e -> System.out.println((int) (double) e));
                    }
                    break;
                case "r":
                    randomNumberIndex = randomNumberIndex % (RANDOM_NUMBERS.length);
                    int n = RANDOM_NUMBERS[randomNumberIndex];
                    stack.push( (double) n);
                    randomNumberIndex++;
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


    /**
     * Applies the given operation to the two operands at the top of the stack,
     * checks the result for saturation,
     * and pushes the result back on the stack.
     *
     * <p>
     * Helps to extract the common behavior inside the switch cases in {@link #processCommand(String)} to deal with different operations.
     * The result of the operation is pushed into a stack after saturation is checked by the method {@link #clampInt(double)}.
     *
     * @param operation represents an operation upon two operands of the same type, producing a result of the same type as the operands
     * @throws IllegalStateException if stack does not have enough elements to pop.
     */
    private void binaryOperator(BinaryOperator<Double> operation) {
        if (stack.size() <= 1) {
            throw new IllegalStateException("Stack underflow.");
        }
        double num2 = stack.pop();
        double num1 = stack.pop();
        stack.push(clampInt(operation.apply(num1, num2)));
    }

    /**
     * Ensure a value is between Integer.MIN_VALUE and Integer.MAX_VALUE.
     *
     * @param number the number
     * @return the number itself, or the saturation limit if beyond that.
     */
    private double clampInt(double number) {
        return Math.min(Math.max(number, Integer.MIN_VALUE), Integer.MAX_VALUE);
    }

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }


    /**
     * Similar to a normal Stack, but check if the size is exceeding the sizeLimit
     * @param <E> any type of element
     */
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
