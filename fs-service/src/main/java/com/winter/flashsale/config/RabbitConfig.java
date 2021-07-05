package com.winter.flashsale.config;

import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String FLASHSALE_ORDER_QUEUE = "stock.flashsale.order.queue";

    public static final String TOPIC_ = "order.flashsale.";
    public static final String ORDER_KEY = "order";
    public static final String ALL_TOPIC_KEY = "*";

    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";

    /**
     * Topic
     */
    @Bean
    public Queue topicQueue1() {
        return new Queue(FLASHSALE_ORDER_QUEUE, true);
    }
    @Bean
    public TopicExchange topicExc() {
        return new TopicExchange(ORDER_EVENT_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExc()).with(TOPIC_ + ORDER_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory factory){
//        log.info("caching factory: {}", factory.getChannelCacheSize());
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
//        rabbitTemplate.setConfirmCallback(rabbitConfirmCallback);

        /*
         * 当mandatory标志位设置为true时
         * 如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息
         * 那么broker会调用basic.return方法将消息返还给生产者
         * 当mandatory设置为false时，出现上述情况broker会直接将消息丢弃
         */
//        rabbitTemplate.setMandatory(true);
//        rabbitTemplate.setReturnCallback(rabbitReturnCallback);
        //使用单独的发送连接，避免生产者由于各种原因阻塞而导致消费者同样阻塞
        rabbitTemplate.setUsePublisherConnection(true);

        return rabbitTemplate;
    }
}
