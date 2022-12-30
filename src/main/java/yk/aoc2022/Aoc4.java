package yk.aoc2022;

import yk.jcommon.collections.Tuple;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;

import static java.lang.Integer.parseInt;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.09
 */
public class Aoc4 {
    public static void main(String[] args) {
        YList<YList<YList<Tuple<Integer, Integer>>>> pares = al(IO.readFile("src/main/java/yk/aoc2022/aoc4.txt").split("\n"))
                .map(s -> s.trim())
                .map(s -> al(s.split(","))
                        .assertSize(2)
                        .map(r -> al(r.split("-")).assertSize(2))
                        .map(r -> new Tuple<>(parseInt(r.first()), parseInt(r.last()))))
                .map(p -> p.eachToEach(p)
                        .filter(l -> l.first() != l.last())
                        .assertSize(2));

        System.out.println("Pares with full intersection: " + pares
                        .map(p -> p.isAny(l -> l.first().a <= l.last().a && l.first().b >= l.last().b))
                        .count(t -> t));

        System.out.println("Pares with intersection: " + pares
                .map(p -> p.isAny(l -> l.first().b >= l.last().a && l.first().a <= l.last().b))
                .count(t -> t));


    }
}
