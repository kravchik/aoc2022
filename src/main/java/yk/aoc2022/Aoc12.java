package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;
import yk.jcommon.utils.StopWatch;

import java.util.Comparator;
import java.util.function.BiPredicate;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.allocate;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.fastgeom.Vec2i.v2i;

/**
 * Created by yuri at 2022.12.19
 */
public class Aoc12 {
    public static final YMap<Vec2i, String> DIRS = hm(
            v2i(1, 0), ">",
            v2i(-1, 0), "<",
            v2i(0, 1), "V",
            v2i(0, -1), "^"
    );

    public static void main(String[] args) {

        //BUG in the task! Replacing E with 'z', instead of 'z'+1, (like it should be basing on the example)

        //may not work for another input, because looks for 'first z' actually (because of the ^^^ bug)
        //  instead of 'z'+1, or E
        //  Same goes for part 2.
        YList<String> surface = readSurface("src/main/java/yk/aoc2022/aoc12.txt", 'z');

        System.out.println("Heating up");
        for (int i = 0; i < 500; i++) {
            YList<State12> v = getPath(surface, (char) ('a' - 1), 'z', (cur, prev) -> cur.height - prev.height < 2);
            System.out.println(v.size());
        }
        System.out.println("Done");
        System.out.println("");

        YList<State12> path = getPath(surface, (char) ('a' - 1), 'z', (cur, prev) -> cur.height - prev.height < 2);
        assertEquals(425, path.size() - 1);

        System.out.println();
        YList<State12> path2 = getPath(surface, 'z', 'a', (a, b) -> b.height - a.height < 2);
        //418
        assertEquals(418, path2.size() - 1);
    }

    @Test
    public void test1() {
        YList<String> surface = readSurface("src/main/java/yk/aoc2022/aoc12.test.txt", (char) ('z' + 1));
        assertEquals(31, getPath(surface, (char) ('a' - 1), (char) ('z' + 1),
                (a, b) -> a.height - b.height < 2).size() - 1);
    }

    @Test
    public void test2() {
        YList<String> surface = readSurface("src/main/java/yk/aoc2022/aoc12.test.txt", (char) ('z' + 1));
        assertEquals(29, getPath(surface, (char) ('z' + 1), 'a', (a, b) -> b.height - a.height < 2).size() - 1);
    }

    private static YList<State12> getPath(YList<String> surface, char start, char target,
                                          BiPredicate<State12, State12> test) {

        //find initial
        Vec2i initial = null;
        for (int y = 0; y < surface.size(); y++) {
            String s = surface.get(y);
            int ind = s.indexOf(start);
            if (ind != -1) {
                initial = v2i(ind, y);
                break;
            }
        }

        StopWatch sw = new StopWatch();
        YList<State12> edge = al(new State12(null, initial, surface.get(initial.y).charAt(initial.x)));
        YMap<Vec2i, State12> search = hm();
        search.put(initial, edge.first());

        //spill water
        State12 end = null;
        for (int i = 0; i < 1000000; i++) {
            if (edge.isEmpty()) break;
            State12 cur = edge.remove(edge.size() - 1);
            if (end != null && cur.depth > end.depth) continue;
            if (cur.height == target) {
                if (end == null) end = cur;
                continue;
            }
            edge = edge.withAll(DIRS.mapToList((k, v) -> cur.pos.add(k))
                    .filter(pos -> pos.x >= 0 && pos.y >= 0
                            && pos.x < surface.first().length() && pos.y < surface.size())
                    .map(pos -> new State12(cur, pos, surface.get(pos.y).charAt(pos.x)))
                    .filter(s -> !search.containsKey(s.pos) || search.get(s.pos).depth > s.depth)
                    .filter(s -> test.test(s, cur))
                    .forThis(ss -> ss.forEach(s -> search.put(s.pos, s)))
            ).sorted(Comparator.comparing(o -> -o.depth));
        }

        //backtrace
        YList<YList<String>> trace = allocate(surface.size(), i -> allocate(surface.first().length(), j -> "."));
        //can we do better? Collect? Generate?
        YList<State12> path = al(end);
        while (path.last().prevPos != null) {
            State12 last = path.last();
            State12 prev = search.get(last.prevPos);
            trace.get(prev.pos.y).set(prev.pos.x, (char) prev.height + "");
            path.add(prev);
        }
        trace.get(path.first().pos.y).set(path.first().pos.x, "E");
        //System.out.println(trace.map(l -> l.toString(" ")).toString("\n"));
        System.out.println(sw.getCurrentTime());
        return path;
    }

    private static YList<String> readSurface(String fileName, char newTarget) {
        return al(IO.readFile(fileName).split("\n")).map(l -> l
                .replace("E", newTarget + "")
                .replace("S", (char) ('a' - 1) + "")
        );
    }

    public static class State12 {
        public Vec2i pos;
        public Vec2i prevPos;
        public char height;
        public int depth;

        public State12(State12 prev, Vec2i pos, char height) {
            this.height = height;
            this.pos = pos;
            if (prev != null) {
                this.prevPos = prev.pos;
                this.depth = prev.depth + 1;
            }
        }
    }
}
