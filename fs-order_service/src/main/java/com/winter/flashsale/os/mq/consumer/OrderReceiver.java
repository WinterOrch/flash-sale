package com.winter.flashsale.os.mq.consumer;

import com.winter.common.message.FlashSaleOrderMessage;
import com.winter.common.message.QueueMessage;
import com.winter.common.utils.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "stock.flashsale.order.queue")
public class OrderReceiver {

    @RabbitHandler
    public void process(String msg) {
        QueueMessage<FlashSaleOrderMessage> message = StringUtils.string2Bean(msg, QueueMessage.class);
        FlashSaleOrderMessage orderMessage = message.getMessageData();
        // TODO Order process
    }
}
