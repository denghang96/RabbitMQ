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

public class DirectCustomer {

	public static void main( String[] args ) throws IOException, TimeoutException{
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
        String queueName="queue-king";
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定，将路由键与交换器绑定
        String routeKey ="king";
        channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,routeKey);
        System.out.println("waiting for message ......");
        
        //定义一个真正的消费者来订阅消息
        final Consumer consumer = new DefaultConsumer(channel){
        	@Override
        	public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        		String msg = new String(bytes, "UTF-8");
                System.out.println("Received【"+envelope.getRoutingKey()+"】"+msg);
            }
        };
        //消费者正式在指定 的队列上消费
        channel.basicConsume(queueName, true, consumer);
	}
}
