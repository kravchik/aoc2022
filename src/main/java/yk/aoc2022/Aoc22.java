package yk.aoc2022;

import org.junit.Test;
import yk.aoc2022.utils.AocUtils;
import yk.jcommon.collections.Tuple;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;

import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.jcommon.utils.MyMath.cycle;

/**
 * Created by yuri at 2022.12.26
 */
public class Aoc22 {

    @Test
    public void test1() {
        YList<String> ll = readLines("aoc22.test.txt");
        YList<String> map = extractMap(ll);
        YList<String> commands = extractCommands(ll);
        assertEquals(6032, getPsw(state -> jump(map, state), map, commands));
    }

    @Test
    public void answer1() {
        YList<String> ll = readLines("aoc22.txt");
        YList<String> map = extractMap(ll);
        YList<String> commands = extractCommands(ll);
        assertEquals(159034, getPsw(state -> jump(map, state), map, commands));
    }

    @Test
    public void test2() {
        YList<String> ll = readLines("aoc22.test.txt");
        YList<String> map = extractMap(ll);
        YList<String> commands = extractCommands(ll);
        assertEquals(5031, getPsw(state -> jumpCube(state, map.first().length() / 3, getTopoTest2()), map, commands));
    }

    @Test
    public void answer2() {
        YList<String> ll = readLines("aoc22.txt");
        YList<String> map = extractMap(ll);
        YList<String> commands = extractCommands(ll);
        YList<Transfer> topology = getTopoPart2();
        assertEquals(147245, getPsw(state -> jumpCube(state, map.first().length() / 3, topology), map, commands));
    }

    @Test
    public void testTopo2() {
        assertEquals("Tuple{a=5:20, b=1}", jumpCube(new Tuple<>(v2i(10, 15), 2), 10, getTopoPart2()).toString());
    }

    //TODO automatically generate layout by traversing simple schema

    private YList<Transfer> getTopoTest2() {
        return addBackTransfers(al(
                new Transfer(v2i(2, 0), 0, v2i(3, 2), 0), //A
                new Transfer(v2i(2, 1), 0, v2i(3, 2), 3), //B
                new Transfer(v2i(0, 1), 3, v2i(2, 0), 3), //C
                new Transfer(v2i(1, 1), 3, v2i(2, 0), 2), //D
                new Transfer(v2i(1, 1), 1, v2i(2, 2), 2), //E
                new Transfer(v2i(0, 1), 1, v2i(2, 2), 1)  //F
                //G is missed
        ));
    }

    private YList<Transfer> getTopoPart2() {
        YList<Transfer> result = addBackTransfers(al(
                new Transfer(v2i(1, 1), 2, v2i(0, 2), 3), //A
                new Transfer(v2i(1, 0), 2, v2i(0, 2), 2), //B
                new Transfer(v2i(1, 0), 3, v2i(0, 3), 2), //C
                new Transfer(v2i(2, 0), 3, v2i(0, 3), 1), //D
                new Transfer(v2i(1, 2), 1, v2i(0, 3), 0), //E
                new Transfer(v2i(1, 2), 0, v2i(2, 0), 0), //F
                new Transfer(v2i(2, 0), 1, v2i(1, 1), 0)  //G
        ));
        return result;
    }

    private static YList<String> extractCommands(YList<String> ll) {
        return AocUtils.findAll(ll.last(), "([0-9]+|[RL])");
    }

    private static YList<String> extractMap(YList<String> ll) {
        return ll.subList(0, ll.size() - 2);
    }

