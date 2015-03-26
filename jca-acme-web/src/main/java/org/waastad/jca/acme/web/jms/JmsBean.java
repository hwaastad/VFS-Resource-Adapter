package org.waastad.jca.acme.web.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.provider.sftp.SftpFileObject;
import org.waastad.jca.acme.api.AcmeMessageListener;

@MessageDriven(messageListenerInterface = AcmeMessageListener.class,
        activationConfig = {
            @ActivationConfigProperty(propertyName = "hostName", propertyValue = "10.215.128.47"),
            @ActivationConfigProperty(propertyName = "username", propertyValue = "cq_17"),
            @ActivationConfigProperty(propertyName = "password", propertyValue = "abc123"),
            @ActivationConfigProperty(propertyName = "path", propertyValue = "incoming")})
public class JmsBean implements AcmeMessageListener {

    private static final Logger log = Logger.getLogger(JmsBean.class.getName());

    @Override
    public void onMessage(String msg) {
        log.log(Level.INFO, "JmsBean2:Got info about file: {0}", msg);
    }

}
