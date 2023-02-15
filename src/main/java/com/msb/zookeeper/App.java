package com.msb.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * description :
 *
 * @author kunlunrepo
 * date :  2023-02-15 10:32
 */
public class App {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        System.out.println("hello zookeeper");

        // zk有session概念，没有线程池的概念
        // watch 观察 回调
        // watch 注册只发生在读类型调用 get
        // 第一类：new zk时候 传入的watch，这个watch，session级别的，跟path，node没有关系
        CountDownLatch cd = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper("192.168.10.81:2181,192.168.10.82:2181,192.168.10.83:2181,192.168.10.84:2181",
                3000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        // 回调方法
                        Event.KeeperState state = event.getState();
                        Event.EventType type = event.getType();
                        String path = event.getPath();
                        System.out.println(event.toString());

                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                System.out.println("Connected");
                                cd.countDown();
                                break;
                            case AuthFailed:
                                break;
                            case ConnectedReadOnly:
                                break;
                            case SaslAuthenticated:
                                break;
                            case Expired:
                                break;
                        }

                        switch (type) {
                            case None:
                                break;
                            case NodeCreated:
                                break;
                            case NodeDeleted:
                                break;
                            case NodeDataChanged:
                                break;
                            case NodeChildrenChanged:
                                break;
                        }

                    }
                });

        cd.await(); // 阻塞
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("zk connecting");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("zk connected");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        // 创建节点
        String pathName = zk.create("/ooxx1", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Stat stat = new Stat();
        byte[] data = zk.getData("/ooxx1",
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        System.out.println("getData watch: " + event);
                    }
                },
                stat);
        System.out.println(new String(data));

        // 触发问题
        Stat stat1 = zk.setData("/ooxx1", "newData".getBytes(), 0);
        // 还回触发吗？
        Stat stat2 = zk.setData("/ooxx1", "newData01".getBytes(), stat1.getVersion());

        System.out.println("---------async start---------");
        zk.getData("/ooxx1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("---------async callback---------");
                System.out.println(ctx.toString()); // 参数
                System.out.println(new String(data)); // 值
            }
        }, "abc");
        System.out.println("---------async over---------");


        Thread.sleep(2222222);
    }
}
