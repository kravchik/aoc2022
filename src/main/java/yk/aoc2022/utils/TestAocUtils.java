package yk.aoc2022.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static yk.aoc2022.utils.AocUtils.split;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by yuri at 2022.12.13
 */
public class TestAocUtils {
    @Test
    public void testTranspose() {
        assertEquals(al(al(0)), al(al(0)).forThis(AocUtils::transpose));
        assertEquals(al(al(0, 1, 2)), al(al(0), al(1), al(2)).mapThis(AocUtils::transpose));
        assertEquals(al(al(0), al(1), al(2)), al(al(0, 1, 2)).mapThis(AocUtils::transpose));
        assertEquals(al(al(0, 1), al(1, 2), al(2, 3)), al(al(0, 1, 2), al(1, 2, 3)).mapThis(AocUtils::transpose));
    }

    @Test
    public void testSplit() {
        assertEquals(al(), split(al(), 1));
        assertEquals(al(al(1)), split(al(1), 1));
        assertEquals(al(al(1)), split(al(1), 2));
        assertEquals(al(al(1, 2)), split(al(1, 2), 2));
        assertEquals(al(al(1), al(2)), split(al(1, 2), 1));
        assertEquals(al(al(1, 2)), split(al(1, 2), 0));
    }


}
