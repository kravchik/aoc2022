package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.*;
import yk.jcommon.utils.IO;
import yk.jcommon.utils.MyMath;

import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.INT_ADD;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.collections.YHashSet.hs;
import static yk.jcommon.utils.MyMath.abs;

/**
 * Created by yuri at 2022.12.16
 */
public class Aoc9 {

    public static void main(String[] args) {
        YArrayList<Tuple<String, Integer>> commands = readCommands("src/main/java/yk/aoc2022/aoc9.txt");
        System.out.println("Result: " + getTrace(commands, 1).size());
        System.out.println("Result: " + getTrace(commands, 9).size());
    }

    @Test
    public void test1() {
        assertEquals(13, getTrace(readCommands("src/main/java/yk/aoc2022/aoc9.test.txt"), 1).size());
    }

    @Test
    public void test2() {
        assertEquals(36, getTrace(readCommands("src/main/java/yk/aoc2022/aoc9.test2.txt"), 9).size());
    }

    private static YSet<YList<Integer>> getTrace(YArrayList<Tuple<String, Integer>> commands, int tailLen) {
        YMap<String, YList<Integer>> dirs = hm(
                "U", al(0, 1),
                "D", al(0, -1),
                "R", al(1, 0),
                "L", al(-1, 0)
        );

        YList<Integer> head = al(0, 0);
        YList<YList<Integer>> tail = YArrayList.allocate(tailLen, i -> al(0, 0));
        YSet<YList<Integer>> trace = hs(tail.get(tailLen - 1));

        for (Tuple<String, Integer> command : commands) {
            YList<Integer> dir = dirs.get(command.a);
            for (int step = 0; step < command.b; step++) {
                head = head.zipWith(dir, (a, b) -> a + b);

                tail = tail.reduce(al(head), (h, t) -> {
                    Integer dist = t.zipWith(h.last(), (a, b) -> (int) abs(b - a)).reduce(INT_ADD);
                    t = t.zipWith(h.last(), (a, b) -> a + MyMath.clamp(dist > 2 ? (b - a) : (int)((b - a)*0.5f), 1));
                    return h.with(t);
                }).cdr();

                trace.add(tail.last());

                //drawState(head, tail, trace);
            }
        }
        return trace;
    }

    private static void drawState(YList<Integer> hPos, YList<YList<Integer>> tPos, YSet<YList<Integer>> trace) {
        YArrayList<YArrayList<String>> surface = YArrayList.allocate(100, l -> YArrayList.allocate(100, k -> "."));

        for (YList<Integer> t : trace) surface.get(50 - t.get(1)).set(t.get(0) + 50, "*");
        surface.get(50).set(50, "S");
        for (int j = 0; j < tPos.size(); j++) {
            YList<Integer> t = tPos.get(j);
            surface.get(50 - t.get(1)).set(t.get(0) + 50, j + 1 + "");
        }
        surface.get(50- hPos.get(1)).set(hPos.get(0) + 50, "H");
        System.out.println();
        System.out.println(surface.map(l -> l.toString("")).toString("\n"));
    }

    private static YArrayList<Tuple<String, Integer>> readCommands(String s2) {
        return al(IO.readFile(s2).split("\n")).map(s -> new Tuple<>(s.split(" ")[0], Integer.parseInt(s.split(" ")[1])));
    }

}
