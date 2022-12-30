package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.IO;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.19
 */
public class Aoc11 {

    //20567144694 is an answer
    public static void main(String[] args) {
        System.out.println("Result1: "
                + getTop2Mul(getRounds(parseMonkeys("src/main/java/yk/aoc2022/aoc11.txt"), 3, 20)));
        System.out.println("Result2: "
                + getTop2Mul(getRounds(parseMonkeys("src/main/java/yk/aoc2022/aoc11.txt"), 1, 10000)));
    }

    @Test
    public void test1() {
        YArrayList<Monkey> round1 = parseMonkeys("src/main/java/yk/aoc2022/aoc11.test.txt");
        System.out.println(round1.toString("\n"));
        YList<YList<Monkey>> rounds = getRounds(round1, 3, 20);
        assertEquals(al(al(10L, 12L, 14L, 26L, 34L), al(245L, 93L, 53L, 199L, 115L), al(), al()), rounds.last().map(m -> m.items));
        assertEquals(10605, getTop2Mul(rounds));
    }

    @Test
    public void test2() {
        YArrayList<Monkey> round1 = parseMonkeys("src/main/java/yk/aoc2022/aoc11.test.txt");
        System.out.println(round1.toString("\n"));
        YList<YList<Monkey>> rounds = getRounds(round1, 1, 10000);

        {
            YList<YList<Long>> totals = YArrayList.allocate(rounds.first().size(), m -> al());
            rounds.subList(0, 20).forEach(r -> r.mapWithIndex((i, m) -> totals.get(i).addAll(m.items)));
            assertEquals(al(99, 97, 8, 103), totals.map(t -> t.size()));
        }

        {
            YList<YList<Long>> totals = YArrayList.allocate(rounds.first().size(), m -> al());
            rounds.subList(0, 1000).forEach(r -> r.mapWithIndex((i, m) -> totals.get(i).addAll(m.items)));
            assertEquals(al(5204, 4792, 199, 5192), totals.map(t -> t.size()));
        }

        {
            YList<YList<Long>> totals = YArrayList.allocate(rounds.first().size(), m -> al());
            rounds.subList(0, 2000).forEach(r -> r.mapWithIndex((i, m) -> totals.get(i).addAll(m.items)));
            assertEquals(al(10419, 9577, 392, 10391), totals.map(t -> t.size()));
        }

        {
            YList<YList<Long>> totals = YArrayList.allocate(rounds.first().size(), m -> al());
            rounds.subList(0, 10000).forEach(r -> r.mapWithIndex((i, m) -> totals.get(i).addAll(m.items)));
            assertEquals(al(52166, 47830, 1938, 52013), totals.map(t -> t.size()));
        }

        assertEquals(2713310158L, getTop2Mul(rounds));
        assertEquals(2713310158L, getTop2Mul(getRounds(parseMonkeys("src/main/java/yk/aoc2022/aoc11.test.txt"), 1, 10000)));
    }

    private static long getTop2Mul(YList<YList<Monkey>> rounds) {
        YList<YList<Long>> totals = YArrayList.allocate(rounds.first().size(), m -> al());
        rounds.subList(0, rounds.size() - 1).forEach(r -> r.mapWithIndex((i, m) -> totals.get(i).addAll(m.items)));
        YList<Integer> top2 = totals.map(t -> t.size()).sorted().reverse().take(2);
        System.out.println(top2);
        return (long)top2.first() * top2.last();
    }

    private static YList<YList<Monkey>> getRounds(YArrayList<Monkey> round1, int worryDivisor, int roundsCount) {
        YList<YList<Monkey>> rounds = al(round1);
        for (int i = 0; i < roundsCount; i++) {
            rounds.add(monkeyRound(rounds.last(), worryDivisor));
            //System.out.println("After round " + (i + 1));
            //System.out.println(rounds.last().mapWithIndex((j, m) -> String.format("Monkey %s: %s", j, m.items.toString(", "))).toString("\n"));
        }
        return rounds;
    }


    private static YList<Monkey> monkeyRound(YList<Monkey> monkeys, int worryDivisor) {
        YList<Monkey> result = monkeys;
        for (int i = 0; i < result.size(); i++) result = monkeyTurn(result, i, worryDivisor);
        return result;
    }

    private static YList<Monkey> monkeyTurn(YList<Monkey> allMonkeys, int i, int worryDivisor) {
        allMonkeys = allMonkeys.map(Monkey::copy);
        Monkey monkey = allMonkeys.get(i);
        for (Long item : monkey.items) {
            long value = monkey.value.equals("old") ? item : Integer.parseInt(monkey.value);
            long newValue;
            if (monkey.operation.equals("*")) newValue = item * value;
            else if (monkey.operation.equals("+")) newValue = item + value;
            else throw BadException.shouldNeverReachHere();
            newValue = newValue / worryDivisor;
            //newValue = newValue % (13 * 17 * 19 * 23);
            newValue = newValue % (2 * 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23);
            //newValue = Math.round(newValue / 3f);
            int otherMonkey = newValue % monkey.divisor == 0 ? monkey.ifTrue : monkey.ifFalse;
            allMonkeys.get(otherMonkey).items.add(newValue);
        }
        monkey.items = al();
        return allMonkeys;
    }

    private static YArrayList<Monkey> parseMonkeys(String fileName) {
        YArrayList<String> lines = al(IO.readFile(fileName).split("\n"));
        YArrayList<YArrayList<String>> monkeyLines = lines.split(l -> l.trim().isEmpty());

        return monkeyLines.map(line -> {
            YList<String> o = al(line.get(2).split("Operation: new = old ")[1].split(" "));
            return new Monkey(
                    al(line.get(1).split("Starting items: ")[1].split(", ")).map(Long::parseLong),
                    o.get(0),
                    o.get(1),
                    Integer.parseInt(line.get(3).split("Test: divisible by ")[1]),
                    Integer.parseInt(al(line.get(4).split("If true: throw to monkey ")[1]).last()),
                    Integer.parseInt(al(line.get(5).split("If false: throw to monkey ")[1]).last())
            );
        });
    }

    public static class Monkey {
        YList<Long> items;
        String operation;
        String value;
        int divisor;
        int ifTrue;
        int ifFalse;

        public Monkey copy() {
            return new Monkey(items, operation, value, divisor, ifTrue, ifFalse);
        }

        public Monkey(YList<Long> items, String operation, String value, int divisor, int ifTrue, int ifFalse) {
            this.items = items;
            this.operation = operation;
            this.value = value;
            this.divisor = divisor;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        @Override
        public String toString() {
            return "Monkey{" +
                    "items=" + items +
                    ", operation='" + operation + '\'' +
                    ", value=" + value +
                    ", divisor=" + divisor +
                    ", ifTrue=" + ifTrue +
                    ", ifFalse=" + ifFalse +
                    '}';
        }
    }
}
