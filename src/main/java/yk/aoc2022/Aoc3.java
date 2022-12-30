package yk.aoc2022;

import yk.aoc2022.utils.AocUtils;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;

import java.util.stream.Collectors;

import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.toYList;

/**
 * Created by yuri at 2022.12.05
 */
public class Aoc3 {
    public static void main(String[] args) {
        YList<YList<Integer>> rr = al(IO.readFile("src/main/java/yk/aoc2022/aoc3.txt").split("\n"))
                .map(s -> s.trim())
                .map(s -> toYList(s.chars().boxed().collect(Collectors.toList())));

        System.out.println("sum of misplaced priority: " + rr
                        .map(l -> al(l.subList(0, l.size() / 2), l.subList(l.size() / 2, l.size())))
                        .map(r -> r.map(c -> c.toSet()))
                        .map(r -> r.first().intersection(r.last()))
                        .map(r -> r.assertSize(1).first())
                        .map(r -> r > 'Z' ? r - 'a' + 1 : r - 'A' + 27)
                        .reduce((a, b) -> a + b));

        YList<YList<YList<Integer>>> groups = AocUtils.split(rr, acc -> acc.cur.size() == 3 ? acc.finishGroup().add() : acc.add());

        System.out.println("sum of badges' priority: " +
                groups.map(g -> g.assertSize(3))
                        .map(e -> e.map(r -> r.toSet()))
                        .map(r -> r.get(0).intersection(r.get(1).intersection(r.get(2))))
                        .map(i -> i.assertSize(1).first())
                        .map(r -> r > 'Z' ? r - 'a' + 1 : r - 'A' + 27)
                        .reduce((a, b) -> a + b));
    }

}
