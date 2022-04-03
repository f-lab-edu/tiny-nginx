package channelpool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpstreamChannelPool {

	private static Map<String, UpstreamChannels> upstreamChannels = new ConcurrentHashMap<>();

	public ChannelProxyPromise<UpstreamChannel> provide(String key) {
		return null;
	}
}
