package channelpool;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpstreamChannels {
	private final Map<SocketAddress, UpstreamChannel> upstreamChannels;
	private final EventLoopGroup eventLoopGroup;
	private Bootstrap bootstrap;

	public UpstreamChannels(EventLoopGroup eventLoopGroup, Bootstrap bootstrap) {
		this.upstreamChannels = new ConcurrentHashMap<>();
		this.eventLoopGroup = eventLoopGroup;
		this.bootstrap = bootstrap;
	}

	public ChannelProxyPromise<UpstreamChannel> acquireChannel(SocketAddress serverHostAndPort) {
		UpstreamChannel upstreamChannel =  upstreamChannels.compute(serverHostAndPort, ((socketAddress, channel) -> {
			if (channel == null) {
				return new UpstreamChannel(serverHostAndPort, eventLoopGroup, bootstrap);
			}

			return channel;
		}));
		return upstreamChannel.createPromise();
	}
}
