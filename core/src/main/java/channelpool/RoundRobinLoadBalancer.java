package channelpool;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer {
	private final AtomicInteger index = new AtomicInteger();

	public SocketAddress allocateAddress(List<? extends SocketAddress> upstreams) {
		return upstreams.get(index.getAndIncrement() & upstreams.size() - 1);
	}
}
