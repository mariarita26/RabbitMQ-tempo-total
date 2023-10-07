import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class Producer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
        ) {
            String msg = "";
            String QUEUE_NAME = "TESTE";
            boolean durable = false;
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

            for (int i = 0; i < 1000001; i++) {
                msg = i + "-" + System.currentTimeMillis();
                channel.basicPublish("", QUEUE_NAME, false, false, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                System.out.println("Mensagem enviada: " + msg);

            }
        }
    }
}


