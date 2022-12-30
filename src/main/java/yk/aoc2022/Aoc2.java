package yk.aoc2022;

import yk.jcommon.collections.Tuple;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;

import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.02
 */
public class Aoc2 {
    public static void main(String[] args) {
        //0 - Rock
        //1 - Paper
        //2 - Scissors

        //0 - lost
        //1 - draw
        //2 - win

        YList<YList<Integer>> mix = al('A', 'B', 'C').map(c -> c - 'A').eachToEach(al('X', 'Y', 'Z').map(c -> c - 'X'));
        System.out.println(mix);
        //00 -> 0
        //
        //System.out.println(mix.map(l -> l + " : " + MyMath.cycle(l.last() - l.first(), 3)).toString("\n"));
        System.out.println(mix.map(l -> l + " : " + (4 + l.last() - l.first()) % 3).toString("\n"));
        System.out.println();
        System.out.println(mix.map(l -> l + " : " + (2 + l.first() + l.last()) % 3).toString("\n"));


        YArrayList<Tuple<Integer, Integer>> tuples = al(IO.readFile("src/main/java/yk/aoc2022/aoc2.txt")
                .split("\n"))
                .map(s -> new Tuple<>(s.charAt(0) - 'A', s.charAt(2) - 'X'));

        YList<Integer> resultScores = al(0, 3, 6);
        YList<Integer> choiceScores = al(1, 2, 3);

        System.out.println("total:  " + tuples
                .map(t -> resultScores.get((4 + t.b - t.a) % 3) + choiceScores.get(t.b))
                .reduce((i1, i2) -> i1 + i2));

        System.out.println("total2: " + tuples
                .map(t -> resultScores.get(t.b)  + choiceScores.get((2 + t.a + t.b) % 3))
                .reduce((i1, i2) -> i1 + i2));


    }
}
