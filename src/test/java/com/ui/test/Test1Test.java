package com.ui.test;

import com.base.UIBase;
import org.testng.annotations.Test;

public class Test1Test extends UIBase {


    @Test
    public void test1() throws InterruptedException {
        method.navigate("https://www.wikipedia.org/");
        Thread.sleep(10000);
        System.out.println("This is 1st test");
    }

    @Test
    public void test2(){
        System.out.println("This is 2nd test");
    }

}
