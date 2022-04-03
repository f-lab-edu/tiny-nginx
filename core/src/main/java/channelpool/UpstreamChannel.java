package channelpool;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Promise;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class UpstreamChannel {
	private static final AtomicReferenceFieldUpdater<UpstreamChannel, Object> STATE_UPDATER =
		AtomicReferenceFieldUpdater.newUpdater(UpstreamChannel.class, Object.class, "state");

	private SocketAddress serverHostAndPort;
	private Channel channel;
	private EventLoopGroup eventLoopGroup;
	private EventLoop channelEventLoop;
	private Bootstrap bootstrap;
	private volatile Object state;
	private AtomicInteger connectionSuccessCount;
	private AtomicInteger connectionFailureCount;

	private static final Object PENDING = new Object();
	private static final Object ACTIVE = new Object();

	public UpstreamChannel(SocketAddress serverHostAndPort, EventLoopGroup eventLoopGroup, Bootstrap bootstrap) {
		this.eventLoopGroup = eventLoopGroup;
		this.channelEventLoop = eventLoopGroup.next();
		this.serverHostAndPort = serverHostAndPort;
		this.bootstrap = bootstrap.clone().remoteAddress(serverHostAndPort)
			.group(channelEventLoop);
	}

	public boolean isActive() {
		return this.channel != null && this.channel.isActive();
	}

	public boolean hasConnected() {
		if (STATE_UPDATER.compareAndSet(this, null, ACTIVE)) {
			return false;
		}
		return true;
	}

	public ChannelProxyPromise<UpstreamChannel> createPromise() {
		ChannelProxyPromise<UpstreamChannel> channelProxyPromise = createNewPromise();
		allocateChannel(channelProxyPromise);
		return channelProxyPromise;
	}

	public SocketAddress serverAddress() {
		return serverHostAndPort;
	}

	private void allocateChannel(ChannelProxyPromise<UpstreamChannel> channelProxyPromise) {
		channelProxyPromise
			.mapIfNot(channel -> channel.hasConnected(), channel -> channel.tryConnecting())
			.applyAsync(applyPromise -> applyPromise
				.then(channel -> channel.isActive(),
					(channel, promise) -> promise.trySuccessUncompletedPromise(channel))
				.applyIfNot(promise -> promise.hasExcetion() || promise.isCompleted(),
					promise -> promise.tryCancelUncompletedPromise())
				.throwIf(promise -> promise.hasExcetion(),
					(cause, promise) -> promise.tryFailureUncompletedPromise(cause)));
	}

	private void tryConnecting() {
		ChannelFuture channelFuture = bootstrap.clone().connect();
		this.channel = channelFuture.channel();
	}

	private ChannelProxyPromise<UpstreamChannel> createNewPromise() {
		Promise<UpstreamChannel> promise = this.eventLoopGroup
			.next()
			.newPromise();
		return new ChannelProxyPromise<>(channelEventLoop, promise, this, null);
	}
}
