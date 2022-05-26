package proxy;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import channelpool.UpstreamChannelPool;
import channelpool.UrlBasedChannelResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import proxy.handler.ForwardToServerHandler;

public class ProxyServerBootstrap {
	private final EventLoopGroup acceptors;
	private final EventLoopGroup workers;
	private final int listen;
	private final Map<String, UpstreamChannelPool> upstreamChannelPools;

	public ProxyServerBootstrap(ProxyBootstrapConfig proxyBootstrapConfig) {
		this.acceptors = new KQueueEventLoopGroup();
		this.workers = new KQueueEventLoopGroup();
		this.listen = proxyBootstrapConfig.getListen();
		this.upstreamChannelPools = this.initializeUpstreamChannelPool(proxyBootstrapConfig);
	}

	public void start() {
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(acceptors, workers)
				.channel(KQueueServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.DEBUG))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					private final UrlBasedChannelResolver resolver = new UrlBasedChannelResolver(upstreamChannelPools);
					@Override
					protected void initChannel(SocketChannel channel) {
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast("clientLogger",new LoggingHandler(LogLevel.DEBUG));
						pipeline.addLast(new HttpRequestDecoder()); // 프록시로 들어오는 메시지 디코딩
						pipeline.addLast(new HttpResponseEncoder()); // 클라이언트로 보내는 메시지 인코딩
						pipeline.addLast(new HttpObjectAggregator(1048576));
						pipeline.addLast("request-forwarding-handler",new ForwardToServerHandler(resolver)); // 프록시 -> 서버로 메시지 포워딩
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
			.map(entry -> Map.entry(entry.getKey(), entry.getValue().stream()
				.map(upstream -> upstream.split(":"))
				.map(segments -> new InetSocketAddress(segments[0], Integer.parseInt(segments[1])))
				.collect(Collectors.toCollection(CopyOnWriteArrayList::new))))
			.map(entry -> Map.entry(entry.getKey(), new UpstreamChannelPool(clientBootstrap, entry.getValue())))
			.collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Bootstrap initializeClientBootstrap() {
		return new Bootstrap()
			.channel(KQueueSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>(){
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					ChannelPipeline pipeline = channel.pipeline();
					pipeline.addLast(new LoggingHandler(LogLevel.DEBUG)); // 속도를 위해 추후 제거할 핸들러
					pipeline.addLast("http-response-decoder-inbound",new HttpResponseDecoder());
					pipeline.addLast("http-message-aggregator-inbound",new HttpObjectAggregator(1048576));
					pipeline.addLast(new HttpRequestEncoder());

				}
			})
			.group(workers);
	}
}
