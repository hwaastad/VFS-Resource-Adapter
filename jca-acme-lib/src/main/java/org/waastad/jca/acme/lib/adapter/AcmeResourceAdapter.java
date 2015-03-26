package org.waastad.jca.acme.lib.adapter;

import java.io.Serializable;
import java.lang.reflect.Method;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

import javax.transaction.xa.XAResource;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.waastad.jca.acme.api.AcmeMessageListener;
import org.waastad.jca.acme.lib.inflow.AcmeActivation;
import org.waastad.jca.acme.lib.inflow.AcmeActivationSpec;
import org.waastad.jca.acme.lib.listener.VFSWatchingThread;
import org.waastad.jca.acme.lib.listener.VFSWorker;

/**
 * AcmeResourceAdapter
 *
 * @version $Revision: $
 */
@Connector(displayName = "VFSFileSystemRA",
        vendorName = "Test Vendor",
        version = "1.0")
public class AcmeResourceAdapter implements ResourceAdapter, Serializable {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The logger
     */
    private static final Logger log = Logger.getLogger(AcmeResourceAdapter.class.getName());

    /**
     * The activations by activation spec
     */
    private final ConcurrentHashMap<AcmeActivationSpec, AcmeActivation> activations;
    private final ConcurrentHashMap<FileObject, AcmeActivationSpec> fileActivations;
    private final ConcurrentHashMap<AcmeActivationSpec, MessageEndpointFactory> factoryMap = new ConcurrentHashMap<>();
    private BootstrapContext bootstrapContext = null;
    Method messageListenerMethod = null;
    
    private DefaultFileMonitor fileMonitor;
    private TestRunnableListener fileListener;
    private WorkManager workManager;
    
    WorkManager getWorkManager() {
        return this.bootstrapContext.getWorkManager();
    }

    /**
     * Default constructor
     */
    public AcmeResourceAdapter() {
        this.activations = new ConcurrentHashMap<>();
        this.fileActivations = new ConcurrentHashMap<>();
    }

    /**
     * This is called during the activation of a message endpoint.
     *
     * @param endpointFactory A message endpoint factory instance.
     * @param spec An activation spec JavaBean instance.
     * @throws ResourceException generic exception
     */
    @Override
    public void endpointActivation(final MessageEndpointFactory endpointFactory,
            ActivationSpec spec) throws ResourceException {
        
        if (!(spec instanceof AcmeActivationSpec)) {
            throw new ResourceException("invalid activation spec type");
        }
        AcmeActivationSpec activeSpec = (AcmeActivationSpec) spec;
        activeSpec.validate();
        log.info(String.format("endpointActivation(%s)", activeSpec.toString()));
        try {
            FileObject fileObject = activeSpec.getFileObject(getClass().getClassLoader());
            synchronized (this.fileActivations) {
                fileActivations.put(fileObject, activeSpec);
            }
            
            Method endpointMethod
                    = AcmeMessageListener.class.getMethod("onMessage", new Class[]{String.class});
            activeSpec.setMessageEndpoint(endpointFactory, endpointMethod);
            log.info(String.format("Got object: %s", fileObject.getName().toString()));
            log.info(String.format("Classloader: %s", fileMonitor.getClass().getClassLoader().toString()));
            fileMonitor.addFile(fileObject);
        } catch (FileSystemException | NoSuchMethodException ex) {
            Logger.getLogger(AcmeResourceAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This is called when a message endpoint is deactivated.
     *
     * @param endpointFactory A message endpoint factory instance.
     * @param spec An activation spec JavaBean instance.
     */
    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory,
            ActivationSpec spec) {
        log.info("endpointDeactivation()");
        try {
            AcmeActivationSpec activeSpec = (AcmeActivationSpec) spec;
            synchronized (this.factoryMap) {
                this.factoryMap.remove(activeSpec);
            }
            synchronized (this.fileActivations) {
                fileActivations.put(activeSpec.getFileObject(getClass().getClassLoader()), activeSpec);
            }
        } catch (FileSystemException e) {
            log.severe(e.getMessage());
        }
        
    }
    
    @Override
    public void start(BootstrapContext ctx)
            throws ResourceAdapterInternalException {
        try {
            log.info("start()");
            this.bootstrapContext = ctx;
            fileListener = new TestRunnableListener();
            fileMonitor = new DefaultFileMonitor(fileListener);
            VFSWorker worker = new VFSWorker(fileMonitor);
            getWorkManager().doWork(worker);
//            new VFSWatchingThread(fileMonitor, this).start();
        } catch (WorkException e) {
            log.severe(String.format("Error: %s", e.getMessage()));
        }
        
    }
    
    @Override
    public void stop() {
        log.info("stop()");
        for (AcmeActivation active : activations.values()) {
            active.stop();
        }
    }
    
    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs)
            throws ResourceException {
        log.info("getXAResources()");
        return null;
    }
    
    Method getMessageEndpointMethod() {
        return this.messageListenerMethod;
    }
    
    class TestRunnableListener implements FileListener {
        
        @Override
        public void fileCreated(FileChangeEvent fce) throws Exception {
            FileObject parent = fce.getFile().getParent();
            AcmeActivationSpec get = fileActivations.get(parent);
            if (get == null) {
                log.severe("Could not find any spec for this...");
            } else {
                get.sendMessage(fce.getFile().getName().toString());
            }
        }
        
        @Override
        public void fileDeleted(FileChangeEvent fce) throws Exception {
            FileObject parent = fce.getFile().getParent();
            AcmeActivationSpec get = fileActivations.get(parent);
            if (get == null) {
                log.severe("Could not find any spec for this...");
            } else {
                get.sendMessage(fce.getFile().getName().toString());
            }
        }
        
        @Override
        public void fileChanged(FileChangeEvent fce) throws Exception {
            FileObject parent = fce.getFile().getParent();
            AcmeActivationSpec get = fileActivations.get(parent);
            if (get == null) {
                log.severe("Could not find any spec for this...");
            } else {
                get.sendMessage(fce.getFile().getName().toString());
            }
        }
        
    }
    
}
