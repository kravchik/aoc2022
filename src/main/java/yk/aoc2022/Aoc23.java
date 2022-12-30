package yk.aoc2022;

import org.junit.Test;
import yk.aoc2022.utils.AocUtils;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;
import yk.jcommon.utils.Ptr;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.allocate;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.fastgeom.Vec2i.v2i;

/**
 * Created by yuri at 2022.12.27
 */
public class Aoc23 {

    @Test
    public void test1() {
        assertEquals(110, countEmpty(doRoudns("aoc23.test.map", new Ptr<>(), 10)));
    }

    @Test
    public void test2() {
        Ptr<Integer> rounds = new Ptr<>();
        doRoudns("aoc23.test.map", rounds, 100500);
        assertEquals(20, (int)rounds.value);
    }

    @Test
    public void answer1() {
        Ptr<Integer> rounds = new Ptr<>();
        assertEquals(3970, countEmpty(doRoudns("aoc23.map", rounds, 10)));
    }

    @Test
    public void answer2() {
        Ptr<Integer> rounds = new Ptr<>();
        doRoudns("aoc23.map", rounds, 100500);
        assertEquals(923, (int)rounds.value);
    }


    private YMap<Vec2i, Elf> doRoudns(final String fileName, Ptr<Integer> rounds, int maxRounds) {
        rounds.value = 0;
        YMap<Vec2i, Elf> elves = hm();
        YMap<Vec2i, Elf> finalElves = elves;
        al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split("\n")).map(AocUtils::stringToCharacters).forWithIndex((y, l) -> l.forWithIndex((x, c) -> {
            if (c == '#') finalElves.put(v2i(x, y), new Elf(v2i(x, y)));
        }));

        YArrayList<Vec2i> NW = al(v2i(-1, -1), v2i(0, -1), v2i(1, -1));
        YList<YList<Vec2i>> WORLD_SIDES = al(
                NW,
                NW.map(v -> ccw90(ccw90(v))),
                NW.map(v -> ccw90(ccw90(ccw90(v)))),
                NW.map(v -> ccw90(v))
        );
        YList<Vec2i> ADJ = WORLD_SIDES.flatMap(d -> d).toSet().toList();
        int nextSide = 0;
        //soutDebug(elves);
        for (int round = 0; round < maxRounds; round++) {
            rounds.value++;
            YMap<Vec2i, Elf> newElves = doRound(elves, WORLD_SIDES, ADJ, nextSide);
            if (newElves == null) break;
            elves = newElves;
            nextSide = (nextSide + 1) % WORLD_SIDES.size();

            System.out.println("After round " + (round + 1));
            //soutDebug(elves);
        }
        return elves;
    }

    public static void soutDebug(YMap<Vec2i, Elf> elves) {
        YList<Integer> ranges = getRanges(elves);
        YList<YList<String>> mapa = allocate(ranges.get(3) - ranges.get(2)+1, y -> allocate(ranges.get(1) - ranges.get(0)+1, x -> "."));
        for (Elf elf : elves.values()) mapa.get(elf.pos.y - ranges.get(2)).set(elf.pos.x - ranges.get(0), "#");
        System.out.println(mapa.map(l -> l.toString("")).toString("\n"));
    }

    private static int countEmpty(YMap<Vec2i, Elf> elves) {
        YList<Integer> ranges = getRanges(elves);
        int total = (ranges.get(3) - ranges.get(2) + 1) * (ranges.get(1) - ranges.get(0) + 1);
        return total - elves.size();
    }

    private static YList<Integer> getRanges(YMap<Vec2i, Elf> elves) {
        return al(elves.values().map(e -> e.pos.x).min(),
                elves.values().map(e1 -> e1.pos.x).max(),
                elves.values().map(e2 -> e2.pos.y).min(),
                elves.values().map(e3 -> e3.pos.y).max());
    }


    private YMap<Vec2i, Elf> doRound(YMap<Vec2i, Elf> elves, YList<YList<Vec2i>> WORLD_SIDES, YList<Vec2i> ADJ, int nextSide) {
        //first half
        YMap<Vec2i, Integer> decisions = hm();
        for (Elf elf : elves.values()) {
            elf.decision = null;
            YMap<Vec2i, Elf> finalElves = elves;
            if (ADJ.isAny(adj -> finalElves.get(elf.pos.add(adj)) != null)) {
                for (int i = 0; i < WORLD_SIDES.size(); i++) {
                    YList<Vec2i> side = WORLD_SIDES.get(((nextSide + i) % WORLD_SIDES.size()));
                    if (!side.isAny(dir -> finalElves.get(elf.pos.add(dir)) != null)) {
                        elf.decision = elf.pos.add(side.get(1));
                        decisions.put(elf.decision, decisions.getOr(elf.decision, 0) + 1);
                        break;
                    }
                }
            }
        }

        //second half
        boolean wasAny = false;
        YMap<Vec2i, Elf> newElves = hm();
        for (Elf elf : elves.values()) {
            if (elf.decision != null && decisions.get(elf.decision) != 1) elf.decision = null;
            if (elf.decision != null) {
                elf.pos = elf.decision;
                wasAny = true;
            }
            elf.decision = null;
            newElves.put(elf.pos, elf);
        }
        if (!wasAny) return null;
        elves = newElves;
        return elves;
    }

    public static class Elf {

        public Vec2i pos;
        public Vec2i decision;
        public Elf(Vec2i pos) {
            this.pos = pos;
        }

    }
    public static Vec2i ccw90(Vec2i v) {
        //noinspection SuspiciousNameCombination
        return v2i(-v.y, v.x);
    }
}
