package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.Tuple;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Vec2i.v2i;

/**
 * Created by yuri at 2022.12.20
 */
public class Aoc15 {

    //116454520000002855041 is too high
    public static void main(String[] args) {
        YList<Tuple<YList<Vec2i>, Integer>> data = readData("src/main/java/yk/aoc2022/aoc15.txt");
        System.out.println(data.toString("\n"));
        int atY = 2000000;
        Vec2i range = rangesReduceFun(rangesForLine(data, atY)).assertSize(1).first();
        assertEquals(4876693, range.y - range.x);

        Vec2i distress = findDistress(data, 4_000_000);
        assertEquals(11645454855041L, distress.x * 4_000_000L + distress.y);
    }

    @Test
    public void test1() {
        YList<Tuple<YList<Vec2i>, Integer>> data = readData("src/main/java/yk/aoc2022/aoc15.test.txt");
        System.out.println(data.toString("\n"));
        Vec2i range = rangesReduceFun(rangesForLine(data, 10)).assertSize(1).first();
        assertEquals(26, range.y - range.x);
    }

    @Test
    public void test2() {
        YList<Tuple<YList<Vec2i>, Integer>> data = readData("src/main/java/yk/aoc2022/aoc15.test.txt");
        System.out.println(data.toString("\n"));
        Vec2i distress = findDistress(data, 20);
        System.out.println(distress);
        assertEquals(56000011, distress.x * 4_000_000L + distress.y);
    }

    private static Vec2i findDistress(YList<Tuple<YList<Vec2i>, Integer>> data, int size) {
        YList<YList<Vec2i>> area = al();
        for (int i = 0; i <= size; i++) area.add(rangesReduceFun(rangesForLine(data, i)));
        area.filter(r -> r.size() > 1).assertSize(1);
        int foundY = 0;
        for (int i = 0; i <= size; i++) if (area.get(i).size() > 1) foundY = i;
        return v2i(area.get(foundY).first().y + 1, foundY);
    }

    private static YList<Tuple<YList<Vec2i>, Integer>> readData(String s2) {
        YList<String> lines = al(IO.readFile(s2).split("\n"));
        return lines.map(l -> al(l.replace("x=", "").replace("y=", "")
                        .split("Sensor at ")[1].split(": closest beacon is at "))
                        .map(s -> v2i(parseInt(s.split(", ")[0]), parseInt(s.split(", ")[1]))))
                .map(vv -> new Tuple<>(vv, manhattan(vv.first(), vv.last())));
    }

    public static YList<Vec2i> rangesForLine(YList<Tuple<YList<Vec2i>, Integer>> sensors, int atY) {
        return sensors.map(s -> {
            Vec2i sensor = s.a.first();
            Integer r = s.b;
            int dx = r - abs(atY - sensor.y);
            if (dx < 0) return null;
            return v2i(sensor.x, sensor.x).add(-dx, dx);

        }).filter(s -> s != null);
    }

    //TODO try reduce with side effects
    public static YList<Vec2i> rangesReduceImperative(YList<Vec2i> rr) {
        rr = rr.sorted(r -> r.x);

        YList<Vec2i> newRr = al();
        a:
        for (int i = 0; i < rr.size(); i++) {
            Vec2i r1 = rr.get(i);

            for (int j = i + 1; j < rr.size(); j++) {
                Vec2i r2 = rr.get(j);

                if (r2.x - 1 <= r1.y) {
                    r1 = v2i(r1.x, max(r1.y, r2.y));
                } else {
                    i = j;
                    newRr.add(r1);
                    continue a;
                }
            }
            newRr.add(r1);
            break;
        }
        return newRr;
    }

    public static YList<Vec2i> rangesReduceFun(YList<Vec2i> rr) {
        return (YList)al().forThis(result -> result.add(rr.sorted(r -> r.x).reduce((r1, r2) -> {
            if (r2.x - 1 <= r1.y) return v2i(r1.x, max(r1.y, r2.y));
            result.add(r1);
            return r2;
        })));
    }

    public static int manhattan(Vec2i a, Vec2i b) {
        return abs(a.x - b.x) + abs(a.y - b.y);
    }
}
