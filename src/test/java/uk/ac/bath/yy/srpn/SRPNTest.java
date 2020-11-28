//package uk.ac.bath.yy.srpn;
//
//import org.junit.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//public class SRPNTest {
//    @Test
//    public void testTwoNumbers() {
//        SRPN srpn = new SRPN();
//        int result;
//        srpn.processCommand("10");
//        assertThat(result).isEqualTo(0);
//        result = srpn.processCommand("2");
//        assertThat(result).isEqualTo(0);
//        result = srpn.processCommand("+");
//        assertThat(result).isEqualTo(0);
//        result = srpn.processCommand("=");
//        assertThat(result).isEqualTo(12);
//
//    }
//
//}