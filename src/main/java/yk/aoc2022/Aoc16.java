package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.collections.YSet;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.IO;

import static junit.framework.TestCase.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.toYList;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.collections.YHashSet.hs;

/**
 * Created by yuri at 2022.12.21
 */
public class Aoc16 {

    public static void main(String[] args) {
        YMap<String, Valve> net = readNet(IO.readFile("src/main/java/yk/aoc2022/" + "aoc16.txt"));
        fillPassages(net);

        System.out.println(net.toString("\n"));

        assertEquals(2124f, findBestState(net, al(new Actor(net.get("AA"), 0))).totalFlow);

        assertEquals(2775f, findBestState(net, al(new Actor(net.get("AA"), 4), new Actor(net.get("AA"), 4))).totalFlow);
    }

    @Test
    public void test1() {
        YMap<String, Valve> net = readNet(IO.readFile("src/main/java/yk/aoc2022/" + "aoc16.test.txt"));
        fillPassages(net);

        System.out.println(net.toString("\n"));

        State16 best = findBestState(net, al(new Actor(net.get("AA"), 0)));
        assertEquals(1651f, best.totalFlow);
        System.out.println("Selected: " + best.totalFlow);
    }

    @Test
    public void test2() {
        YMap<String, Valve> net = readNet(IO.readFile("src/main/java/yk/aoc2022/" + "aoc16.test.txt"));
        fillPassages(net);

        System.out.println(net.toString("\n"));

        State16 best = findBestState(net, al(new Actor(net.get("AA"), 4), new Actor(net.get("AA"), 4)));
        assertEquals(1707f, best.totalFlow);
        System.out.println("Selected: " + best.totalFlow);
    }

    private static State16 findBestState(YMap<String, Valve> net, YArrayList<Actor> actors) {
        State16 initial = new State16();
        initial.state = net.map((k, v) -> false);
        initial.actors = actors;
        YList<State16> edge = al(initial);

        YMap<YMap<String, Boolean>, Float> bestStates = hm();
        State16 best = null;
        System.out.println(initial);
        for (int i = 0; i < 100000000; i++) {
            if (edge.isEmpty()) break;
            State16 cur = edge.remove(edge.size() - 1);

            //int selectedActor = cur.actors.indexOf(cur.actors.min(a -> a.atMinute));
            YList<State16> newStates = al();
            for (int selectedActor = 0; selectedActor < cur.actors.size(); selectedActor++) {
                int finalSelectedActor = selectedActor;
                newStates.addAll(cur.actors.get(selectedActor).atValve.passes
                        .filter((k, v) -> !cur.state.get(k))
                        .filter((k, v) -> net.get(k).rate > 0)
                        .mapToList((p, len) -> cur.moveAndOpen(finalSelectedActor, net.get(p), len))
                        .filter(s -> bestStates.getOrDefault(s.state, 0f) < s.totalFlow)
                        .filter(s -> s.actors.get(finalSelectedActor).atMinute < 30));
            }
            if (newStates.isEmpty()) {
                if (best == null || best.totalFlow < cur.totalFlow) {
                    best = cur;
                    System.out.println("New best: " + best);
                }
            } else {
                for (State16 state : newStates) bestStates.put(state.state, state.totalFlow);
                edge.addAll(newStates);
                edge = edge.withAll(newStates).sorted(
                        e -> e.totalFlow - e.actors.reduce(0, (sum, a) -> sum + a.atMinute) * 100);
            }
        }
        return best;
    }

    private static YMap<String, Valve> readNet(String lines) {
        return al(lines.split("\n"))
                .map(l -> al(l.replace("Valve ", "")
                        .replace(" has flow rate=", "/")
                        .replace("; tunnels lead to valves ", "/")
                        .replace("; tunnel leads to valve ", "/")
                        .split("/")))
                .forThis(t -> System.out.println(t.toString("\n")))
                .map(l -> new Valve(l.get(0), Float.parseFloat(l.get(1)), al(l.get(2).split(", "))))
                .groupBy(p -> p.name)
                .map((k, v) -> v.assertSize(1).first());
    }

    private static void fillPassages(YMap<String, Valve> net) {
        net.keySet().forEach(from -> markPasses(net, from));
    }

    private static void markPasses(YMap<String, Valve> net, String from) {
        YList<String> edge = al(from);
        YSet<String> seen = hs(from);
        for (int i = 1; i < 10000 && edge.notEmpty(); i++) {
            int finalStep = i;
            edge = edge
                    .flatMap(s -> net.get(s).tunnels)
                    .filter(next -> seen.add(next))
                    .forThis(e -> e.forEach(to -> net.get(from).passes.put(to, finalStep)));
        }
    }

    public static class Actor {
        public Valve atValve;
        public int atMinute;

        public Actor(Valve atValve, int atMinute) {
            this.atValve = atValve;
            this.atMinute = atMinute;
        }

        @Override
        public String toString() {
            return "Actor{" +
                    "atValve=" + atValve.name +
                    ", atMinute=" + atMinute +
                    '}';
        }
    }

    public static class State16 {
        public State16 prev;
        public YMap<String, Boolean> state;
        public float totalFlow;

        YList<Actor> actors = al();

        public State16 moveAndOpen(int actorIndex, Valve moveTo, int pathLen) {
            Actor actor = actors.get(actorIndex);
            if (moveTo == actor.atValve) throw BadException.die("The same place");
            if (state.get(moveTo.name)) throw BadException.die("Already open");
            State16 result = new State16();
            result.prev = this;
            Actor newActor = new Actor(moveTo, actor.atMinute + pathLen + 1);
            result.actors = toYList(this.actors);
            result.actors.set(actorIndex, newActor);
            result.state = state.with(moveTo.name, true);
            result.totalFlow = totalFlow + moveTo.rate * (30 - newActor.atMinute);
            return result;
        }

        @Override
        public String toString() {
            return "State16{" +
                    "actors=" + actors +
                    ", totalFlow=" + totalFlow +
                    ", state=" + state.filter((k, v) -> v).mapToList((k, v) -> k).toString(" ") +
                    '}';
        }
    }

    public static class Valve {
        public String name;
        public float rate;
        public YList<String> tunnels;
        public YMap<String, Integer> passes = hm();

        public Valve(String name, float rate, YList<String> tunnels) {
            this.name = name;
            this.rate = rate;
            this.tunnels = tunnels;
        }

        @Override
        public String toString() {
            return "Place{" +
                    "valve='" + name + '\'' +
                    ", rate=" + rate +
                    ", tunnels=" + tunnels +
                    ", passes=" + passes +
                    '}';
        }
    }

    @Test
    public void testFillPasses() {
        YMap<String, Valve> net = readNet("Valve AA has flow rate=0; tunnels lead to valves BB, CC\n" +
                "Valve BB has flow rate=13; tunnels lead to valves AA\n" +
                "Valve CC has flow rate=2; tunnels lead to valves BB\n");
        fillPassages(net);
        System.out.println(net.toString("\n"));
    }
}
