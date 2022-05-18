package proxy;

import channelpool.UpstreamChannelPool;
import channelpool.UrlBasedChannelResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ProxyServerBootstrap {
    private final EventLoopGroup acceptors;
    private final EventLoopGroup workers;
    private final int listen;
	private final Map<String, UpstreamChannelPool> upstreamChannelPools;

    public ProxyServerBootstrap(ProxyBootstrapConfig proxyBootstrapConfig) {
        this.acceptors = new NioEventLoopGroup(proxyBootstrapConfig.getAcceptorCount());
        this.workers = new NioEventLoopGroup(proxyBootstrapConfig.getWorkerCount());
        this.listen = proxyBootstrapConfig.getListen();
		this.upstreamChannelPools = this.initializeUpstreamChannelPool(proxyBootstrapConfig);
    }

    public void start() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(acceptors, workers)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            // pipeline.addLast(new HttpRequestDecoder());
                            // pipeline.addLast(new HttpObjectAggregator(1048576));
                            // pipeline.addLast(new HttpResponseEncoder());
						 pipeline.addLast(new RequestForwardingHandler(new UrlBasedChannelResolver(upstreamChannelPools)));
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(listen).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptors.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }

    public void stop() {

    }

    private Map<String, UpstreamChannelPool> initializeUpstreamChannelPool(ProxyBootstrapConfig proxyBootstrapConfig) {
		Bootstrap clientBootstrap = initializeClientBootstrap();
        Map<String, List<String>> locations = proxyBootstrapConfig.getLocations();

		return locations.entrySet().stream()
				.map(entry-> Map.entry(entry.getKey(), entry.getValue().stream()
						.map(upstream -> upstream.split(":"))
						.map(segments -> new InetSocketAddress(segments[0], Integer.parseInt(segments[1])))
						.collect(Collectors.toCollection(CopyOnWriteArrayList::new))))
				.map(entry -> Map.entry(entry.getKey(), new UpstreamChannelPool(clientBootstrap, entry.getValue())))
				.collect(Collectors.toConcurrentMap(Map.Entry::getKey,Map.Entry::getValue));
    }

    private Bootstrap initializeClientBootstrap() {
        return new Bootstrap()
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioSocketChannel.class)
                .group(workers);
    }
}
