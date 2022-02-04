import java.io.File;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProxyBootstrap {
	private static final Logger logger = LoggerFactory.getLogger(ProxyBootstrap.class);

	public void start() {
		EventLoopGroup acceptor = new NioEventLoopGroup(1);
		EventLoopGroup worker = new NioEventLoopGroup(4);
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(acceptor, worker)
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
						pipeline.addLast(new SimpleContentsHandler());
					}
				});

			ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
			channelFuture.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			acceptor.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	public ProxyBootstrapConfig configure() {
		String configuationPath = System.getProperty("bootstrap.config.path", "bootstrap.yml");
		try {
			ImmutableHierarchicalConfiguration configuration = new FileBasedConfigurationBuilder<>(
				YAMLConfiguration.class)
				.configure(new Parameters()
					.fileBased()
					.setFile(new File(configuationPath)))
				.getConfiguration();

			return new ProxyBootstrapConfig.Builder()
				.setWorkerCount(configuration.get(Integer.class, "worker.count"))
				.build();

		} catch (ConfigurationException exception) {
			throw new IllegalArgumentException(String.format("couldn't load configuration from %s", configuationPath),
				exception);
		}
	}
}
