package selfservicecafe_p;

import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * This class represents the customer on the SelfServiceModel model.
 */
public class CustomerProcess extends SimProcess{

    // a reference to the model this process is part of
    private SelfServiceModel myModel;

    /**
     * Constructor of the customer process
     * @param owner         the model this process belongs to
     * @param name          this customer's name
     * @param showInTrace   flag to indicate if this process shall produce output for the trace
     */
    public CustomerProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        myModel = (SelfServiceModel) owner;
    }

    /**
     * Describes the customer's life cycle:
     *
     * On arrival 40% of the customer will enter the queue for the sandwich bar.
     * The rest will enter the queue for the menu bar.
     * After that 90% will go to the full self-service drinks bar.
     * The payment process happens at one joint check-out. After paying the costumer looks for a seat
     * and consumes his/her meal. After the main dish 10% of the costumers get a dessert, pay at an own
     * check-out for the dessert bar and go find a new seat at the dining hall.
     * @throws SuspendExecution
     */
    @Override
    public void lifeCycle() throws SuspendExecution {
        double random = Math.random();

        if (random < 0.4) {
            myModel.customerSandwichBarQueue.insert(this);
            sendTraceNote("SandwichBarQueueLength: " + myModel.customerSandwichBarQueue.length());

            if (!myModel.idleSandwichBarEmployeesQueue.isEmpty()) {
                EmployeeProcess employee = myModel.idleSandwichBarEmployeesQueue.first();
                myModel.idleSandwichBarEmployeesQueue.remove(employee);

                employee.activateAfter(this);
            }
        }
        else {
            myModel.customerMenuBarQueue.insert(this);
            sendTraceNote("MenuBarQueueLength: " + myModel.customerMenuBarQueue.length());

            if (!myModel.idleMenuBarEmployeesQueue.isEmpty()) {
                EmployeeProcess employee = myModel.idleMenuBarEmployeesQueue.first();
                myModel.idleMenuBarEmployeesQueue.remove(employee);

                employee.activateAfter(this);
            }
        }
        passivate();

        // reactivation after sandwich bar/ menu bar

        random = Math.random();

        if (random < 0.9) {
            myModel.customerDrinksBarQueue.insert(this);
            sendTraceNote("DrinksBarQueueLength: " + myModel.customerDrinksBarQueue.length());

            if (!myModel.idleDrinksBarQueue.isEmpty()) {
                DrinksBarProcess drinksBar = myModel.idleDrinksBarQueue.first();
                myModel.idleDrinksBarQueue.remove(drinksBar);

                drinksBar.activateAfter(this);
            }
            passivate();

            // customer is done at the drinks bar
        }

        // now: check out
        myModel.customerCheckOutQueue.insert(this);
        sendTraceNote("CheckOutQueueLength: " + myModel.customerCheckOutQueue.length());

        if (!myModel.idleCheckOutEmployeesQueue.isEmpty()) {
            EmployeeProcess employee = myModel.idleCheckOutEmployeesQueue.first();
            myModel.idleCheckOutEmployeesQueue.remove(employee);

            employee.activateAfter(this);
        }
        passivate();

        // customer is done at check out

        // now: eating at dining hall
        myModel.customerDiningHallSeatsQueue.insert(this);
        sendTraceNote("DiningHallQueue: " + myModel.customerDiningHallSeatsQueue.length());

        if (!myModel.idleDiningHallSeatsQueue.isEmpty()) {
            DiningHallProcess diningHall = myModel.idleDiningHallSeatsQueue.first();
            myModel.idleDiningHallSeatsQueue.remove(diningHall);

            diningHall.activateAfter(this);
        }
        passivate();

        // customer is done with the main dish
        // 10% get a dessert

        random = Math.random();

        if (random < 0.1) {
            myModel.customerDessertBarQueue.insert(this);
            sendTraceNote("DessertBarQueue: " + myModel.customerDessertBarQueue.length());

            if (!myModel.idleDessertBarEmployeesQueue.isEmpty()) {
                EmployeeProcess employee = myModel.idleDessertBarEmployeesQueue.first();
                myModel.idleDessertBarEmployeesQueue.remove(employee);

                employee.activateAfter(this);

                passivate();
            }

            // done with getting the dessert
            // now: pay for dessert

            myModel.customerDessertCheckOutQueue.insert(this);
            sendTraceNote("DessertCheckOutQueue: " + myModel.customerDessertCheckOutQueue.length());

            if (!myModel.idleDessertCheckOutEmployeesQueue.isEmpty()) {
                EmployeeProcess employee = myModel.idleDessertCheckOutEmployeesQueue.first();
                myModel.idleDessertCheckOutEmployeesQueue.remove(employee);

                employee.activateAfter(this);

                passivate();
            }

            // done with paying
            // now: get back to the dining hall, look for a new seat

            myModel.customerDiningHallSeatsQueue.insert(this);
            sendTraceNote("DrinksBarQueueLength: " + myModel.customerDrinksBarQueue.length());

            if (!myModel.idleDiningHallSeatsQueue.isEmpty()) {
                DiningHallProcess diningHall = myModel.idleDiningHallSeatsQueue.first();
                myModel.idleDiningHallSeatsQueue.remove(diningHall);

                diningHall.setMainDish(false);
                diningHall.activateAfter(this);
            }
            passivate();

        }

        sendTraceNote("Customer was served");
    }
}
