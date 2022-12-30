package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YSet;
import yk.jcommon.fastgeom.Vec3i;
import yk.jcommon.utils.IO;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashSet.hs;
import static yk.jcommon.fastgeom.Vec3i.v3i;

/**
 * Created by yuri at 2022.12.23
 */
public class Aoc18 {

    public static final YList<Vec3i> DIRS = al(
            v3i(1, 0, 0),
            v3i(-1, 0, 0),
            v3i(0, 1, 0),
            v3i(0, -1, 0),
            v3i(0, 0, 1),
            v3i(0, 0, -1)
    );

    @Test
    public void test1() {
        assertEquals(64, getAllFaces(readDroplet("aoc18.test.txt")));
    }

    @Test
    public void test2() {
        YSet<Vec3i> droplet = readDroplet("aoc18.test.txt");
        YSet<Vec3i> steam = getSteam(droplet);
        assertEquals(58, getAllSteamedFaces(droplet, steam));
    }

    @Test
    public void answer1() {
        assertEquals(4450, getAllFaces(readDroplet("aoc18.txt")));
    }

    @Test
    public void answer2() {
        YSet<Vec3i> droplet = readDroplet("aoc18.txt");
        YSet<Vec3i> steam = getSteam(droplet);
        assertEquals(2564, getAllSteamedFaces(droplet, steam));
    }

    private int getAllFaces(YSet<Vec3i> droplet) {
        return droplet.reduce(0, (i, v) -> i + DIRS
                .reduce(0, (j, d) -> j + (droplet.contains(v.add(d)) ? 0 : 1)));
    }

    private int getAllSteamedFaces(YSet<Vec3i> droplet, YSet<Vec3i> steam) {
        return droplet.reduce(0, (i, v) -> i + DIRS
                .reduce(0, (j, d) -> j + (droplet.contains(v.add(d)) || !steam.contains(v.add(d)) ? 0 : 1)));
    }

    private YSet<Vec3i> getSteam(YSet<Vec3i> droplet) {
        Vec3i l = v3i(
                droplet.map(d -> d.x).min(),
                droplet.map(d -> d.y).min(),
                droplet.map(d -> d.z).min()).sub(1);
        Vec3i r = v3i(
                droplet.map(d -> d.x).max(),
                droplet.map(d -> d.y).max(),
                droplet.map(d -> d.z).max()).add(1);

        YList<Vec3i> steamFront = al(l);
        YSet<Vec3i> steam = hs();
        while(steamFront.notEmpty()) {
            Vec3i cur = steamFront.remove(steamFront.size() - 1);
            DIRS.map(d -> cur.add(d))
                    .filter(c -> c.max(l).equals(c))
                    .filter(c -> c.min(r).equals(c))
                    .filter(c -> !droplet.contains(c))
                    .filter(c -> !steam.contains(c))
                    .forThis(t -> steamFront.addAll(t))
                    .forThis(t -> steam.addAll(t));
        }

        return steam;
    }

    private YSet<Vec3i> readDroplet(String fileName) {
        return al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split("\n"))
                .map(l -> l.split(","))
                .map(l -> v3i(parseInt(l[0]), parseInt(l[1]), parseInt(l[2]))).toSet();
    }
}
