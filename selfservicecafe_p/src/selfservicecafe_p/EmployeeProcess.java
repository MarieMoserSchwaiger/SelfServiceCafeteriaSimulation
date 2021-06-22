package selfservicecafe_p;

import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * This class represents the employee on the SelfServiceModel model.
 * Every employee is assigned to a specific station.
 * The Employee waits in the corresponding queue until a costumer requests
 * their service.
 */
public class EmployeeProcess extends SimProcess {

    // a reference to the model this process is part of
    private SelfServiceModel myModel;

    // stationIndex:
    //      Index 0 = sandwich bar
    //      Index 1 = menu bar
    //      Index 2 = check out
    //      Index 3 = dessert bar
    //      Index 4 = dessert check out
    private int stationIndex;

    /**
     * Constructor of the EmployeeProcess
     * @param owner         the model this process belongs to
     * @param name          this employee's name
     * @param showInTrace   flag to indicate if this process shall produce output for the trace
     * @param stationIndex  this employee's station
     */
    public EmployeeProcess(Model owner, String name, boolean showInTrace, int stationIndex) {
        super(owner, name, showInTrace);

        myModel = (SelfServiceModel) owner;
        this.stationIndex = stationIndex;
    }

    /**
     * Describes the employee's life cycle.
     * It will first check which station the employee belongs to.
     * If there is a costumer waiting, the employee
     *      1) removes the customer from the corresponding queue
     *      2) serves the costumer
     *      3) reactivates the costumer
     * if no costumer is waiting at the employee's station, the employee
     *      1) inserts themselves into the station's idleEmployees queue and
     *      2) waits until someone arrives
     * @throws SuspendExecution
     */
    @Override
    public void lifeCycle() throws SuspendExecution {
        while (true) {
            if (stationIndex == 0) {
                if (myModel.customerSandwichBarQueue.isEmpty()) {
                    myModel.idleSandwichBarEmployeesQueue.insert(this);

                    passivate();
                } else {
                    CustomerProcess customer = myModel.customerSandwichBarQueue.first();
                    myModel.customerSandwichBarQueue.remove(customer);
                    hold(new TimeSpan(myModel.getServiceTimeSandwichBar()));

                    // customer was served at sandwich bar
                    // reactivate customer
                    customer.activate(new TimeSpan(0.0));
                }
            } else if (stationIndex == 1) {
                if (myModel.customerMenuBarQueue.isEmpty()){
                    myModel.idleMenuBarEmployeesQueue.insert(this);

                    passivate();
                } else {
                    CustomerProcess customer = myModel.customerMenuBarQueue.first();
                    myModel.customerMenuBarQueue.remove(customer);
                    hold(new TimeSpan(myModel.getServiceTimeMenuBar()));

                    // customer was served at menu bar
                    // reactivate customer
                    customer.activate(new TimeSpan(0.0));
                }
            } else if (stationIndex == 2){
                if (myModel.customerCheckOutQueue.isEmpty()) {
                    myModel.idleCheckOutEmployeesQueue.insert(this);

                    passivate();
                } else {
                    CustomerProcess customer = myModel.customerCheckOutQueue.first();
                    myModel.customerCheckOutQueue.remove(customer);
                    hold(new TimeSpan(myModel.getServiceTimeCheckOut()));

                    // customer was served at check out
                    // reactivate customer
                    customer.activate(new TimeSpan(0.0));
                }
            } else if (stationIndex == 3) {
                if (myModel.customerDessertBarQueue.isEmpty()) {
                    myModel.idleDessertBarEmployeesQueue.insert(this);
                    passivate();
                } else {
                    CustomerProcess customer = myModel.customerDessertBarQueue.first();
                    myModel.customerDessertBarQueue.remove(customer);
                    hold(new TimeSpan(myModel.getServiceTimeDessertBar()));

                    // customer was served at dessert bar
                    // reactivate customer
                    customer.activate(new TimeSpan(0.0));
                }
            } else if (stationIndex == 4) {
                if (myModel.customerDessertCheckOutQueue.isEmpty()) {
                    myModel.idleDessertCheckOutEmployeesQueue.insert(this);
                    passivate();
                } else {
                    CustomerProcess customer = myModel.customerDessertCheckOutQueue.first();
                    myModel.customerDessertCheckOutQueue.remove(customer);
                    hold(new TimeSpan(myModel.getServiceTimeDessertCheckOut()));

                    // customer was served at dessert check out
                    // reactivate customer
                    customer.activate(new TimeSpan(0.0));
                }
            } else
                try {
                    throw new Exception("Error: station with index " + stationIndex + " does not exist.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
