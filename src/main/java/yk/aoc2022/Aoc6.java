package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;

import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.14
 */
public class Aoc6 {

    public static void main(String[] args) {
        System.out.println("Result1: " + detectSignal(IO.readFile("src/main/java/yk/aoc2022/aoc6.txt"), 4));
        System.out.println("Result1: " + detectSignal(IO.readFile("src/main/java/yk/aoc2022/aoc6.txt"), 14));
    }

    public static int detectSignal(String input, int len) {
        YList<String> buffer = al();
        return forString(input, (i, s) -> {
            buffer.add(s);
            if (buffer.size() > len) buffer.remove(0);
            if (buffer.toSet().size() == len) return i + 1;
            return null;
        }).orElse(-1);
    }

    @Test
    public void test1() {
        assertEquals(-1, detectSignal("", 4));
        assertEquals(-1, detectSignal("mjq", 4));
        assertEquals(-1, detectSignal("mjqm", 4));
        assertEquals(7, detectSignal("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 4));
        assertEquals(5, detectSignal("bvwbjplbgvbhsrlpgdmjqwftvncz", 4));
        assertEquals(6, detectSignal("nppdvjthqldpwncqszvftbrmjlhg", 4));
        assertEquals(10, detectSignal("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 4));
        assertEquals(11, detectSignal("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 4));
    }

    @Test
    public void test2() {
        assertEquals(-1, detectSignal("", 14));
        assertEquals(-1, detectSignal("abc", 14));
        assertEquals(-1, detectSignal("abca", 14));

        assertEquals(19, detectSignal("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 14));
        assertEquals(23, detectSignal("bvwbjplbgvbhsrlpgdmjqwftvncz", 14));
        assertEquals(23, detectSignal("nppdvjthqldpwncqszvftbrmjlhg", 14));
        assertEquals(29, detectSignal("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 14));
        assertEquals(26, detectSignal("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 14));
    }

    public static <T> Optional<T> forString(String s, BiFunction<Integer, String, T> consumer) {
        for (int i = 0; i < s.length(); i++) {
            T t = consumer.apply(i, s.charAt(i) + "");
            if (t != null) return Optional.of(t);
        }
        return Optional.empty();
    }

}
