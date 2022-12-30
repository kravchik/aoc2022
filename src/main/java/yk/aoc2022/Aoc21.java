package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.IO;

import java.util.function.Function;

import static java.lang.Long.parseLong;
import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.26
 */
public class Aoc21 {

    @Test
    public void test1() {
        assertEquals(152, simpleExecute(readMonkeys("aoc21.test.txt"), "root"));
    }

    @Test
    public void test2() {
        YMap<String, Object> monkeys = readMonkeys("aoc21.test.txt");
        assertEquals(301L, complexExecute(monkeys, "root"));
    }

    @Test
    public void answer1() {
        assertEquals(155708040358220L, simpleExecute(readMonkeys("aoc21.txt"), "root"));
    }

    @Test
    public void answer2() {
        YMap<String, Object> monkeys = readMonkeys("aoc21.txt");
        assertEquals(3342154812537L, complexExecute(monkeys, "root"));
    }

    private YMap<String, Object> readMonkeys(final String fileName) {
        return al(IO.readFile("src/main/java/yk/aoc2022/" + fileName).split("\n")).map(l -> {
            String[] ss = l.split(" ");
            String name = ss[0].replace(":", "");
            return ss.length > 2 ? al(name, ss[1], ss[2], ss[3]) : al(name, parseLong(ss[1]));
        }).groupBy(l -> (String) l.first()).map((k, v)
                -> v.assertSize(1).first().mapThis(e -> e.size() == 2 ? e.get(1) : e.cdr()));
    }

    public static long simpleExecute(YMap<String, Object> program, String name) {
        Object value = program.get(name);
        if (value instanceof Long) return (Long)value;
        YList<String> l = (YList) value;
        String op = l.get(1);
        long arg1 = simpleExecute(program, l.get(0));
        long arg2 = simpleExecute(program, l.get(2));
        return execute(op, arg1, arg2);
    }

    private static long execute(String op, long arg1, long arg2) {
        if ("+".equals(op)) return arg1 + arg2;
        if ("-".equals(op)) return arg1 - arg2;
        if ("*".equals(op)) return arg1 * arg2;
        if ("/".equals(op)) return arg1 / arg2;
        throw BadException.die("Unexpected op " + op);
    }

    public static Object complexExecute(YMap<String, Object> program, String name) {
        Object value = program.get(name);
        if (value instanceof Long) return name.equals("humn")
                ? (Function<Long, Long>) n -> n
                : value;
        YList<String> l = (YList) value;
        String op = l.get(1);
        Object arg1 = complexExecute(program, l.get(0));
        Object arg2 = complexExecute(program, l.get(2));

        if ("root".equals(name)) return arg1 instanceof Long
                    ? ((Function<Long, Long>) arg2).apply((Long) arg1)
                    : ((Function<Long, Long>) arg1).apply((Long) arg2);

        if (arg1 instanceof Long && arg2 instanceof Long) return execute(op, (Long)arg1, (Long)arg2);

        if (arg1 instanceof Long) {
            Long l1 = (Long) arg1;
            Function<Long, Long> f = (Function<Long, Long>) arg2;
            if ("+".equals(op)) return (Function<Long, Long>) n -> f.apply(n - l1);
            if ("-".equals(op)) return (Function<Long, Long>) n -> f.apply(l1 - n);
            if ("*".equals(op)) return (Function<Long, Long>) n -> f.apply(n / l1);
            if ("/".equals(op)) return (Function<Long, Long>) n -> f.apply(l1 / n);
            throw BadException.die("Unexpected op " + op);
        }
        
        if (arg2 instanceof Long) {
            Long l2 = (Long) arg2;
            Function<Long, Long> f = (Function<Long, Long>) arg1;
            if ("+".equals(op)) return (Function<Long, Long>) n -> f.apply(n - l2);
            if ("-".equals(op)) return (Function<Long, Long>) n -> f.apply(l2 + n);
            if ("*".equals(op)) return (Function<Long, Long>) n -> f.apply(n / l2);
            if ("/".equals(op)) return (Function<Long, Long>) n -> f.apply(l2 * n);
            throw BadException.die("Unexpected op " + op);
        }
        throw BadException.die("");
    }
}
