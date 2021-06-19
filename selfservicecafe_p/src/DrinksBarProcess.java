import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * This class represents the drinks bar on the SelfServiceModel model.
 */

public class DrinksBarProcess extends SimProcess{
    private SelfServiceModel myModel;

    private int numberOfMachines = 2;


    /**
     * Constructor of the drinks bar process
     * @param owner
     * @param name
     * @param showInTrace
     */
    public DrinksBarProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        myModel = (SelfServiceModel) owner;
    }

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
