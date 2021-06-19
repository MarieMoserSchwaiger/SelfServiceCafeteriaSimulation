import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

public class EmployeeProcess extends SimProcess {

    private SelfServiceModel myModel;

    // stationIndex:
    //      Index 0 = sandwich bar
    //      Index 1 = menu bar
    //      Index 2 = check out
    //      Index 3 = dessert bar
    //      Index 4 = dessert check out
    private int stationIndex;

    public EmployeeProcess(Model owner, String name, boolean showInTrace, int stationIndex) {
        super(owner, name, showInTrace);

        myModel = (SelfServiceModel) owner;
        this.stationIndex = stationIndex;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {

        //überlegung: die mitarbeiter sind fix einer station zugeteilt, also wenn bei der sandwich bar keiner ansteht, kommt der mitarbeiter der sandwich bar in die idleQueue
        //              selbst, wenn bei der kasse viele Leute anstehen
        //    -> mitarbeiter kommt erst aus der idleQueue wenn jemand bei seiner Station ansteht

        while (true) {

            if (stationIndex == 0) {// && !myModel.customerSandwichBarQueue.isEmpty()) {
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
            } else if (stationIndex == 1) {//&& !myModel.customerMenuBarQueue.isEmpty()) {
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
            } else if (stationIndex == 2){// && !myModel.customerCheckOutQueue.isEmpty()) {
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
            } else if (stationIndex == 3) {// && !myModel.customerDessertBarQueue.isEmpty()) {
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
            } else if (stationIndex == 4) {// && !myModel.customerDessertCheckOutQueue.isEmpty()) {
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
