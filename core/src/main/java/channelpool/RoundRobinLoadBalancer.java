package channelpool;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer {
	private final AtomicInteger index = new AtomicInteger();
	private final List<SocketAddress> upstreams;

	public RoundRobinLoadBalancer(List<SocketAddress> upstreams) {
		this.upstreams = upstreams;
	}

	public SocketAddress acquireNextAddress() {
		return upstreams.get(index.getAndIncrement() & upstreams.size() - 1);
	}
}
