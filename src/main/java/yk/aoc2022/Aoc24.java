package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.collections.YSet;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;

import java.util.Objects;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.*;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.allocate;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.fastgeom.Vec2i.v2i;

/**
 * Created by yuri at 2022.12.28
 */
public class Aoc24 {

    @Test
    public void test1() {
        initState("aoc24.test1.map");
        for (int i = 0; i < 10; i++) soutMap(mapStateAtMinute(i));
    }

    @Test
    public void test2() {
        initState("aoc24.test2.map");
        assertEquals(18, findSolution(0, v2i(1, 0), v2i(getWidth() - 2, getHeight() - 1)));
    }

    @Test
    public void answer1() {
        initState("aoc24.map");
        assertEquals(262, findSolution(0, v2i(1, 0), v2i(getWidth() - 2, getHeight() - 1)));
    }

    @Test
    public void test3() {
        initState("aoc24.test2.map");
        Vec2i start = v2i(1, 0);
        Vec2i end = v2i(getWidth() - 2, getHeight() - 1);
        assertEquals(54, findSolution(findSolution(findSolution(0, start, end), end, start), start, end));
    }

    @Test
    public void answer2() {
        initState("aoc24.map");
        Vec2i start = v2i(1, 0);
        Vec2i end = v2i(getWidth() - 2, getHeight() - 1);
        assertEquals(785, findSolution(findSolution(findSolution(0, start, end), end, start), start, end));
    }

    private void initState(String fileName) {
        YArrayList<YList<Character>> map = al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split("\n")).map(l -> stringToCharacters(l));
        YList<Blizzard> blizzards = al();

        map.forWithIndex((y, l) -> l.forWithIndex((x, c) -> {
            if (c == '#') return;
            if (c == '.') return;
            blizzards.add(new Blizzard(v2i(x, y), DIR_SYMBOLS.indexOf(c)));
        }));

        stepsBuffer = hm(0, new MapState(map, blizzards));
    }

    private int getHeight() {
        return mapStateAtMinute(0).map.size();
    }

    private int getWidth() {
        return mapStateAtMinute(0).map.first().size();
    }

    public int findSolution(int fromMinute, Vec2i start, Vec2i target) {
        int w = getWidth();
        int h = getHeight();

        YList<ExpeditionState> edge = al(new ExpeditionState(fromMinute, start));
        YSet<ExpeditionState> seen = edge.toSet();
        ExpeditionState best = null;
        int foundSolutions = 0;
        while(true) {
            if (edge.isEmpty()) break;
            ExpeditionState cur = edge.remove(edge.size() - 1);
            if (cur.pos.equals(target)) {
                if (best == null || best.minute > cur.minute) {
                    best = cur;
                    System.out.println("Found best: " + best.minute);
                    foundSolutions++;
                    edge = edge.filter(e -> e.minute < cur.minute);
                    if (foundSolutions > 1000) break;
                }
            }
            MapState nextMapState = mapStateAtMinute(cur.minute + 1);
            ExpeditionState finalBest = best;
            YList<ExpeditionState> newEdge = DIRS.map(d -> cur.pos.add(d))
                    .with(cur.pos)
                    .filter(p -> p.cycle(w, h).equals(p))
                    .filter(p -> nextMapState.map.get(p.y).get(p.x) == '.')
                    .map(p -> new ExpeditionState(cur.minute + 1, p))
                    .filter(p -> finalBest == null || finalBest.minute > p.minute)
                    .filter(s -> !seen.contains(s));
            edge.addAll(newEdge);
            seen.addAll(newEdge);
            edge = edge.sorted(s -> -s.minute - target.disSquared(s.pos));
        }
        return best.minute;
    }

    public static class ExpeditionState {
        public int minute;
        public Vec2i pos;

        public ExpeditionState(int minute, Vec2i pos) {
            this.minute = minute;
            this.pos = pos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExpeditionState that = (ExpeditionState) o;
            return minute == that.minute && pos.equals(that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(minute, pos);
        }

        @Override
        public String toString() {
            return "ExpeditionState{" +
                    "minute=" + minute +
                    ", pos=" + pos +
                    '}';
        }
    }


    public YMap<Integer, MapState> stepsBuffer;

    public MapState mapStateAtMinute(int i) {
        MapState result = stepsBuffer.get(i);
        if (result == null) {
            MapState prev = mapStateAtMinute(i - 1);
            result = new MapState(prev.map, prev.blizzards.map(b -> b.doStep(stepsBuffer.get(0).map)));
            stepsBuffer.put(i, result);
        }
        return result;
    }

    public static class Blizzard {
        public Vec2i pos;

        public int dir;
        public Blizzard(Vec2i pos, int dir) {
            this.pos = pos;
            this.dir = dir;
        }
        public Blizzard doStep(YList<YList<Character>> map) {
            return new Blizzard(pos.add(DIRS.get(dir)).sub(1).cycle(map.first().size() - 2, map.size() - 2).add(1), dir);
        }
    }
    public static class MapState {
        public YList<YList<Character>> map;

        public YList<Blizzard> blizzards;
        public MapState(YList<YList<Character>> prev, YList<Blizzard> blizzards) {
            this.blizzards = blizzards;
            this.map = fillMap(prev, blizzards);
        }
        private static YList<YList<Character>> fillMap(YList<YList<Character>> prev, YList<Blizzard> blizzards) {
            int w = prev.first().size();
            int h = prev.size();
            YList<YList<Character>> map = allocate(h, y -> allocate(w, x -> '#'));
            for (int y = 1; y < h - 1; y++) for (int x = 1; x < w - 1; x++) map.get(y).set(x, '.');
            map.get(0).set(1, '.');
            map.last().set(w - 2, '.');
            for (Blizzard blizzard : blizzards) {
                YList<Character> line = map.get(blizzard.pos.y);
                char element = line.get(blizzard.pos.x);
                char result = element == '.'
                        ? DIR_SYMBOLS.get(blizzard.dir)
                        : DIR_SYMBOLS.contains(element)
                        ? '2'
                        : ((parseInt(element + "") + 1) + "").charAt(0);
                line.set(blizzard.pos.x, result);
            }
            return map;
        }
    }

    private static void soutMap(MapState state) {
        System.out.println(state.map.map(l -> l.toString("")).toString("\n"));
    }

}
