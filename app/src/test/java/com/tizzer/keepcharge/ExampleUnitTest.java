package com.tizzer.keepcharge;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String s1 = "2018-09-24";
        String s2 = "2018-09-24 23:33:02.655000";
        System.out.println(s1.startsWith(s2));
    }
}