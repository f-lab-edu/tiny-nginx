package channelpool;

import java.net.SocketAddress;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;

public class UpstreamChannelProvider {
	private final RoundRobinLoadBalancer roundRobinLoadBalancer;
	private final UpstreamChannels upstreamChannels;
	private final EventLoopGroup eventLoopGroup;

	public UpstreamChannelProvider(List<SocketAddress> upstreams, EventLoopGroup eventLoopGroup, Bootstrap bootstrap) {
		this.roundRobinLoadBalancer = new RoundRobinLoadBalancer(upstreams);
		this.eventLoopGroup = eventLoopGroup;
		this.upstreamChannels = new UpstreamChannels(eventLoopGroup, bootstrap);
	}

	public ChannelProxyPromise<UpstreamChannel> acquireNextChannel() {
		return acquireNextChannel(createNewPromise());
	}

	public ChannelProxyPromise<UpstreamChannel> acquireNextChannel(
		ChannelProxyPromise<UpstreamChannel> clientPromise) {
		clientPromise.retryIf(future -> future.cause() != null, clientRetryPromise -> clientRetryPromise
				.supply(() -> upstreamChannels.acquireChannel(roundRobinLoadBalancer.acquireNextAddress()))
				.whenIf(channelPromise -> channelPromise.isSuccess(),
					channel -> clientPromise.trySuccessUncompletedPromise(channel))
			, retryPromise -> clientPromise.tryFailureUncompletedPromise(new IllegalStateException()));
		return clientPromise;
	}

	private ChannelProxyPromise<UpstreamChannel> createNewPromise() {
		EventExecutor eventExecutor = eventLoopGroup.next();
		return new ChannelProxyPromise<>(eventExecutor, eventExecutor.newPromise(), null, null);
	}
}
