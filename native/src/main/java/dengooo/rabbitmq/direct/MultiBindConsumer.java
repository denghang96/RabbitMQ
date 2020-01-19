package dengooo.rabbitmq.direct;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
/**
 * 一个队列绑定多个路由键
 * @author denghang
 *
 */
public class MultiBindConsumer {
	public static void main(String[] args) throws IOException, TimeoutException {
		//1.创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //2.设置主机
        connectionFactory.setHost("192.168.0.108");
        connectionFactory.setVirtualHost("dengooo");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("123456");
        //3.创建连接
        Connection connection = connectionFactory.newConnection();
        //4.创建信道
        Channel channel = connection.createChannel();
        //设置交换器.此处设置为 直接交换器
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        
        String[] routekeys={"king","mark","james"};
        for(String routekey:routekeys){
            channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,
                    routekey);
        }
        System.out.println(" [*] Waiting for messages:");
        
        // 创建队列消费者
        final Consumer consumerA = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties
                                               properties,
                                       byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received "
                        + envelope.getRoutingKey() + ":'" + message
                        + "'");
            }
        };
        channel.basicConsume(queueName, true, consumerA);
	}
}
