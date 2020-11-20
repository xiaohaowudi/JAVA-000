package com.javacamp.byte_buddy_aop;

public class TestAOP {

    public static void main(String[] args) {
        try {
            Func funcObj = (Func)LogTimeAOP.loadLogTimeExpClass(Func.class).newInstance();
            Integer ret = funcObj.func();
            System.out.println("ret = " + ret);

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
