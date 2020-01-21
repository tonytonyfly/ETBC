package club.crabglory.www.data.netkit.push.foo.handle;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

import club.crabglory.www.data.netkit.push.clink.box.StringReceivePacket;
import club.crabglory.www.data.netkit.push.clink.core.Connector;
import club.crabglory.www.data.netkit.push.clink.core.IoContext;
import club.crabglory.www.data.netkit.push.clink.core.Packet;
import club.crabglory.www.data.netkit.push.clink.core.ReceivePacket;
import club.crabglory.www.data.netkit.push.clink.utils.CloseUtils;
import club.crabglory.www.data.netkit.push.foo.Foo;

public class ConnectorHandler extends Connector {
    private final File cachePath;
    private final String clientInfo;
    private final ConnectorCloseChain closeChain = new DefaultPrintConnectorCloseChain();
    // 用来做头，默认不消费
    private final ConnectorStringPacketChain stringPacketChain = new DefaultNonConnectorStringPacketChain();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ConnectorHandler(SocketChannel socketChannel, File cachePath) throws IOException {
        this.clientInfo = socketChannel.getRemoteAddress().toString();
        this.cachePath = cachePath;
        setup(socketChannel);
    }

    public String getClientInfo() {
        return clientInfo;
    }

    /**
     * 外部调用的退出操作
     */
    public void exit() {
        CloseUtils.close(this);
    }

    /**
     * 内部监测到链接断开的回调
     *
     * @param channel SocketChannel
     */
    @Override
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel);
        closeChain.handle(this, this);
    }

    /**
     * 开启责任链的消费
     */
    @Override
    protected void onReceivedPacket(ReceivePacket packet) {
        super.onReceivedPacket(packet);
        switch (packet.type()) {
            case Packet.TYPE_MEMORY_STRING: {
                deliveryStringPacket((StringReceivePacket) packet);
                break;
            }
            default: {
                System.out.println("New Packet:" + packet.type() + "-" + packet.length());
            }
        }
    }

    @Override
    protected File createNewReceiveFile(long length, byte[] headerInfo) {
        return Foo.createRandomTemp(cachePath);
    }

    @Override
    protected OutputStream createNewReceiveDirectOutputStream(long length, byte[] headerInfo) {
        // 服务器默认创建一个内存存储ByteArrayOutputStream
        return new ByteArrayOutputStream();
    }

    /**
     * 避免阻塞当前的数据读取线程调度，则单独交给另外一个调度线程进行数据调度
     * 进行责任链的消费
     * @param packet StringReceivePacket
     */
    private void deliveryStringPacket(StringReceivePacket packet) {
        IoContext.get().getScheduler().delivery(() -> stringPacketChain.handle(this, packet));
    }

    /**
     * 获取当前链接的消息处理责任链 链头
     *
     * @return ConnectorStringPacketChain
     */
    public ConnectorStringPacketChain getStringPacketChain() {
        return stringPacketChain;
    }

    /**
     * 获取当前链接的关闭链接处理责任链 链头
     *
     * @return ConnectorCloseChain
     */
    public ConnectorCloseChain getCloseChain() {
        return closeChain;
    }
}
