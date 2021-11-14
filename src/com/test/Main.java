package com.test;

abstract class Super
{
    public Super() {System.out.println("Super Constructor");}

    public void meth1()
    {
        System.out.println("Method 1 of Super");
    }
    abstract void meth2();
}

class sub extends Super
{
    @Override
    void meth2()
    {
        System.out.println("Method 2 of Sub");
    }
}

public class Main
{
    public static void main(String args[])
    {
        sub s = new sub();
        s.meth1();
        s.meth2();
    }
}