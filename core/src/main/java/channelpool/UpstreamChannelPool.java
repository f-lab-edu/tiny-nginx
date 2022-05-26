package channelpool;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpstreamChannelPool {
	private final RoundRobinLoadBalancer roundRobinLoadBalancer;
	private final Map<SocketAddress, UpstreamChannel> upstreamChannels;
	private final List<? extends SocketAddress> upstreamAddresses;
	private final Bootstrap clientBootstrap;

	public UpstreamChannelPool(Bootstrap clientBootstrap, List<? extends SocketAddress> upstreamAddresses) {
		this.clientBootstrap = clientBootstrap;
		this.upstreamAddresses = upstreamAddresses;
		this.roundRobinLoadBalancer = new RoundRobinLoadBalancer();
		this.upstreamChannels = new ConcurrentHashMap<>();
	}

	public ChannelProxyPromise<UpstreamChannel> allocateChannel() {
		return allocateChannel(createNewPromise());
	}

	private ChannelProxyPromise<UpstreamChannel> allocateChannel(ChannelProxyPromise<UpstreamChannel> clientPromise) {
		clientPromise.retryIf(future -> future.cause() != null, clientRetryPromise -> clientRetryPromise
				.supply(() -> findChannel())
				.whenIf(channelPromise -> channelPromise.isSuccess(),
					channel -> clientPromise.trySuccess(channel))
			, retryPromise -> clientPromise.tryFailure(new IllegalStateException()));
		return clientPromise;
	}

	private ChannelProxyPromise<UpstreamChannel> findChannel() {
		SocketAddress serverHostAndPort = roundRobinLoadBalancer.allocateAddress(upstreamAddresses);
		UpstreamChannel upstreamChannel =  upstreamChannels.compute(serverHostAndPort, ((socketAddress, channel) -> {
			if (channel == null) {
				return new UpstreamChannel(clientBootstrap, serverHostAndPort);
			}
			return channel;
		}));
		return upstreamChannel.tryConnect();
	}

	private ChannelProxyPromise<UpstreamChannel> createNewPromise() {
		EventLoopGroup eventLoopGroup = clientBootstrap.config().group();
		EventExecutor eventExecutor = eventLoopGroup.next();
		return new ChannelProxyPromise<>(eventExecutor, eventExecutor.newPromise(), null, null);
	}
}
