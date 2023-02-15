package com.msb.zookeeper.lock;

import com.msb.zookeeper.config.MyConf;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * description :
 *
 * @author kunlunrepo
 * date :  2023-02-15 21:31
 */
public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    private ZooKeeper zk;

    String threadName;

    CountDownLatch cc = new CountDownLatch(1);

    String pathName;

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void trylock() {
        try {
//            if (zk.getData("/"))

            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");

            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void unlock() {
        try {
            zk.delete(pathName, -1);
            System.out.println(threadName + "释放锁");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "sdf");


                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }

    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (null != name) {
            System.out.println( threadName +" 创建节点.... " + name);
            pathName = name;
            zk.getChildren("/", false, this, "sdf");
        }
    }

    // getChildren
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        // 一定能看到自己前边的
//        System.out.println(threadName + "lock locks...");
//        for (String child : children) {
//            System.out.println(threadName + "看到的是: " + child);
//        }

        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));
        if (i == 0) {
            System.out.println(threadName + "我是第一");
            try {
                zk.setData("/", threadName.getBytes(), -1);
            } catch (KeeperException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cc.countDown();
        } else {
            zk.exists("/"+children.get(i-1), this, this,  "sdf");
        }


    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }
}
