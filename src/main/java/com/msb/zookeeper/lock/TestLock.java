package com.msb.zookeeper.lock;

import com.msb.zookeeper.config.MyConf;
import com.msb.zookeeper.config.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * description :
 *
 * @author kunlunrepo
 * date :  2023-02-15 21:26
 */
public class TestLock {

    ZooKeeper zk;

    @Before
    public void conn() {
        zk = ZKUtils.getZK();
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lock() {

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(){
                @Override
                public void run() {
                    WatchCallBack watchCallBack = new WatchCallBack();
                    watchCallBack.setZk(zk);

                    String name = Thread.currentThread().getName();
                    watchCallBack.setThreadName(name);
                    System.out.println("线程名称: " + name);

                    // 抢锁
                    watchCallBack.trylock();

                    // 干活
                    System.out.println( name + "干活..."+ finalI);
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 释放锁
                    watchCallBack.unlock();


                }
            }.start();
        }

        while (true);

    }

}
