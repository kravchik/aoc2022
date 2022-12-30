package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YSet;
import yk.jcommon.utils.IO;

import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.INT_SUM;
import static yk.aoc2022.utils.AocUtils.findAll;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashSet.hs;

/**
 * Created by yuri at 2022.12.23
 */
public class Aoc19 {

    public static final YList<YList<Integer>> productionByRobot = al(
            al(1, 0, 0, 0),
            al(0, 1, 0, 0),
            al(0, 0, 1, 0),
            al(0, 0, 0, 1)
    );

    @Test
    public void test1() {
        YList<YList<YList<Integer>>> blueprints = readCosts("aoc19.test.txt", "\n\n");
        int sum = evalBlueprints(blueprints, 24).mapWithIndex((i, v) -> (i + 1) * v).reduce(INT_SUM);
        assertEquals(9 * 1 + 12 * 2, sum);
    }

    @Test
    public void test2() {
        YList<YList<YList<Integer>>> blueprints = readCosts("aoc19.test.txt", "\n\n");
        assertEquals(al(56, 62), evalBlueprints(blueprints, 32));
    }

    @Test
    public void answer1() {
        YList<YList<YList<Integer>>> blueprints = readCosts("aoc19.txt", "\n");
        System.out.println(blueprints);
        int sum = evalBlueprints(blueprints, 24).mapWithIndex((i, v) -> (i + 1) * v).reduce(INT_SUM);
        //[0, 1, 0, 13, 1, 4, 0, 5, 3, 0, 0, 2, 5, 1, 0, 1, 9, 6, 1, 6, 2, 3, 2, 9, 3, 1, 0, 7, 1, 1]
        assertEquals(1395, sum);
    }

    //not 0!
    @Test
    public void answer2() {
        YList<YList<YList<Integer>>> blueprints = readCosts("aoc19.txt", "\n").take(3);
        System.out.println(blueprints);
        int sum = evalBlueprints(blueprints, 32).reduce(1, (a, b) -> a * b);
        assertEquals(2700, sum);
    }

    private YList<Integer> evalBlueprints(YList<YList<YList<Integer>>> blueprints, int maxMinutes) {
        int i = 0;
        YList<Integer> qualities = al();
        for (YList<YList<Integer>> blueprint : blueprints) {
            System.out.println("I: " + ++i);
            int x = bestProd(blueprint, maxMinutes);
            qualities.add(x);
            System.out.println(x);
        }
        System.out.println(qualities);
        return qualities;
    }

    private int bestProd(YList<YList<Integer>> bp, int maxMinutes) {
        System.out.println("Solving for " + bp);
        YList<State19> edge = al(new State19(al(1, 0, 0, 0)));
        YSet<State19> seen = hs(edge.first());
        State19 best = null;
        YList<Integer> integers = al(0, 1, 2, 3);
        while (edge.notEmpty()) {
            State19 cur = edge.remove(edge.size() - 1);
            if (best == null || cur.stock.get(3) + cur.robots.get(3) > best.stock.get(3) + best.robots.get(3)) {
                best = cur;
                System.out.println("Cur stock: " + cur.minute + " " + cur.stock + " " + cur.robots + " (" + best + ")");
                System.out.println("seen.size() = " + seen.size());
                System.out.println("edge.size() = " + edge.size());
            }
            State19 finalBest = best;
            YList<State19> newStates = bp.zipWith(integers, (cost, i) -> cur.tick(i, bp, maxMinutes))
                    .filter(s -> s != null)
                    .filter(s -> s.minute < maxMinutes) //don't need to build on the last minute
                    .with(cur.tick(-1, bp, maxMinutes))
                    .filter(s -> s.minute <= maxMinutes)
                    .filter(s ->
                            s.robots.get(0) < 8
                            && s.robots.get(1) < 15
                            && s.robots.get(2) < 15)
                    .filter(s -> s.optimism > finalBest.stock.get(3))
                    .filter(s -> !seen.contains(s))
                    ;
            edge.addAll(newStates);
            seen.addAll(newStates);

            edge = edge.sorted(e ->
                    -e.minute * 2
                    + e.robots.reduce(INT_SUM) * 40
                    + e.robots.get(3) * 100
            );
        }
        return best.stock.get(3);
    }

    private YList<YList<YList<Integer>>> readCosts(final String fileName, String splitter) {
        YList<YList<Integer>> bb = al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split(splitter))
                .map(l -> findAll(l, "([0-9]+)").cdr().map(s -> parseInt(s)));
        return bb.map(l -> al(
                al(l.get(0), 0, 0, 0),
                al(l.get(1), 0, 0, 0),
                al(l.get(2), l.get(3), 0, 0),
                al(l.get(4), 0, l.get(5), 0)));
    }

    public static class State19 {
        public YList<Integer> stock = al(0, 0, 0, 0);
        public YList<Integer> robots;
        public int minute;
        public int optimism;

        public State19() {
        }

        public State19(YList<Integer> robots) {
            this.robots = robots;
        }

        public State19 tick(int robotIndex, YList<YList<Integer>> costs, int maxMinutes) {
            State19 result = new State19();
            result.stock = robotIndex == -1 ? stock : stock.zipWith(costs.get(robotIndex), (a, b) -> a - b);
            if (result.stock.isAny(v -> v < 0)) return null;
            result.stock = result.stock.zipWith(robots, INT_SUM);
            result.minute = minute + 1;
            result.robots = robotIndex == -1 ? robots : robots.zipWith(productionByRobot.get(robotIndex), INT_SUM);
            result.optimism = result.stock.get(3) + result.robots.get(3) * (maxMinutes - result.minute) + max(0, (maxMinutes-1 - result.minute)*((maxMinutes-1 - result.minute + 1))/2);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State19 state19 = (State19) o;
            return minute == state19.minute && stock.equals(state19.stock) && robots.equals(state19.robots);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stock, robots, minute);
        }
    }

}
