package proxy;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import channelpool.UpstreamChannelPool;
import channelpool.UrlBasedChannelResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestForwardingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private final UrlBasedChannelResolver upstreamChannelPool;

	public RequestForwardingHandler(UrlBasedChannelResolver upstreamChannelPool) {
		this.upstreamChannelPool = upstreamChannelPool;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, FullHttpRequest message) throws Exception {
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, message.content().copy());
		context.writeAndFlush(response);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.close();
	}
}
