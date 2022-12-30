package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.Tuple;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.IO;
import yk.jcommon.utils.MyMath;

import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.cycle;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.26
 */
public class Aoc20 {

    @Test
    public void test1() {
        YList<Tuple<Integer, Long>> tuples = readTuples("aoc20.test.txt");
        YList<Long> nn = decode(tuples, 1);
        assertEquals(al(4L, -3L, 2L), nn);
        assertEquals((Long)3L, nn.reduce((i, j) -> i + j));
    }

    @Test
    public void test2() {
        YList<Tuple<Integer, Long>> tuples = readTuples("aoc20.test.txt");
        tuples = tuples.map(t -> new Tuple<>(t.a, t.b * 811589153L));
        YList<Long> nn = decode(tuples, 10);
        assertEquals(al(811589153L, 2434767459L, -1623178306L), nn);
        assertEquals((Long)1623178306L, nn.reduce((i, j) -> i + j));
    }

    @Test
    public void answer1() {
        YList<Tuple<Integer, Long>> tuples = readTuples("aoc20.txt");
        YList<Long> nn = decode(tuples, 1);
        System.out.println(nn);
        assertEquals((Long)4578L, nn.reduce((i, j) -> i + j));
    }

    @Test
    public void answer2() {
        YList<Tuple<Integer, Long>> tuples = readTuples("aoc20.txt");
        tuples = tuples.map(t -> new Tuple<>(t.a, t.b * 811589153L));
        YList<Long> nn = decode(tuples, 10);
        assertEquals((Long)2159638736133L, nn.reduce((i, j) -> i + j));
    }

    private YList<Tuple<Integer, Long>> readTuples(final String fileName) {
        YList<Tuple<Integer, Long>> tuples = al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split("\n")).map(Long::parseLong).mapWithIndex((i, n) -> new Tuple<>(i, n));
        return tuples;
    }

    private YList<Long> decode(YList<Tuple<Integer, Long>> tuples, int times) {
        //System.out.println(tuples.map(t -> t.b));
        for (int j = 0; j < times; j++) {
            for (int i = 0; i < tuples.size(); i++) {
                int finalI = i;
                Tuple<Integer, Long> tuple = tuples.first(t -> t.a == finalI);
                int ind = tuples.indexOf(tuple);
                tuples.remove(ind);
                tuples.add((int) cycle(ind + tuple.b, tuples.size()), tuple);
            }
            //System.out.println("R" + (j + 1) + tuples.map(t -> t.b));
        }
        int zeroIndex = tuples.indexOf(tuples.first(t -> t.b == 0));
        YList<Long> nn = al(
                tuples.get(MyMath.cycle(zeroIndex + 1000, tuples.size())).b,
                tuples.get(MyMath.cycle(zeroIndex + 2000, tuples.size())).b,
                tuples.get(MyMath.cycle(zeroIndex + 3000, tuples.size())).b);
        return nn;
    }
}
