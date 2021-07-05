package com.winter.flashsale.mq;

import com.winter.common.message.FlashSaleOrderMessage;
import com.winter.common.message.QueueMessage;
import com.winter.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Sender {

    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    private static final String FLASHSALE_ROUTING = RabbitConfig.TOPIC_ + RabbitConfig.ORDER_KEY;

    RabbitTemplate rabbitTemplate;

    @Autowired
    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDirectFlashSaleOrder(FlashSaleOrderMessage msg) {
        String strMsg = StringUtils.bean2String(new QueueMessage<>(msg));

        log.info("send message: " + strMsg);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EVENT_EXCHANGE, FLASHSALE_ROUTING, strMsg);
    }

}
