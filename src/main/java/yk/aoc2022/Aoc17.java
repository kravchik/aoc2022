package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;

import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.stringToCharacters;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Vec2i.v2i;

/**
 * Created by yuri at 2022.12.22
 */
public class Aoc17 {
    private final YList<YList<Vec2i>> boulders = al(
            al("####"),
            al(".#.", "###", ".#."),
            al("..#", "..#", "###"),
            al("#", "#", "#", "#"),
            al("##", "##"))
            .map(b -> b.reverse().map(l -> stringToCharacters(l)))
            .map(b -> b.mapWithIndex((y, line) -> line
                    .mapWithIndex((x, s) -> s == '#' ? v2i(x, y) : null)
                    .filter(v -> v != null)).flatMap(l -> l));

    @Test
    public void test1() {
        YList<Integer> period = al();
        YList<Integer> sync = al();
        tuneIn("aoc17.test.txt", sync, period);
        assertEquals(3068, getChamberHeight(2022, sync, period));
    }

    @Test
    public void test2() {
        YArrayList<Integer> sync = al();
        YArrayList<Integer> period = al();
        tuneIn("aoc17.test.txt", sync, period);
        assertEquals(1514285714288L, getChamberHeight(1000000000000L - 1, sync, period));
    }

    @Test
    public void answer1() {
        YList<Integer> period = al();
        YList<Integer> sync = al();
        tuneIn("aoc17.txt", sync, period);
        assertEquals(3127, getChamberHeight(2022, sync, period));
    }

    @Test
    public void answer2() {
        YList<Integer> period = al();
        YList<Integer> sync = al();
        tuneIn("aoc17.txt", sync, period);
        assertEquals(1542941176480L, getChamberHeight(1000000000000L/*-1*/, sync, period));
    }

    private long getChamberHeight(long forCount, YList<Integer> sync, YList<Integer> period) {
        forCount -= 1;
        long periodsCount = (forCount - sync.size()) / period.size();
        int inPeriod = (int) ((forCount - sync.size()) % period.size());
        if (inPeriod < 0) return sync.get((int) forCount);
        return period.get(inPeriod) + periodsCount * period.last() + sync.last();
    }

    private void tuneIn(final String fileName, YList<Integer> sync, YList<Integer> period) {
        YList<Integer> jets = readJets(fileName);

        YList<char[]> chamber = al();
        YList<Integer> sizes = al();
        int currentPeriodSize = 5;
        int currentBoulder = -1;
        int currentJet = -1;
        int syncSize = 100;
        while (true) {
            currentBoulder = (currentBoulder + 1) % boulders.size();
            currentJet = dropBoulder(jets, chamber, sizes, currentBoulder, currentJet);

            int testCount = currentPeriodSize;
            if (syncSize + currentPeriodSize + testCount < sizes.size()) {
                boolean match = true;
                for (int i = 0; i < testCount; i++) {
                    int dif2 = sizes.get(syncSize + i) - sizes.get(syncSize - 1);
                    int dif3 = sizes.get(syncSize + currentPeriodSize + i) - sizes.get(syncSize + currentPeriodSize - 1);
                    if (dif2 != dif3) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    sync.addAll(sizes.subList(0, syncSize));
                    period.addAll(sizes.subList(syncSize, syncSize + currentPeriodSize)
                            .map(v -> v - sizes.get(syncSize - 1)));
                    System.out.println("Period size: " + period.size());
                    return;
                }

                currentPeriodSize += 1;
            }
        }
    }

    private int dropBoulder(YList<Integer> jets, YList<char[]> chamber, YList<Integer> sizes, int currentBoulder, int currentJet) {
        YList<Vec2i> boulder = boulders.get(currentBoulder);
        Vec2i bPos = v2i(2, chamber.size() + 3);
        while (true) {
            currentJet = (currentJet + 1) % jets.size();
            Vec2i jetPos = bPos.add(jets.get(currentJet), 0);
            if (!isBlocked(chamber, boulder, jetPos)) bPos = jetPos;

            Vec2i fallPos = bPos.add(0, -1);
            if (isBlocked(chamber, boulder, fallPos)) break;
            bPos = fallPos;

        }
        while (chamber.size() <= bPos.y + boulder.max(b -> b.y).y) chamber
                .add(new char[]{'.', '.', '.', '.', '.', '.', '.'});
        sizes.add(chamber.size());

        Vec2i finalBPos = bPos;
        boulder.map(v -> v.add(finalBPos)).forEach(v -> chamber.get(v.y)[v.x] = '#');
        return currentJet;
    }

    private YList<Integer> readJets(String fileName) {
        return stringToCharacters(IO.readFile("src/main/java/yk/aoc2022/" + fileName))
                .map(c -> c == '<' ? -1 : 1);
    }

    private boolean isBlocked(YList<char[]> chamber, YList<Vec2i> boulder, Vec2i newPos) {
        return boulder
                .map(v -> v.add(newPos))
                .isAny(v
                        -> v.x < 0
                        || v.x >= 7
                        || v.y < 0
                        || v.y < chamber.size() && chamber.get(v.y)[v.x] == '#'
                );
    }
}
