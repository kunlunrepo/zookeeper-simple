package com.msb.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * description :
 *
 * @author kunlunrepo
 * date :  2023-02-15 17:59
 */
public class TestConfig {

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
    public void getConf() {

        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setZk(zk);

        MyConf myConf = new MyConf();
        watchCallBack .setConf(myConf);

        watchCallBack.await();

        // 节点不存在

        // 节点存在

        // 节点改变
        while (true) {
            if (null == myConf.getConf() || "".equals(myConf.getConf())) {
                System.out.println("conf 丢了 ...");
                watchCallBack.await();
            } else {
                System.out.println(myConf.getConf());
            }
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
