import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * This class represents the drinks bar on the SelfServiceModel model.
 * This station is a fully self service station, so no employees are responsible for this station.
 */
public class DrinksBarProcess extends SimProcess{

    // a reference to the model this process is part of
    private SelfServiceModel myModel;

    /**
     * Constructor of the DrinksBarProcess
     * @param owner         the model this process belongs to
     * @param name          this drink bar's name
     * @param showInTrace   flag to indicate if this process shall produce output for the trace
     */
    public DrinksBarProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        myModel = (SelfServiceModel) owner;
    }

    /**
     * Describes the drink bar's life cycle.
     * It will continually loop through the following steps
     * If there is a costumer waiting
     *      1) remove the customer from the queue
     *      2) serve the costumer
     *      3) reactivate the costumer
     * if no costumer is waiting at the drinks bar
     *      1) insert into the idleDrinksBarQueue and
     *      2) wait (passivate) until someone arrives
     * @throws SuspendExecution
     */
    @Override
    public void lifeCycle() throws SuspendExecution {
        while (true) {
            if (myModel.customerDrinksBarQueue.isEmpty()) {
                myModel.idleDrinksBarQueue.insert(this);
                passivate();
            } else {
                CustomerProcess customer = myModel.customerDrinksBarQueue.first();
                myModel.customerDrinksBarQueue.remove(customer);

                hold(new TimeSpan(myModel.getServiceTimeDrinksBar()));
                customer.activate(new TimeSpan(0.0));
            }
        }
    }
}
