package protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import protobuf.codecProtocol.CustomProtocolDecoder;
import protobuf.codecProtocol.CustomProtocolEncoder;
import protobuf.messageManager.MessageCenter;

/**
 * @author lishikun
 * @version 1.0
 * @date 2018年9月05日
 */
public class SubReqServer {
    private SubReqServer(){}
    private static SubReqServer instance;
    public static SubReqServer getInstance(){
        if(instance==null){
            synchronized (SubReqServer.class){
                if(instance==null){
                    instance=new SubReqServer();
                }
            }
        }
        return instance;
    }

    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            /*ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(ClientProtocolProto.ClientProtocol.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(SubReqServerHandler.getInstance());*/
                            ch.pipeline().addLast("decoder",new CustomProtocolDecoder());
                            ch.pipeline().addLast("encoder",new CustomProtocolEncoder());
                            ch.pipeline().addLast(SubReqServerHandler.getInstance());
                        }
                    });
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        SubReqServer.getInstance().init();
        int port = 8099;
        new SubReqServer().bind(port);
    }

    public void init(){
        MessageCenter.getInstance().init();
    }
}
