import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSHelper {
	
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 3700;
	
	private static final String JMS_CONNECTION_FACTORY = "jms/JPoker24GameConnectionFactory";
	private static final String JMS_QUEUE = "jms/JPoker24GameQueue";
	private static final String JMS_TOPIC = "jms/JPoker24GameTopic";
	
	private Context jndiContext;
	private ConnectionFactory connectionFactory;
	private Connection connection;
	
	private Session session;
	private Queue queue;
	private Topic topic;

	public JMSHelper() throws NamingException, JMSException {
		this(DEFAULT_HOST);
	}
	public JMSHelper(String host) throws NamingException, JMSException {
		int port = DEFAULT_PORT;
		System.setProperty("org.omg.CORBA.ORBInitialHost", host);
		System.setProperty("org.omg.CORBA.ORBInitialPort", ""+port);
		
		//NamingException
		try {
			jndiContext = new InitialContext();
		} catch (NamingException e){
			System.out.println("JMSHelper error:"+e);
			throw e;
		}
		connectionFactory = (ConnectionFactory)jndiContext.lookup(JMS_CONNECTION_FACTORY);
		queue = (Queue)jndiContext.lookup(JMS_QUEUE);
		topic = (Topic)jndiContext.lookup(JMS_TOPIC);
			
		//JMSException
		connection = connectionFactory.createConnection();
		connection.start();
	}
	public Session createSession() throws JMSException {
		if(session != null) {
			return session;
		} else {
			return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		}
	}
	public ObjectMessage createMessage(Serializable obj) throws JMSException {
		return createSession().createObjectMessage(obj);
	}
	public MessageProducer createQueueSender() throws JMSException {
		return createSession().createProducer(queue);
	}
	public MessageConsumer createQueueReader() throws JMSException {
		return createSession().createConsumer(queue);
	}
	public MessageProducer createTopicSender() throws JMSException {
		return createSession().createProducer(topic);
	}
	public MessageConsumer createTopicReader() throws JMSException {
		return createSession().createConsumer(topic);
	}
	
}

