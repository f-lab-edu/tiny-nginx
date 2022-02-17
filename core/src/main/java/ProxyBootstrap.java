import java.io.File;

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
import utils.YamlUtil;

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

	public void readProperty(){
		String configuationPath = System.getProperty("bootstrap.config.path", "src/main/resources/bootstrap.yml");
	}

	public ProxyBootstrapConfig configure(String configuationPath) {
		File fileHandler = new File(configuationPath);

		if(!fileHandler.isFile() && !fileHandler.exists()){
			throw new IllegalArgumentException(String.format("You can not load configuration file from : %s", configuationPath));
		}

		return YamlUtil.toObject(fileHandler, ProxyBootstrapConfig.class);
	}
}
