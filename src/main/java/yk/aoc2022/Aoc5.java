package yk.aoc2022;

import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;

import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static yk.aoc2022.utils.AocUtils.pop;
import static yk.aoc2022.utils.AocUtils.popSome;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.toYList;

/**
 * Created by yuri at 2022.12.09
 */
public class Aoc5 {

    public static void main(String[] args) {
        YList<YArrayList<String>> raw = al(IO.readFile("src/main/java/yk/aoc2022/aoc5.txt").split("\n")).split(s -> s.trim().equals("")).assertSize(2);
        YArrayList<YList<Character>> rawState = raw.first().map(s -> toYList(s.chars().boxed().map(i -> (char)(int)i).collect(Collectors.toList())));

        YList<YList<Character>> state = al();
        rawState.last().mapWithIndex((i, c) -> {
            if (c <= '9' && c >= '0') {
                YList<Character> line = al();
                for (int j = rawState.size() - 2; j >= 0; j--) line.add(rawState.get(j).getOr(i, null));
                state.add(line.map(cc -> cc != null && cc == ' ' ? null : cc).filter(cc -> cc != null));
            }
            return c;
        });

        YList<YList<Character>> state2 = state.map(lines -> lines.map(c -> c));

        System.out.println(raw.last());
        YList<YList<Integer>> steps = raw.last().map(s -> {
            String[] lines = s.split(" ");
            return al(parseInt(lines[1]), parseInt(lines[3]) - 1, parseInt(lines[5]) - 1);
        });

        System.out.println(state);
        System.out.println(steps);



        for (YList<Integer> step : steps) {
            System.out.println(step);
            for (int count = 0; count < step.get(0); count++) state.get(step.get(2)).add(pop(state.get(step.get(1))));
        }

        System.out.println("Terminal state: \n" + state.toString("\n"));

        System.out.println("Result: " + state.map(line -> line.last()).toString(""));

        for (YList<Integer> step : steps) {
            state2.get(step.get(2)).addAll(popSome(state2.get(step.get(1)), step.get(0)));
        }

        System.out.println("Terminal state2: \n" + state2.toString("\n"));
        System.out.println("Result 2: " + state2.map(line -> line.last()).toString(""));


    }


}
