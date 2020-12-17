package cw.calculator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.system.OutputCaptureRule;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {
    @Rule
    public OutputCaptureRule output = new OutputCaptureRule();

    private Calculator sut;

    @Before
    public void setup() {
        sut = new Calculator();
    }
    private String perform(String... lines) {
        Arrays.stream(lines).forEach(sut::processLine);
        return output.toString().trim();
    }
    private void check(String actual, String... expected) {
        assertThat(actual).isEqualTo(String.join("\n", expected));
    }

    @Test
    public void testSuite1_1(){
        check(perform("10", "2", "+", "="), "12");
    }
    @Test
    public void testSuite1_2(){
        check(perform("11", "3", "-", "="), "8");
    }
    @Test
    public void testSuite1_3(){
        check(perform("9", "4", "*", "="), "36");
    }
    @Test
    public void testSuite1_4(){
        check(perform("11", "3", "/", "="), "3");
    }
    @Test
    public void testSuite1_5(){
        check(perform("11", "3", "%", "="), "2");
    }

    @Test
    public void testSuite2_1(){
        check(perform("3", "3", "*", "4", "4", "*", "+", "="), "25");
    }
    @Test
    public void testSuite2_2(){
        check(perform("1234", "2345", "3456", "d", "+", "d", "+", "d", "="),
                "1234", "2345", "3456", "1234", "5801", "7035", "7035");
    }

    @Test
    public void testSuite3_1(){
        check(perform("2147483647", "1", "+", "="), "2147483647");
    }
    @Test
    public void testSuite3_2(){
        check(perform("-2147483647", "1", "-", "=", "20", "-", "="),
                "-2147483648", "-2147483648");
    }
    @Test
    public void testSuite3_3(){
        check(perform("100000", "0", "-", "d", "*", "="),
                "100000", "Stack underflow.", "100000");
    }

    @Test
    public void testSuite4_1(){
        check(perform("1", "+"), "Stack underflow.");
    }
    @Test
    public void testSuite4_2(){
        check(perform("10", "5", "-5", "+", "/"), "Divide by 0.");
    }
    @Test
    public void testSuite4_3(){
        check(perform("11+1+1+d"), "Stack underflow.", "13");
    }
    @Test
    public void testSuite4_4(){
        check(perform("# This i s a comment #", "1 2 + # And so i s t h i s #", "d"),
                "3");
    }
    @Test
    public void testSuite4_5(){
        check(perform("3 3 ^ 3 ^ 3 ^="), "3");
    }
    @Test
    public void testSuite4_6(){
        check(perform("r r r r r r r r r r r r r r r r r r r r r r d r r r d"), "1804289383", "846930886", "1681692777", "1714636915", "1957747793", "424238335", "719885386", "1649760492", "596516649", "1189641421",
                "1025202362", "1350490027", "783368690", "1102520059", "2044897763", "1967513926", "1365180540", "1540383426", "304089172", "1303455736", "35005211", "521595368",
                "Stack overflow.", "Stack overflow.", "1804289383", "846930886", "1681692777", "1714636915", "1957747793", "424238335", "719885386", "1649760492", "596516649", "1189641421",
                "1025202362", "1350490027", "783368690", "1102520059", "2044897763", "1967513926", "1365180540", "1540383426", "304089172", "1303455736", "35005211", "521595368", "1804289383");
    }

    @Test
    public void testUnrecognised_1() {
        check(perform("a"), "Unrecognised operator or operand \"a\".");
    }
    @Test
    public void testUnrecognised_2() {
        check(perform("1."), "Unrecognised operator or operand \".\".");
    }
    @Test
    public void testUnrecognised_3() {
        check(perform("1.2"), "Unrecognised operator or operand \".\".");
    }
    @Test
    public void testUnrecognised_4() {
        check(perform("abc"),
                "Unrecognised operator or operand \"a\".", "Unrecognised operator or operand \"b\".", "Unrecognised operator or operand \"c\".");
    }

    @Test
    public void experiment002() {
        check(perform("11+1+1"), "13");
    }
    @Test
    public void experiment003() {
        check(perform("11-2+1"), "8");
    }
    @Test
    public void experiment004() {
        check(perform("3*4/2+1", "="), "7");
    }
    @Test
    public void experiment005() {
        check(perform("3+5*2/4", "="), "5");
    }
    @Test
    public void experiment006() {
        check(perform("3 5 2 4 +*/", "="), "5");
    }
    @Test
    public void experiment007() {
        check(perform("11 6 3 2 5 +/-*", "="), "-9");
    }
    @Test
    public void experiment008() {
        check(perform("4 5 9 3 *-/", "d"), "3");
    }
}