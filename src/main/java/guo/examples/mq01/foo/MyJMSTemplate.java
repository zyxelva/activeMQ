package main.java.guo.examples.mq01.foo;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MyJMSTemplate {

  private ConnectionFactory connectionFactory = null;
  private Connection connection = null;
  private Session session = null;

  private Session createSession() {
    // defualt user & password both are null
    String user = ActiveMQConnection.DEFAULT_USER;
    String password = ActiveMQConnection.DEFAULT_PASSWORD;
    // DEFAULT_BROKER_URL =failover://tcp://localhost:61616
    String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    connectionFactory = new ActiveMQConnectionFactory(user, password, url);
    try {
      connection = connectionFactory.createConnection();
      session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
    } catch (JMSException e) {
      e.printStackTrace();
    }

    return session;
  }


  public void excute(ExcutionCallback ec) {
    createSession();
    ec.excute(session);
    close();
  }


  private void close() {

    try {
      session.commit();
      session.close();

      connection.close();
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

}
