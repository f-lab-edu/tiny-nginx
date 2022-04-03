package channelpool;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelProxyPromise<C extends UpstreamChannel> extends ProxyPromise<C> {

	public ChannelProxyPromise(EventExecutor eventExecutor, Promise<C> promise, C value, Throwable cause) {
		super(eventExecutor, promise, value, cause);
	}

	@Override
	public boolean isCompleted() {
		return promise.isDone() && promise.isSuccess();
	}
}
