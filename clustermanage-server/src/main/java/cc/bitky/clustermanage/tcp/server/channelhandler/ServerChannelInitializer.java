package cc.bitky.clustermanage.tcp.server.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Service
public class ServerChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private final CanFrameChannelInboundHandler canFrameChannelInboundHandler;
    private final ParsedMessageInBoundHandler parsedMessageInBoundHandler;
    private final WebMsgOutBoundHandler webMsgOutBoundHandler;
    private final SendingOutBoundHandler sendingOutBoundHandler;

    //   private ChannelPipeline pipeline;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ServerChannelInitializer(CanFrameChannelInboundHandler canFrameChannelInboundHandler,
                                    ParsedMessageInBoundHandler parsedMessageInBoundHandler,
                                    WebMsgOutBoundHandler webMsgOutBoundHandler,
                                    SendingOutBoundHandler sendingOutBoundHandler) {
        super();
        this.canFrameChannelInboundHandler = canFrameChannelInboundHandler;
        this.parsedMessageInBoundHandler = parsedMessageInBoundHandler;
        this.webMsgOutBoundHandler = webMsgOutBoundHandler;
        this.sendingOutBoundHandler = sendingOutBoundHandler;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler("kyOutlineLogger", LogLevel.INFO));
        ch.pipeline().addLast(canFrameChannelInboundHandler);
        ch.pipeline().addLast(parsedMessageInBoundHandler);
        ch.pipeline().addLast(sendingOutBoundHandler);
        ch.pipeline().addLast(webMsgOutBoundHandler);

        parsedMessageInBoundHandler.getServerTcpMessageHandler()
                .setSendWebMessagesListener(iMessages -> {
                            ch.pipeline().write(iMessages);
                            return true;
                        }
                );
    }  //     logger.warn("Netty 模块未初始化，无通道可使用");
}