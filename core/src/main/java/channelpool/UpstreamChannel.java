package channelpool;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.EventExecutor;
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
	private Bootstrap bootstrap;
	private volatile Object state;
	private AtomicInteger connectionSuccessCount;
	private AtomicInteger connectionFailureCount;

	private static final Object PENDING = new Object();
	private static final Object ACTIVE = new Object();

	public UpstreamChannel(Bootstrap bootstrap, SocketAddress serverHostAndPort) {
		this.bootstrap = bootstrap;
		this.serverHostAndPort = serverHostAndPort;
	}

	public ChannelFuture registerInEventLoop(Channel other){
		return this.channel.eventLoop().register(other);
	}

	public boolean inEventLoop(Channel other){
		return this.channel.eventLoop().equals(other.eventLoop());
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

	public ChannelProxyPromise<UpstreamChannel> tryConnect() {
		ChannelProxyPromise<UpstreamChannel> channelProxyPromise = createNewPromise();
		tryConnect(channelProxyPromise);
		return channelProxyPromise;
	}

	public SocketAddress serverAddress() {
		return serverHostAndPort;
	}

	private void tryConnect(ChannelProxyPromise<UpstreamChannel> channelProxyPromise) {
		channelProxyPromise
			.mapIfNot(channel -> channel.hasConnected(), channel -> channel.tryConnecting())
			.applyAsync(applyPromise -> applyPromise
				.then(channel -> channel.isActive(),
					(channel, promise) -> promise.trySuccess(channel))
				.applyIfNot(promise -> promise.hasExcetion() || promise.isCompleted(),
					promise -> promise.tryCancel())
				.throwIf(promise -> promise.hasExcetion(),
					(cause, promise) -> promise.tryFailure(cause)));
	}

	private void tryConnecting() {
		this.channel = bootstrap
			.connect(serverHostAndPort)
			.channel();
	}

	private ChannelProxyPromise<UpstreamChannel> createNewPromise() {
		Promise<UpstreamChannel> promise = this.bootstrap.config().group()
			.next()
			.newPromise();
		EventExecutor eventExecutor = this.bootstrap.config().group().next();

		return new ChannelProxyPromise<>(eventExecutor, promise, this, null);
	}

	public Channel getChannel() {
		return channel;
	}
}
