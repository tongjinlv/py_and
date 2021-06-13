package com.matatalab.matatacode.model;

import com.matatalab.matatacode.AppConst;
import com.matatalab.matatacode.utils.MLog;

import java.util.LinkedList;

public class MyQueue<T> {
    private LinkedList list = new LinkedList();
    public static MyQueue queue=new MyQueue();
    public void clear()//销毁队列
    {
        list.clear();
    }
    public boolean QueueEmpty()//判断队列是否为空
    {
        return list.isEmpty();
    }
    public void enQueue(Object o)//进队
    {
        list.addLast(o);
    }
    public Object deQueue()//出队
    {
        if(!list.isEmpty())
        {
            return list.removeFirst();
        }
        return "队列为空";
    }
    public int QueueLength()//获取队列长度
    {
        return list.size();
    }
    public Object QueuePeek()//查看队首元素
    {
        return list.getFirst();
    }
    public static void main()//测试队列
    {
        MyQueue queue = new MyQueue();
        MLog.td("tjl",String.valueOf(queue.QueueEmpty()));
        queue.enQueue("a");
        queue.enQueue("b");
        queue.enQueue("c");
        queue.enQueue("d");
        queue.enQueue("e");
        queue.enQueue("f");
        MLog.td("tjl",String.valueOf(queue.QueueLength()));
        MLog.td("tjl",String.valueOf(queue.deQueue()));
        MLog.td("tjl",String.valueOf(queue.deQueue()));
        MLog.td("tjl",String.valueOf(queue.QueuePeek()));
        MLog.td("tjl",String.valueOf(queue.deQueue()));
        queue.clear();
        queue.enQueue("s");
        queue.enQueue("t");
        queue.enQueue("r");
        MLog.td("tjl",String.valueOf(queue.deQueue()));
        MLog.td("tjl",String.valueOf(queue.QueueLength()));
        MLog.td("tjl",String.valueOf(queue.QueuePeek()));
        MLog.td("tjl",String.valueOf(queue.deQueue()));
    }
}