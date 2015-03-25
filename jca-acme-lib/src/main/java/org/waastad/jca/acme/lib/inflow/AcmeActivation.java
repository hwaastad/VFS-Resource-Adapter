package org.waastad.jca.acme.lib.inflow;

import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import org.waastad.jca.acme.lib.adapter.AcmeResourceAdapter;

/**
 * AcmeActivation
 *
 * @version $Revision: $
 */
public class AcmeActivation {

    private static final Logger log = Logger.getLogger(AcmeActivation.class.getName());

    /**
     * The resource adapter
     */
    private AcmeResourceAdapter ra;

    /**
     * Activation spec
     */
    private AcmeActivationSpec spec;

    /**
     * The message endpoint factory
     */
    private MessageEndpointFactory endpointFactory;

    /**
     * Default constructor
     *
     * @exception ResourceException Thrown if an error occurs
     */
    public AcmeActivation() throws ResourceException {
        this(null, null, null);
    }

    /**
     * Constructor
     *
     * @param ra AcmeResourceAdapter
     * @param endpointFactory MessageEndpointFactory
     * @param spec AcmeActivationSpec
     * @exception ResourceException Thrown if an error occurs
     */
    public AcmeActivation(AcmeResourceAdapter ra,
            MessageEndpointFactory endpointFactory,
            AcmeActivationSpec spec) throws ResourceException {
        this.ra = ra;
        this.endpointFactory = endpointFactory;
        this.spec = spec;
    }

    /**
     * Get activation spec class
     *
     * @return Activation spec
     */
    public AcmeActivationSpec getActivationSpec() {
        return spec;
    }

    /**
     * Get message endpoint factory
     *
     * @return Message endpoint factory
     */
    public MessageEndpointFactory getMessageEndpointFactory() {
        return endpointFactory;
    }

    /**
     * Start the activation
     *
     * @throws ResourceException Thrown if an error occurs
     */
    public void start() throws ResourceException {
        log.info("Start()");
    }

    /**
     * Stop the activation
     */
    public void stop() {

    }

}
