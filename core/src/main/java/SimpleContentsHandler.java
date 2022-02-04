import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SimpleContentsHandler extends ChannelInboundHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(SimpleContentsHandler.class);


	private Channel upstreamChannel;

	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {

		// Create a connection for upstream requests
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(context.channel().eventLoop())
			.channel(NioSocketChannel.class)
			.handler(new ChannelInboundHandlerAdapter(){
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
					logger.debug("channelRead() from upstream");
				}
			});
		ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(8888));
		upstreamChannel = channelFuture.channel();
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		logger.debug("channelRead() from client");

		// forward the client request to upstream
		if(upstreamChannel.isActive()){
			upstreamChannel.writeAndFlush(message);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext context) throws Exception {
		context.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
		cause.printStackTrace();
		context.close();
	}
}
