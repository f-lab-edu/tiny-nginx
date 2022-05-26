package channelpool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpHeaders;

public class UrlBasedChannelResolver {
    private final Map<String, UpstreamChannelPool> upstreamChannels;

    public UrlBasedChannelResolver(Map<String, UpstreamChannelPool> upstreamChannels) {
        this.upstreamChannels = upstreamChannels;
    }

    public ChannelProxyPromise<UpstreamChannel> resolve() {
        UpstreamChannelPool upstreamChannelPool = upstreamChannels.get("/");
        return upstreamChannelPool.allocateChannel();
    }

    public ChannelProxyPromise<UpstreamChannel> allocateChannel(HttpHeaders httpHeaders) {
        return null;
    }
}
