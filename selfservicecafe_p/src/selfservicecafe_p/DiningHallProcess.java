package selfservicecafe_p;

import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * This class represents the dining hall on the SelfServiceModel model.
 */
public class DiningHallProcess extends SimProcess{

    // a reference to the model this process is part of
    private SelfServiceModel myModel;

    private boolean mainDish;

    /**
     * Constructor of the dining hall process
     * @param owner         the model this process belongs to
     * @param name          this dining hall's name
     * @param showInTrace   flag to indicate if this process shall produce output for the trace
     */
    public DiningHallProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        myModel = (SelfServiceModel) owner;

        // default: costumer consumes a main dish
        mainDish = true;
    }

    /**
     * set the mainDish boolean, which indicates whether
     * the costumer is eating a main dish or a dessert
     * @param mainDish  true, if the costumer is eating a main dish
     *                  false, if the costumer is eating a dessert
     */
    public void setMainDish (boolean mainDish) {
        this.mainDish = mainDish;
    }

    /**
     * Describes the dining hall's life cycle.
     * It will continually loop through the following steps
     * If there is a costumer waiting
     *      1) remove the customer from the queue
     *      2) serve the costumer
     *          a) check whether the costumer is eating a main dish or a dessert
     *      3) reactivate the costumer
     * if no costumer is waiting for a seat at the dining hall
     *      1) insert into the idleDiningHallSeatsQueue and
     *      2) wait (passivate) until someone arrives
     * @throws SuspendExecution
     */
    @Override
    public void lifeCycle() throws SuspendExecution {
        while (true) {
            if (myModel.customerDiningHallSeatsQueue.isEmpty()) {
                myModel.idleDiningHallSeatsQueue.insert(this);

                passivate();
            } else {
                CustomerProcess customer;

                customer = myModel.customerDiningHallSeatsQueue.first();
                myModel.customerDiningHallSeatsQueue.remove(customer);

                if (mainDish) {
                    hold(new TimeSpan(myModel.getRestingTimeMainDish()));
                } else {
                    hold(new TimeSpan(myModel.getRestingTimeDessert()));
                    mainDish = true;
                }

                customer.activate(new TimeSpan(0.0));
            }
        }
    }
}
