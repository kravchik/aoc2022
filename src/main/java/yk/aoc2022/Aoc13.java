package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;
import yk.lang.yads.Yads;

import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.INT_ADD;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.20
 */
public class Aoc13 {

    public static void main(String[] args) {
        YList<YList<Object>> pares = readPares("aoc13.txt");

        System.out.println("Result: " + countOrdered(pares));

        YList<Object> all = pares
                .flatMap(p -> p)
                .with(al(al(2)))
                .with(al(al(6)))
                .sorted(Aoc13::comparePackets);
        int res = (all.indexOf(al(al(2))) + 1) * (all.indexOf(al(al(6))) + 1);
        assertEquals(24948, res);
        System.out.println("Result2: " + res);
    }

    @Test
    public void test1() {
        assertEquals(13, countOrdered(readPares("aoc13.test.txt")));
    }

    @Test
    public void test2() {
        YList<Object> all = readPares("aoc13.test.txt")
                .flatMap(p -> p)
                .with(al(al(2)))
                .with(al(al(6)))
                .sorted(Aoc13::comparePackets);
        assertEquals(140, (all.indexOf(al(al(2))) + 1) * (all.indexOf(al(al(6))) + 1));
    }

    private static int countOrdered(YList<YList<Object>> pares) {
        return pares
                .map(p -> comparePackets(p.first(), p.last()))
                .mapWithIndex((i, r) -> r < 1 ? i + 1 : 0)
                .reduce(INT_ADD);
    }

    private static YList<YList<Object>> readPares(final String fileName) {
        return al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split("\n")).split(l -> l.trim().isEmpty())
                .map(p -> p.map(e -> Yads.deserialize(e
                        .replace("[", "(")
                        .replace("]", ")")
                        .replace(",", " "))));
    }

    public static int comparePackets(Object a, Object b) {
        if (a instanceof Integer && b instanceof Integer) return Integer.compare((Integer) a, (Integer) b);
        if (a instanceof YList && !(b instanceof YList)) return comparePackets(a, al(b));
        if (b instanceof YList && !(a instanceof YList)) return comparePackets(al(a), b);
        YList la = (YList) a;
        YList lb = (YList) b;

        for (int i = 0; ; i++) {
            if (la.size() <= i && lb.size() <= i) return 0;
            if (la.size() <= i) return -1;
            if (lb.size() <= i) return 1;
            int res = comparePackets(la.get(i), lb.get(i));
            if (res != 0) return res;
        }
    }
}
