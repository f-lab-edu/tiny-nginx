package proxy.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForwardToCleintHandler extends ChannelInboundHandlerAdapter {
	private Channel clientChannel;
	public ForwardToCleintHandler(Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {

		// forward message to client channel
		if(message instanceof HttpResponse) {
			log.info("forward message to client channel", message);
			clientChannel.writeAndFlush(message).addListener(future -> {
				if(future.isSuccess()) {
					clientChannel.close();
					//remove handler itself from pipeline
					context.pipeline().remove(this);
				}
				else {
					clientChannel.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
				}
			});
		}
	}
}
