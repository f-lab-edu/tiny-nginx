package proxy.handler;

import channelpool.UpstreamChannel;
import channelpool.UrlBasedChannelResolver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForwardToServerHandler extends ChannelInboundHandlerAdapter {
	private final UrlBasedChannelResolver urlBasedChannelResolver;

	public ForwardToServerHandler(UrlBasedChannelResolver urlBasedChannelResolver) {
		this.urlBasedChannelResolver = urlBasedChannelResolver;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("클라이언트와 연결을 수락한다. {}", ctx.channel());
		ctx.fireChannelActive();
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		if (message instanceof HttpRequest) {

			FullHttpRequest httpRequest = ((FullHttpRequest)message).copy();
			httpRequest.headers().remove(HttpHeaderNames.CONNECTION);

			//get client channel
			Channel clientChannel = context.channel();

			// get upstream channel from channel pool
			urlBasedChannelResolver.resolve().addListener(upstreamChannelFuture -> {
				if (upstreamChannelFuture.isSuccess()) {
					UpstreamChannel upstreamChannel = upstreamChannelFuture.getNow();

					// forward request to upstream
					upstreamChannel.getChannel().writeAndFlush(httpRequest);

					// forward error response to client channel
					upstreamChannel.getChannel()
						.pipeline()
						.addLast("content-forwarding-handler",new ForwardToCleintHandler(clientChannel));
				}
			});


			// forward response to client channel

			// forward response to client
			// upstreamChannel.channel.pipeline().addAfter("request-forwarding-handler", "response-forwarding-handler", new ResponseForwardingHandler(clientChannel));

			// forward content to upstream
			// clientChannel.pipeline().addAfter("request-forwarding-handler", "content-forwarding-handler", new ContentForwardingHandler(upstreamChannel.channel));

			// forward content to client
			// upstreamChannel.channel.pipeline().addAfter("content-forwarding-handler", "content-forwarding-handler", new ContentForwardingHandler(clientChannel));

			// forward last content to upstream
			// clientChannel.pipeline().addAfter("content-forwarding-handler", "last-content-forwarding-handler", new LastContentForwardingHandler(upstreamChannel.channel));

			// forward last content to client
			// upstreamChannel.channel.pipeline().addAfter("last-content-forwarding-handler", "last-content-forwarding-handler", new LastContentForwardingHandler(clientChannel));

			// forward close to upstream
			// clientChannel.pipeline().addAfter("last-content-forwarding-handler", "close-forwarding-handler", new CloseForwardingHandler(upstreamChannel.channel));

			// forward close to client
			// upstreamChannel.channel.pipeline().addAfter("close-forwarding-handler", "close-forwarding-handler", new CloseForwardingHandler(clientChannel));

			// forward error to upstream	upstreamChannel.writeAndFlush(httpRequest, clientChannel);

			// remove current handler

			// if (upstreamChannel.inEventLoop(clientChannel)) {
			// 	upstreamChannel.writeAndFlush(httpRequest, clientChannel); // event loop 안에 있으면 업스트림에 쓰
			// } else {
			// }

			// 	clientChannel.deregister().addListener(future -> {
			// 		if (future.isSuccess()) {
			// 			upstreamChannel.registerInEventLoop(clientChannel).addListener(registerFuture -> {
			// 				if (registerFuture.isSuccess()) {
			// 					upstreamChannel.writeAndFlush(httpRequest, clientChannel); // 스레드 변경
			// 				}
			// 			});
			// 		} else {
			// 			log.info("deregister fail");
			// 		}
			// 	});
			// }

		}
		context.fireChannelRead(message);
	}

	// clientChannel.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)) // 클라이언트에게 응답 보냄
	// 	.addListener(ChannelFutureListener.CLOSE);

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		log.info("클라이언트로 부터 읽기를 완료한다. {}", ctx.channel());
		ctx.fireChannelReadComplete();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.info("클라이언트와 연결을 끊는다. {}", ctx.channel());
		ctx.fireChannelUnregistered();
	}
}
