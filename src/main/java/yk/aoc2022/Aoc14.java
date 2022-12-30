package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.allocate;
import static yk.jcommon.fastgeom.Vec2i.v2i;

/**
 * Created by yuri at 2022.12.20
 */
public class Aoc14 {
    public static final int LEFT = 0;
    public static final int WIDTH = 700;
    public static final int HEIGHT = 170;
    public static final String TEST_INPUT = "498,4 -> 498,6 -> 496,6\n" +
            "503,4 -> 502,4 -> 502,9 -> 494,9";

    public static void main(String[] args) {
        YArrayList<YArrayList<Vec2i>> lines = readLines(IO.readFile("src/main/java/yk/aoc2022/aoc14.txt"));
        {
            YList<YList<String>> map = fillMap(lines);
            int rest = pourSand(map);
            System.out.println("Rest: " + rest);
            System.out.println(map.map(l -> l.toString("")).toString("\n"));
            assertEquals(1406, rest);
        }
        {
            Integer maxY = lines.flatMap(l -> l).map(v -> v.y).max();
            System.out.println(maxY);
            lines = lines.with(al(v2i(LEFT, maxY + 2), v2i(LEFT + WIDTH - 1, maxY + 2)));
            YList<YList<String>> map = fillMap(lines);

            int rest = pourSand(map);
            assertEquals(20870, rest);
            System.out.println("Rest: " + rest);

            System.out.println(map.map(l -> l.toString("")).toString("\n"));
        }
    }
    @Test
    public void test1() {
        YList<YList<String>> map = fillMap(readLines(TEST_INPUT));
        assertEquals(24, pourSand(map));
        System.out.println(map.map(l -> l.toString("")).toString("\n"));
    }

    @Test
    public void test2() {
        YArrayList<YArrayList<Vec2i>> lines = readLines(TEST_INPUT);
        Integer maxY = lines.flatMap(l -> l).map(v -> v.y).max();
        lines = lines.with(al(v2i(LEFT, maxY + 2), v2i(LEFT + WIDTH - 1, maxY + 2)));
        YList<YList<String>> map = fillMap(lines);
        assertEquals(93, pourSand(map));
        System.out.println(map.map(l -> l.toString("")).toString("\n"));
    }

    private static int pourSand(YList<YList<String>> map) {
        int rest = 0;
        while(true) if (simulateSand(map, v2i(500, 0))) rest++;else break;
        return rest;
    }

    private static boolean simulateSand(YList<YList<String>> map, Vec2i sandInitial) {
        if (get(map, sandInitial).equals("O")) return false;
        Vec2i cur = sandInitial;
        while(true) {
            if (cur.y >= HEIGHT-1) return false;
            Vec2i next = cur.add(0, 1);
            if (get(map, next).equals(".")) {cur = next;continue;}
            next = cur.add(-1, 1);
            if (get(map, next).equals(".")) {cur = next;continue;}
            next = cur.add(1, 1);
            if (get(map, next).equals(".")) {cur = next;continue;}
            set(map, cur, "O");
            return true;
        }
    }

    private static String get(YList<YList<String>> map, Vec2i pos) {
        return map.get(pos.y).get(pos.x - LEFT);
    }

    private static void set(YList<YList<String>> map, Vec2i pos, String value) {
        map.get(pos.y).set(pos.x - LEFT, value);
    }

    private static YList<YList<String>> fillMap(YArrayList<YArrayList<Vec2i>> lines) {
        YList<YList<String>> map = allocate(HEIGHT, y -> allocate(WIDTH, x -> "."));
        for (YArrayList<Vec2i> line : lines) {
            line.reduce((from, to) -> {
                Vec2i delta = to.sub(from).min(1).max(-1);
                Vec2i cur = from;
                while(!cur.equals(to)) {
                    map.get(cur.y).set(cur.x - LEFT, "#");
                    cur = cur.add(delta);
                }
                map.get(to.y).set(to.x - LEFT, "#");
                return to;
            });
        }
        return map;
    }

    private static YArrayList<YArrayList<Vec2i>> readLines(String input) {
        return al(input.split("\n"))
                .map(l -> al(l.split(" -> ")).map(el -> v2i(parseInt(el.split(",")[0]), parseInt(el.split(",")[1]))));
    }

}
