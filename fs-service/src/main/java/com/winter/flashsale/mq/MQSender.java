package com.winter.flashsale.mq;

import com.winter.flashsale.mq.msg.FlashSaleMessage;
import com.winter.flashsale.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static final Logger log = LoggerFactory.getLogger(MQSender.class);

    AmqpTemplate amqpTemplate;
    RabbitTemplate rabbitTemplate;

    @Autowired
    public MQSender(AmqpTemplate amqpTemplate, RabbitTemplate rabbitTemplate) {
        this.amqpTemplate = amqpTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendFlashSaleMessage(FlashSaleMessage msg) {
        String strMsg = StringUtils.bean2String(msg);

        log.info("send message: " + strMsg);
        amqpTemplate.convertAndSend(MQConfig.FLASHSALE_QUEUE, strMsg);
    }

}
