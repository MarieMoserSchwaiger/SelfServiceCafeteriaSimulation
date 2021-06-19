import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * This class represents the dining hall on the SelfServiceModel model.
 */

public class DiningHallProcess extends SimProcess{
    private SelfServiceModel myModel;

   // private int numberOfSeats;

    private boolean mainDish;


    /**
     * Constructor of the dining hall process
     * @param owner
     * @param name
     * @param showInTrace
     */
    public DiningHallProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

       // numberOfSeats = 400;
        myModel = (SelfServiceModel) owner;
        mainDish = true;
    }

    public void setMainDish (boolean mainDish) {
        this.mainDish = mainDish;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {

        while (true) {
            if (myModel.customerDiningHallSeats.isEmpty() && myModel.customerReservedSeats.isEmpty()) {
                myModel.idleDiningHallSeatsQueue.insert(this);
                passivate();
            } else {
                CustomerProcess customer;

                if (myModel.customerReservedSeats.isEmpty()) {
                   customer = myModel.customerDiningHallSeats.first();
                   myModel.customerDiningHallSeats.remove(customer);
                } else {
                   customer = myModel.customerReservedSeats.first();
                   myModel.customerReservedSeats.remove(customer);
                }

                if (mainDish) {
                    hold(new TimeSpan(myModel.getRestingTimeMainDish()));
                } else {
                    hold(new TimeSpan(myModel.getRestingTimeDessert()));
                }

                customer.activate(new TimeSpan(0.0));


            }

        }

    }


}
