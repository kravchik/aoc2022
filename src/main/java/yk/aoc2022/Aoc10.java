package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.IO;
import yk.jcommon.utils.MyMath;

import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.split;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Vec2i.v2i;

/**
 * Created by yuri at 2022.12.19
 */
@SuppressWarnings("SuspiciousNameCombination")
public class Aoc10 {

    public static void main(String[] args) {
        YList<String> comands = al(IO.readFile("src/main/java/yk/aoc2022/aoc10.txt").split("\n"));
        YList<Vec2i> history = executeCpu(comands);
        System.out.println("Result 1 : " + calcStrength(history));
        System.out.println(executeCrt(history).toString("\n"));
    }

    @Test
    public void test1() {
        YList<String> comands = al(IO.readFile("src/main/java/yk/aoc2022/aoc10.test.txt").split("\n"));

        YList<Vec2i> cpuHistory = executeCpu(comands);
        System.out.println(cpuHistory);
        int strength = calcStrength(cpuHistory);
        assertEquals(13140, strength);
        System.out.println(strength);

        String pic = executeCrt(cpuHistory).toString("\n");
        System.out.println(pic);

        assertEquals("##..##..##..##..##..##..##..##..##..##..\n" +
                "###...###...###...###...###...###...###.\n" +
                "####....####....####....####....####....\n" +
                "#####.....#####.....#####.....#####.....\n" +
                "######......######......######......####\n" +
                "#######.......#######.......#######.....", pic);
    }


    private static int calcStrength(YList<Vec2i> history) {
        int strength = 0;
        for(int i = 20-1; i < history.size(); i += 40) strength += history.get(i).x * history.get(i).y;
        return strength;
    }

    public static YList<String> executeCrt(YList<Vec2i> cpuHistory) {
        return split(cpuHistory.mapWithIndex((i, v) -> MyMath.abs(v.y - i % 40) < 2 ? "#" : "."), 40)
                .map(l -> l.toString(""));
    }

    private static YList<Vec2i> executeCpu(YList<String> comands) {
        int regX = 1;
        int cycle = 1;
        YList<Vec2i> history = al();
        for (String command : comands) {
            if (command.equals("noop")) history.add(v2i(cycle++, regX));
            else if (command.startsWith("addx")) {
                history.add(v2i(cycle++, regX));
                history.add(v2i(cycle++, regX));
                regX += Integer.parseInt(command.split(" ")[1]);
            }
            else throw BadException.shouldNeverReachHere();
        }
        return history;
    }
}
