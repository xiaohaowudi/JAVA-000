package com.javacamp.test_func;

// 测试Instrument是否成功的主类
public class TestFunc {
    public static void main(String[] args) {
        Func funcObj = new Func();

        System.out.println(funcObj.getClass().getName());
        System.out.println("reuslt is : " + funcObj.func());
    }
}
