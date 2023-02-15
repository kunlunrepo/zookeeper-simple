package com.msb.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * description :
 *
 * @author kunlunrepo
 * date :  2023-02-15 18:07
 */
public class WatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    private ZooKeeper zk;

    private MyConf conf;

    CountDownLatch cc = new CountDownLatch(1);

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setConf(MyConf conf) {
        this.conf = conf;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/AppConf", this, this, "sdfs");

                break;
            case NodeDeleted:
                // 容忍性
                conf.setConf("");
                cc = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/AppConf", this, this, "sdfs");

                break;
            case NodeChildrenChanged:
                break;
        }
    }

    public void await() {
        zk.exists("/AppConf", this, this, "ABC");
        try {
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            String s = new String(data);
            conf.setConf(s); // 两个线程数据传递的过程
            cc.countDown();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            zk.getData("/AppConf", this, this, "sdfs");
//            try {
//                cc.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
