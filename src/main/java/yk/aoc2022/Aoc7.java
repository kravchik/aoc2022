package yk.aoc2022;

import org.junit.Test;
import yk.jcommon.collections.YArrayList;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.utils.IO;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;

/**
 * Created by yuri at 2022.12.14
 */
@SuppressWarnings("ConstantConditions")
public class Aoc7 {
    public static String PARENT = "PARENT";
    public static String DIR = "DIR";

    public static void main(String[] args) {
        YArrayList<String> commands = al(IO.readFile("src/main/java/yk/aoc2022/aoc7.txt").split("\n"));
        FileEntry topDir = executeCommands(commands);
        fillSizes(topDir);
        System.out.println("Result 1: " + calcTask(topDir, 100000));

        int needed = 30000000 - (70000000 - topDir.totalSize);
        System.out.println("Need to free: " + needed);
        System.out.println("Result2: " + getDirSizes(topDir).filter(d -> d >= needed).sorted().first());
    }

    @Test
    public void test1() {
        YArrayList<String> commands = al(IO.readFile("src/main/java/yk/aoc2022/aoc7.test.txt").split("\n"));
        FileEntry topDir = executeCommands(commands);
        assertEquals(IO.readFile("src/main/java/yk/aoc2022/aoc7.test.out.txt"), toString("", topDir));
        fillSizes(topDir);
        System.out.println(toString("", topDir));
        assertEquals(95437, calcTask(topDir, 100000));
    }

    @Test
    public void test2() {
        YArrayList<String> commands = al(IO.readFile("src/main/java/yk/aoc2022/aoc7.test.txt").split("\n"));
        FileEntry topDir = executeCommands(commands);
        fillSizes(topDir);
        assertEquals(48381165, (int)topDir.totalSize);
        int needed = 30000000 - (70000000 - topDir.totalSize);
        assertEquals(24933642, (int)getDirSizes(topDir).filter(d -> d >= needed).sorted().first());
    }

    private static FileEntry executeCommands(YArrayList<String> commands) {
        FileEntry topDir = FileEntry.newDir(null, "/");
        FileEntry curDir = null;
        for (String command : commands) {
            //System.out.println((curDir == null ? "null" : curDir.name) + " " + command);
            if (command.startsWith("$ cd")) {
                String dirName = command.replace("$ cd ", "");
                if (dirName.equals("/")) curDir = topDir;
                else if (dirName.equals("..")) curDir = curDir.parent;
                else curDir = curDir.children.get(dirName);
            } else if (command.startsWith("$ ls")) {
            } else if (command.startsWith("dir")) {
                String dirName = command.replace("dir ", "");
                curDir.children.put(dirName, FileEntry.newDir(curDir, dirName));
            } else {
                String fileName = command.split(" ")[1];
                int size = Integer.parseInt(command.split(" ")[0]);
                curDir.children.put(fileName, FileEntry.newFile(fileName, size));
            }
        }
        return topDir;
    }

    public static int fillSizes(FileEntry dir) {
        if (dir.isDirectory) {
            dir.totalSize = dir.children.mapToList((k, v) -> fillSizes(v)).reduce((i1, i2) -> i1 + i2);
            return dir.totalSize;
        } else return dir.totalSize;
    }

    public static YList<Integer> getDirSizes(FileEntry dir) {
        if (dir.isDirectory) return dir.children.values().toList().flatMap(v -> getDirSizes(v)).with(dir.totalSize);
        else return al();
    }

    public static int calcTask(FileEntry dir, int sizeLimit) {
        if (dir.isDirectory) {
            return dir.children.mapToList((k, v) -> calcTask(v, sizeLimit)).reduce((i1, i2) -> i1 + i2)
                    + (dir.totalSize <= sizeLimit ? dir.totalSize : 0);
        } else return 0;
    }

    public static String toString(String tab, FileEntry entry) {
        if (entry.isDirectory) {
            return tab + String.format("- %s (dir%s)", entry.name, entry.totalSize == null ? "" : ", size=" + entry.totalSize)
                    + entry.children.mapToList((k, v) -> toString(tab + "  ", v)).toStringPrefixInfix("\n", "");
        } else {
            return tab + String.format("- %s (file, size=%s)", entry.name, entry.totalSize);
        }
    }

    public static class FileEntry {
        public String name;
        public FileEntry parent;
        public boolean isDirectory;
        public Integer totalSize;
        public YMap<String, FileEntry> children = hm();

        public static FileEntry newDir(FileEntry parent, String name) {
            FileEntry result = new FileEntry();
            result.parent = parent;
            result.name = name;
            result.isDirectory = true;
            return result;
        }

        public static FileEntry newFile(String name, int size) {
            FileEntry result = new FileEntry();
            result.totalSize = size;
            result.name = name;
            return result;
        }

        @Override
        public String toString() {
            return "FileEntry{" +
                    "name='" + name + '\'' +
                    ", isDirectory=" + isDirectory +
                    ", totalSize=" + totalSize +
                    ", children=" + children +
                    '}';
        }
    }

}