    private static YArrayList<String> readLines(String fileName) {
        return al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split("\n"));
    }

    private int getPsw(Function<Tuple<Vec2i, Integer>, Tuple<Vec2i, Integer>> jumper, YList<String> map, YList<String> commands) {

        YList<YList<Character>> debug = map.map(l -> AocUtils.stringToCharacters(l));

        int rot = 0;

        Vec2i pos = v2i(map.first().indexOf('.'), 0);
        addDebug(debug, rot, pos);
        for (String command : commands) {
            //System.out.println(command);
            if ("R".equals(command)) rot = cycle(rot + 1, 4);
            else if ("L".equals(command)) rot = cycle(rot - 1, 4);
            else {
                int steps = parseInt(command);
                for (int i = 0; i < steps; i++) {
                    Vec2i dir = AocUtils.DIRS.get(rot);
                    Vec2i next = pos.add(dir);
                    char at = getAt(map, next);
                    if (at == '#') break;
                    else if (at == '.') pos = next;
                    else {
                        Tuple<Vec2i, Integer> j = jumper.apply(new Tuple<>(pos, rot));
                        if (getAt(map, j.a) == '.') {
                            pos = j.a;
                            rot = j.b;
                        }
                    }
                    addDebug(debug, rot, pos);
                    //soutDebug(debug);
                }
            }
            addDebug(debug, rot, pos);
        }

        pos = pos.add(1);
        soutDebug(debug);
        return 1000 * pos.y + 4 * pos.x + rot;
    }

    private void addDebug(YList<YList<Character>> debug, int rot, Vec2i pos) {
        debug.get(pos.y).set(pos.x, AocUtils.DIR_SYMBOLS.get(rot));
    }

    private void soutDebug(YList<YList<Character>> debug) {
        System.out.println();
        //System.out.println(debug.map(l -> l.toString(" ")).toString("\n"));
        System.out.println(debug.map(l -> l.map(s -> s.equals('.') || s.equals('#') ? s : ' ').toString(" ")).toString("\n"));
        //System.out.println(debug.map(l -> l.map(s -> s.equals('.') || s.equals('#') ? ' ' : s).toString(" ")).toString("\n"));
    }


    private static Tuple<Vec2i, Integer> jump(YList<String> map, Tuple<Vec2i, Integer> state) {
        Vec2i dir = AocUtils.DIRS.get(state.b);
        Vec2i next = state.a;
        while (true){
            next = next.sub(dir);
            if (getAt(map, next) == ' ') {
                Vec2i found = next.add(dir);
                if (getAt(map, found) == '.') state.a = found;
                break;
            }
        }
        return new Tuple<>(state.a, state.b);
    }

    public static Vec2i rot(Vec2i pos, int r, int cs) {
        for (int i = 0; i < r; i++) pos = rotCw(pos).add(cs-1, 0);
        return pos;
    }

    private static Vec2i rotCw(Vec2i input) {
        //noinspection SuspiciousNameCombination
        return v2i(-input.y, input.x);
    }

    public static YList<Transfer> addBackTransfers(YList<Transfer> oneWay) {
        return oneWay.withAll(oneWay.map(t -> new Transfer(t.b, t.bDir, t.a, t.aDir)));
    }

    private static Tuple<Vec2i, Integer> jumpCube(Tuple<Vec2i, Integer> state, int sideSize, YList<Transfer> topology) {
        Vec2i fromSide = state.a.div(sideSize);
        Transfer topo = topology.first(t -> t.a.equals(fromSide) && t.aDir == state.b);
        if (topo == null) {
            System.out.println("wtf: " + state.a + " " + state.b + " " + fromSide);
        }
        return topo.transfer(state.a.add(AocUtils.DIRS.get(state.b)), sideSize);
    }

    public static char getAt(YList<String> map, Vec2i pos) {
        if (pos.y < 0 || pos.y >= map.size()) return ' ';
        String line = map.get(pos.y);
        if (pos.x < 0 || pos.x >= line.length()) return ' ';
        return line.charAt(pos.x);
    }

    public static class Transfer {
        public Vec2i a;
        public int aDir;
        public int bDir;
        public Vec2i b;

        public Transfer(Vec2i a, int aDir, Vec2i b, int bDir) {
            this.a = a;
            this.aDir = aDir;
            this.bDir = bDir;
            this.b = b;
        }

        public Tuple<Vec2i, Integer> transfer(Vec2i pos, int cs) {
            return new Tuple<>(rot(pos.cycle(cs), cycle(bDir - aDir - 2, 4), cs).add(b.mul(cs)), cycle(bDir + 2, 4));
        }
    }
}
