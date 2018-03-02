package main.java.guo.examples.mq01.foo;

import javax.jms.Session;

public interface ExcutionCallback {

  public void excute(Session session);


}
