import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.*;

/**
 * This class represents a process source, which continually generates
 * customers in order to keep the simulation running.
 */
public class CustomerGenerator extends SimProcess {

    /**
     * Constructor of the CustomerGenerator
     * @param owner         the model this costumer generator belongs to
     * @param name          this costumer generator's name
     * @param showInTrace   flag to indicate if this process shall produce output for the trace
     */
    public CustomerGenerator(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }

    /**
     * describes this process's life cycle: continually generate new costumers.
     * @throws SuspendExecution
     */
    public void lifeCycle() throws SuspendExecution {
        // get a reference to the model
        SelfServiceModel model = (SelfServiceModel)getModel();

        while (true) {
            // create a new customer
            // Parameters:
            // model            = it's part of this model
            // "Customer"       = name of the object
            // true             = show in trace file?
            CustomerProcess customer = new CustomerProcess(model, "Customer", true);

            customer.activateAfter(this);

            hold(new TimeSpan(model.getCustomerArrivalTime()));
        }
    }
}
