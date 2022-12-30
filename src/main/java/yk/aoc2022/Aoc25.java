package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.IO;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.stringToCharacters;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;

/**
 * Created by yuri at 2022.12.28
 */
public class Aoc25 {
    public static final YMap<Character, Integer> FROM_SNAFU = hm(
            '2', 2,
            '1', 1,
            '0', 0,
            '-', -1,
            '=', -2
    );

    public static final YMap<Integer, Character> TO_SNAFU = hm(
            2  , '2',
            1  , '1',
            0  , '0',
            -1 , '-',
            -2 , '='
    );

    @Test
    public void testA() {
        YArrayList<YArrayList<String>> readPares = al(IO.readFile("src/main/java/yk/aoc2022/aoc25.testA.txt").split("\n")).cdr().map(l -> l.replaceAll(" +", " ").trim()).map(l -> al(l.split(" ")));

        for (YArrayList<String> pare : readPares) {
            long dec = Long.parseLong(pare.first());
            String snafu = pare.last();
            assertEquals(snafu, toSnafu(dec));
            assertEquals(dec, fromSnafu(snafu));
        }
    }

    @Test
    public void testB() {
        YArrayList<YArrayList<String>> readPares = al(IO.readFile("src/main/java/yk/aoc2022/aoc25.testB.txt").split("\n")).cdr().map(l -> l.replaceAll(" +", " ").trim()).map(l -> al(l.split(" ")));

        for (YArrayList<String> pare : readPares) {
            long dec = Long.parseLong(pare.last());
            String snafu = pare.first();
            assertEquals(snafu, toSnafu(dec));
            assertEquals(dec, fromSnafu(snafu));
        }

    }

    @Test
    public void answer1() {
        YArrayList<String> nn = al(IO.readFile("src/main/java/yk/aoc2022/aoc25.txt").split("\n")).map(l -> l.trim());
        assertEquals("20==1==12=0111=2--20", toSnafu(nn.reduce(0L, (a, b) -> a + fromSnafu(b))));
    }

    @Test
    public void testNextSymbol() {
        assertEquals(0, maxSnafuDigit(-2));
        assertEquals(0, maxSnafuDigit(2));
        assertEquals(1, maxSnafuDigit(3));
        assertEquals(1, maxSnafuDigit(12));
        assertEquals(2, maxSnafuDigit(13));

        assertEquals("=", nextSymbol(0, -2));
        assertEquals("-", nextSymbol(0, -1));
        assertEquals("0", nextSymbol(0, 0));
        assertEquals("1", nextSymbol(0, 1));
        assertEquals("2", nextSymbol(0, 2));
        assertEquals("1=", nextSymbol(1, 3));
        assertEquals("1-", nextSymbol(1, 4));
        assertEquals("10", nextSymbol(1, 5));
        assertEquals("11", nextSymbol(1, 6));
        assertEquals("12", nextSymbol(1, 7));
        assertEquals("2=", nextSymbol(1, 8));
        assertEquals("2-", nextSymbol(1, 9));
        assertEquals("20", nextSymbol(1, 10));
        assertEquals("21", nextSymbol(1, 11));
        assertEquals("22", nextSymbol(1, 12));
        assertEquals("1==", nextSymbol(2, 13));
    }

    @Test
    public void testGetSnafu() {
        assertEquals("=", toSnafu(-2));
        assertEquals("0", toSnafu(0));
        assertEquals("1=", toSnafu(3));
        assertEquals("1==", toSnafu(13));
    }

    public static String toSnafu(long n) {
        return nextSymbol(maxSnafuDigit(n), n);
    }

    public static int maxSnafuDigit(long n) {
        if (n < 3) return 0;
        for (int i = 1; i< 100; i++) if (pow(5, i) * 2 + (pow(5, i)-1)/2 >= n) return i;
        throw BadException.shouldNeverReachHere();
    }

    public static String nextSymbol(int snafuPower, long number) {
        if (snafuPower == 0) return TO_SNAFU.get((int)number) + "";
        long p = (long) pow(5, snafuPower);
        long chosen = round((float)(number + p * 2) / p - 2);
        return TO_SNAFU.get((int)chosen) + nextSymbol(snafuPower - 1, number - chosen * p);
    }

    public static long fromSnafu(String s) {
        YList<Integer> numbers = stringToCharacters(s).map(c -> FROM_SNAFU.get(c));
        return numbers.mapWithIndex((i, n) -> (long) pow(5, numbers.size() - i - 1) * n)
                .reduce(0L, (a, b) -> a + b);
    }
}
