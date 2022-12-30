package yk.aoc2022;

import yk.jcommon.collections.YList;

import static java.lang.Integer.parseInt;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.utils.IO.readFile;

/**
 * Created by yuri at 2022.12.01
 */
public class Aoc1 {
    public static void main(String[] args) {
        YList<Integer> orderedSums = al(readFile("src/main/java/yk/aoc2022/aoc1.txt").split("\n"))
                .map(s -> s.trim())
                .split(s -> s.isEmpty())
                .map(s -> s.map(i -> parseInt(i)).reduce((i1, i2) -> i1 + i2))
                .sorted(i -> -i);
        System.out.println("max:  " + orderedSums.first());
        System.out.println("top3: " + orderedSums.take(3).reduce((i1, i2) -> i1 + i2));
    }
}
