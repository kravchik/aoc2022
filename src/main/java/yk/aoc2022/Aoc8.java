package yk.aoc2022;

import org.junit.Test;
import yk.aoc2022.utils.AocUtils;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;

import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.INT_SUM;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.14
 */
public class Aoc8 {

    public static void main(String[] args) {
        YArrayList<YList<Integer>> surface = readSurface("src/main/java/yk/aoc2022/aoc8.txt");
        YList<YList<Integer>> visibility = getVisibility(surface);
        System.out.println("Result: " + calcVisible(visibility));

        YList<YList<Integer>> scenic = getScenicScores(surface);
        YList<Integer> top = scenic.flatMap(l -> l).sorted().reverse().take(5);

        System.out.println("Scenic: " + top.first());

        System.out.println(visibility.zipWith(scenic, (a, b) -> a
                .zipWith(b, (aa, bb) -> top.contains(bb) ? "*" : aa == 0 ? " " : "^")
                .toString("")).toString("\n"));
    }

    @Test
    public void test1() {
        YList<YList<Integer>> surface = readSurface("src/main/java/yk/aoc2022/aoc8.test.txt");

        YList<YList<Integer>> visible = getVisibility(surface);
        System.out.println(surface.toString("\n"));
        System.out.println("Visible: " + visible.map(l -> l.reduce(INT_SUM)).reduce(INT_SUM));
        System.out.println(visible.toString("\n"));

        assertEquals(21, calcVisible(visible));

        YList<YList<Integer>> scenicScores = getScenicScores(surface);
        System.out.println(scenicScores.toString("\n"));
        assertEquals(4, (int)scenicScores.get(1).get(2));
        assertEquals(8, (int)scenicScores.get(3).get(2));
    }

    private static YArrayList<YList<Integer>> readSurface(String fileName) {
        return al(IO.readFile(fileName).split("\n"))
                .map(s -> AocUtils.stringToInts(s).map(i -> i - '0'));
    }

    private static int calcVisible(YList<YList<Integer>> visible) {
        return visible.map(l -> l.reduce(INT_SUM)).reduce(INT_SUM);
    }

    private static YList<YList<Integer>> getVisibility(YList<YList<Integer>> surface) {
        int s = surface.size();
        YArrayList<YList<YList<Integer>>> rotations = al(
                al(al(1, 0, 0),
                   al(0, 1, 0)),
                al(al(0, 1, 0),
                   al(1, 0, 0)),
                al(al(-1, 0, s-1),
                   al(0, 1, 0)),
                al(al(0, 1, 0),
                   al(-1, 0, s-1))
        );
        YList<YList<Integer>> visibility = YArrayList.allocate(s, i -> YArrayList.allocate(s, j -> 0));
        for (YList<YList<Integer>> g : rotations)
            for (int y = 0; y < surface.size(); y++) {
                int lastHeight = -1;
                for (int x = 0; x < surface.size(); x++) {
                    int ratatedX = g.get(0).get(0) * x + g.get(0).get(1) * y + g.get(0).get(2);
                    int rotatedY = g.get(1).get(0) * x + g.get(1).get(1) * y + g.get(1).get(2);
                    int h = surface.get(rotatedY).get(ratatedX);
                    if (h > lastHeight) {
                        lastHeight = h;
                        visibility.get(rotatedY).set(ratatedX, 1);
                    }
                }
            }
        return visibility;
    }

    private static YList<YList<Integer>> getScenicScores(YList<YList<Integer>> surface) {
        int s = surface.size();
        YArrayList<YList<YList<Integer>>> rotations = al(
                al(al(1, 0, 0),
                   al(0, 1, 0)),
                al(al(0, 1, 0),
                   al(1, 0, 0)),
                al(al(-1, 0, s-1),
                   al(0, 1, 0)),
                al(al(0, 1, 0),
                   al(-1, 0, s-1))
        );

        YList<YList<Integer>> scenic = YArrayList.allocate(s, i -> YArrayList.allocate(s, j -> 0));

        for (int y1 = 0; y1 < surface.size(); y1++) {
            for (int x1 = 0; x1 < surface.size(); x1++) {
                int curHeight = surface.get(y1).get(x1);
                int curScenic = 1;
                for (YList<YList<Integer>> g : rotations) {
                    int viewDistance = 0;
                    int x = x1;
                    int y = y1;
                    while (true) {
                        x += g.get(0).get(0);
                        y += g.get(1).get(0);
                        if (x >= s || x < 0 || y >= s || y < 0) break;
                        viewDistance++;
                        if (curHeight <= surface.get(y).get(x)) break;
                    }
                    curScenic *= viewDistance;
                }
                scenic.get(y1).set(x1, curScenic);

            }}

        return scenic;
    }
}
