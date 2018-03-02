package com.grgbanking.gaps.core.adapter;

import com.grgbanking.gaps.core.adapter.codec.LengthFieldFormat;
import com.grgbanking.gaps.core.adapter.codec.SimpleLengthFieldBasedFrameDecoder;
import com.grgbanking.gaps.core.adapter.codec.SimpleLengthFieldPrepender;
import com.grgbanking.gaps.core.adapter.handler.SimpleInletHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class TCPInletAdapter {

	private int port;
	private int numberOfHandlerThreads;
	private SimpleInletHandler simpleInletHandler = new SimpleInletHandler();

	public TCPInletAdapter(int port) {
		this.port = port;
		this.numberOfHandlerThreads = 16;
	}
	
	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		final EventExecutorGroup handlerGroup = new DefaultEventExecutorGroup(numberOfHandlerThreads);
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast("decoder", new SimpleLengthFieldBasedFrameDecoder(LengthFieldFormat.BIN, 4096, 0, 2, 0, 2));
					channel.pipeline().addLast("encoder", new SimpleLengthFieldPrepender(LengthFieldFormat.BIN, 2, 0, false));
					channel.pipeline().addLast(handlerGroup, "handler", simpleInletHandler);
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future = bootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	 public static void main(String[] args) throws Exception {
		 int port;
		 if (args.length > 0) {
			 port = Integer.parseInt(args[0]);
		 } else {
			 port = 5678;
		 }
		 new TCPInletAdapter(port).run();
	 }

}
