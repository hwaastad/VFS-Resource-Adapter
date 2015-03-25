package org.waastad.jca.acme.lib.listener;

import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.waastad.jca.acme.lib.adapter.AcmeResourceAdapter;

public class VFSWatchingThread extends Thread {

    private final DefaultFileMonitor fileMonitor;

    public VFSWatchingThread(DefaultFileMonitor fileMonitor, AcmeResourceAdapter resourceAdapter) {
        this.fileMonitor = fileMonitor;
    }

    @Override
    public void run() {
        fileMonitor.setRecursive(false);
        fileMonitor.setDelay(5000);
        fileMonitor.start();
    }

}
