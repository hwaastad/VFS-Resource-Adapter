package org.waastad.jca.acme.lib.listener;

import java.util.logging.Logger;
import javax.resource.spi.work.Work;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

public class VFSWorker implements Work {

    private static final Logger log = Logger.getLogger(VFSWorker.class.getName());

    private final DefaultFileMonitor fileMonitor;

    public VFSWorker(DefaultFileMonitor fileMonitor) {
        this.fileMonitor = fileMonitor;
    }

    @Override
    public void release() {
        log.info("Releasing VFS worker...");
        this.fileMonitor.stop();
    }

    @Override
    public void run() {
        log.info("Starting VFS worker...");
        log.info(String.format("Classloader: %s", fileMonitor.getClass().getClassLoader().toString()));
        fileMonitor.setRecursive(false);
        fileMonitor.setDelay(5000);
        fileMonitor.start();
    }

}
