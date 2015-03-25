
package org.waastad.jca.acme.lib.inflow;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Logger;
import javax.resource.ResourceException;

import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.waastad.jca.acme.api.AcmeMessageListener;


@Activation(messageListeners = {AcmeMessageListener.class})
public class AcmeActivationSpec implements ActivationSpec {


    private static final Logger log = Logger.getLogger(AcmeActivationSpec.class.getName());


    private ResourceAdapter ra;

    private Method endpointMethod = null;
    private MessageEndpointFactory mef = null;


    @ConfigProperty(defaultValue = "1.2.3.4")
    private String hostName;

    @ConfigProperty(defaultValue = "username")
    private String username;

    @ConfigProperty(defaultValue = "passord")
    private String password;

    @ConfigProperty(defaultValue = "")
    private String path;

    private FileObject fileObject;

    /**
     * Default constructor
     */
    public AcmeActivationSpec() {

    }


    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileObject getFileObject() throws FileSystemException {
        String url = String.format("sftp://%s:%s@%s/%s", username, password, hostName, path);
        log.info(String.format("Resolving: %s", url));
        return VFS.getManager().resolveFile(url);
    }

    public void setFileObject(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public FileObject getRemoteResolver() throws FileSystemException {
        String url = String.format("sftp://%s:%s@%s/%s", username, password, hostName, path);
        log.info(String.format("Resolving: %s", url));
        return VFS.getManager().resolveFile(url);
    }


    @Override
    public void validate() throws InvalidPropertyException {
        log.finest("validate()");

    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        log.finest("getResourceAdapter()");
        return ra;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) {
        log.finest("setResourceAdapter()");
        this.ra = ra;
    }

    public void setMessageEndpoint(MessageEndpointFactory mef, Method endpointMethod) {
        this.mef = mef;
        this.endpointMethod = endpointMethod;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.fileObject);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AcmeActivationSpec other = (AcmeActivationSpec) obj;
        return Objects.equals(this.fileObject, other.fileObject);
    }

    @Override
    public String toString() {
        return "AcmeActivationSpec{" + "hostName=" + hostName + ", username=" + username + ", password=" + password + ", path=" + path + '}';
    }

    public void sendMessage(Object message) {
        log.info("Sedning message.....");
        try {
            MessageEndpoint endpoint = (MessageEndpoint) mef.createEndpoint(null);
            try {
                endpoint.beforeDelivery(this.endpointMethod);
                ((AcmeMessageListener) endpoint).onMessage(message);
            } catch (ResourceException | NoSuchMethodException e) {
                log.severe(e.getMessage());
            } finally {
                try {
                    endpoint.afterDelivery();
                } catch (ResourceException e) {

                }
            }
        } catch (UnavailableException e) {
            log.severe(e.getMessage());
        }
    }

}
