package dengooo.rabbitmq.direct;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 直接交换器。生产者
 * @author denghang
 *
 */
public class DirectProducer {

	public final static String EXCHANGE_NAME = "direct_logs";
	
	public static void main( String[] args ) throws IOException, TimeoutException{
		//1.创建连接工厂
        ConnectionFactory connectionFactory= new ConnectionFactory();
        //2.设置主机
        connectionFactory.setHost("192.168.0.108");

        connectionFactory.setVirtualHost("dengooo");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("123456");
        //3.创建连接
        Connection connection =connectionFactory.newConnection();
        //4.创建信道
        Channel channel =connection.createChannel();
        
        //设置交换器.此处设置为 直接交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        
        //声明路由键
        String[] routeKeys = {"king","mark","james"};
        for(int i = 0; i < 3; i++) {
        	//路由键
        	String routeKey = routeKeys[i%3];
        	//消息体
        	String message = "Hello RabbitMQ" + (i + 1);
        	//发布消息
        	channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes());
        	System.out.println("Sent:"  + routeKey + ":" + message);
        }
        channel.close();
        connection.close();
    }
}
