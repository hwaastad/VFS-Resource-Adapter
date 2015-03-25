package org.waastad.jca.acme.web.jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import org.apache.commons.vfs2.FileObject;
import org.waastad.jca.acme.api.AcmeMessageListener;

@MessageDriven(messageListenerInterface = AcmeMessageListener.class,
        activationConfig = {
            @ActivationConfigProperty(propertyName = "hostName", propertyValue = "10.215.128.47"),
            @ActivationConfigProperty(propertyName = "username", propertyValue = "cq_17"),
            @ActivationConfigProperty(propertyName = "password", propertyValue = "abc123"),
            @ActivationConfigProperty(propertyName = "path", propertyValue = "test")})
public class JmsBean2 implements AcmeMessageListener {

    @Override
    public void onMessage(Object msg) {
        System.out.println("JmsBean2: Got Message");
        if (msg instanceof FileObject) {
            FileObject file = (FileObject) msg;
            System.out.println("JmsBean2:Got info about file: " + file.getName().toString());
        }
    }

}
