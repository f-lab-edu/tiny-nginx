package channelpool;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class UpstreamChannelProviderTest {
	private static final EventLoopGroup dummyGroup = new DefaultEventLoopGroup(5);
	@Test
	public void testAquireChannel() {
		 LocalAddress FIRST_SERVER_ADDRESS = new LocalAddress("first.id");
		 LocalAddress SECOND_SERVER_ADDRESS = new LocalAddress("second.id");

		 ServerBootstrap serverBootstrap = new ServerBootstrap()
		 	.group(dummyGroup)
		 	.channel(LocalServerChannel.class)
		 	.childHandler(new LoggingHandler(LogLevel.DEBUG));

		 ChannelFuture secondServer = serverBootstrap.clone().bind(SECOND_SERVER_ADDRESS);
		 secondServer.awaitUninterruptibly();

		 Bootstrap bootstrap = new Bootstrap()
				 .group(dummyGroup)
				 .channel(LocalChannel.class)
				 .handler(new LoggingHandler(LogLevel.DEBUG));

		 UpstreamChannelPool upstreamChannelPool = new UpstreamChannelPool(bootstrap,
		 	List.of(FIRST_SERVER_ADDRESS, SECOND_SERVER_ADDRESS));

		 ChannelProxyPromise<UpstreamChannel> channelProxyPromise = upstreamChannelPool.allocateChannel();
		 UpstreamChannel upstreamChannel = channelProxyPromise.promise.awaitUninterruptibly().getNow();

		 Assertions.assertTrue(upstreamChannel.isActive());
		 Assertions.assertEquals(upstreamChannel.serverAddress(), SECOND_SERVER_ADDRESS);
	}
}