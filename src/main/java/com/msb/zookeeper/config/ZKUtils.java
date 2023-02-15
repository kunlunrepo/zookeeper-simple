package com.msb.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * description :
 *
 * @author kunlunrepo
 * date :  2023-02-15 17:22
 */
public class ZKUtils {

    private static ZooKeeper zk;

    private static String address = "192.168.10.81:2181,192.168.10.82:2181,192.168.10.83:2181,192.168.10.84:2181/testConf";

    private static DefaultWatch watch = new DefaultWatch();

    private static CountDownLatch init = new CountDownLatch(1);
    public static ZooKeeper getZK() {
        try {
            zk = new ZooKeeper(address, 1000, watch);
            watch.setCc(init);
            init.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }
}
