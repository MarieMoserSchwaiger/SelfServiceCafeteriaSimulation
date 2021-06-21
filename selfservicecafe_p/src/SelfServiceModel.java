import desmoj.core.simulator.*;
import desmoj.core.dist.*;

/**
 * This is the model class. It is the main class of a process-oriented model
 * of a self service cafeteria with different stations and a dining hall.
 */
public class SelfServiceModel extends Model {

    // model parameter: the number of stations with employees
    protected static int NUM_STATIONS = 5;

    protected static int employeesPerStation = 2;

    protected static int NUM_DRINKSMACHINES = 2;

    protected static int NUM_DININGHALLCAPACITY = 50;

    private ContDistUniform customerArrivalTime;

    public double getCustomerArrivalTime() {
        return customerArrivalTime.sample();
    }

    // service time for the different stations
    private ContDistUniform serviceTimeSandwichBar;
    private ContDistUniform serviceTimeMenuBar;
    private ContDistUniform serviceTimeDrinksBar;  // no employee
    private ContDistUniform serviceTimeCheckOut;
    private ContDistUniform serviceTimeDessertBar;
    private ContDistUniform serviceTimeDessertCheckOut;

    // time spent at the dining hall
    private ContDistUniform restingTimeMainDish;
    private ContDistUniform restingTimeDessert;

    public double getServiceTimeSandwichBar() {
        return serviceTimeSandwichBar.sample();
    }

    public double getServiceTimeMenuBar() {
        return serviceTimeMenuBar.sample();
    }

    public double getServiceTimeDrinksBar() {
        return serviceTimeDrinksBar.sample();
    }

    public double getServiceTimeCheckOut() {
        return serviceTimeCheckOut.sample();
    }

    public double getServiceTimeDessertBar() {
        return serviceTimeDessertBar.sample();
    }

    public double getServiceTimeDessertCheckOut() {
        return serviceTimeDessertCheckOut.sample();
    }

    public double getRestingTimeMainDish() {
        return restingTimeMainDish.sample();
    }

    public double getRestingTimeDessert() {
        return restingTimeDessert.sample();
    }

    // queues for customers
    protected ProcessQueue<CustomerProcess> customerSandwichBarQueue;  // 0
    protected ProcessQueue<CustomerProcess> customerMenuBarQueue;      // 1
    protected ProcessQueue<CustomerProcess> customerDrinksBarQueue;  // no employee
    protected ProcessQueue<CustomerProcess> customerCheckOutQueue;    // 2
    protected ProcessQueue<CustomerProcess> customerDessertBarQueue;  // 3
    protected ProcessQueue<CustomerProcess> customerDessertCheckOutQueue; // 4
    protected ProcessQueue<CustomerProcess> customerDiningHallSeatsQueue;
    protected ProcessQueue<CustomerProcess> customerReservedSeats;

    // queues for employees
    protected ProcessQueue<EmployeeProcess> idleSandwichBarEmployeesQueue;
    protected ProcessQueue<EmployeeProcess> idleMenuBarEmployeesQueue;
    protected ProcessQueue<EmployeeProcess> idleCheckOutEmployeesQueue;
    protected ProcessQueue<EmployeeProcess> idleDessertBarEmployeesQueue;
    protected ProcessQueue<EmployeeProcess> idleDessertCheckOutEmployeesQueue;

    // queues for drinks bar and dining hall
    protected ProcessQueue<DrinksBarProcess>  idleDrinksBarQueue;
    protected ProcessQueue<DiningHallProcess> idleDiningHallSeatsQueue;


    /**
     * SelfServiceModel Constructor
     *
     * Creates a new SelfServiceModel model via calling the constructor of the superclass.
     *
     * @param owner         the model this model is part of (set to null when there is no such model)
     * @param modelName     this model's name
     * @param showInReport  flag to indicate if this model shall produce output to the report file
     * @param showInTrace   flag to indicate if this model shall produce output to the trace file
     */
    public SelfServiceModel(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
        super(owner, modelName, showInReport, showInTrace);
    }

    /**
     * Returns a description of the model to be used in the report.
     * @return model description as a string
     */
    public String description() {
        return "This model describes a self service cafe\n" +
                    "to be continued...";
    }

    /**
     * Activates dynamic model components (simulation processes).
     *
     * This method is used to place all events or processes on the internal event list of the simulator
     * which are necessary to start the simulation.
     */
    public void doInitialSchedules() {
        // create and activate the employees
        for (int i=0; i < NUM_STATIONS; i++) {
            for (int j=0; j < employeesPerStation; j++) {
                EmployeeProcess employee = new EmployeeProcess(this, "Employee", true, i);
                employee.activate(new TimeSpan(0.0));
            }
        }

        // create and activate the drinksBar machine(s)
        for (int i=0; i < NUM_DRINKSMACHINES; i++) {
            DrinksBarProcess drinksBar = new DrinksBarProcess(this, "DrinksBar", true);
            drinksBar.activate(new TimeSpan(0.0));
        }

        // create and activate the dining hall seats
        for (int i=0; i < NUM_DININGHALLCAPACITY; i++) {
            DiningHallProcess diningHall = new DiningHallProcess(this, "DiningHall", true);
            diningHall.activate(new TimeSpan(0.0));
        }

        // create and activate the customer generator process
        CustomerGenerator generator = new CustomerGenerator(this, "CustomerArrival", false);
        generator.activate();
    }

    /**
     * Initialises static model components like distributions and queues.
     */
    public void init() {
        // service times

        // initialise the serviceTimeSandwichBarStream
        // Parameters:
        // this                              = belongs to this model
        // "serviceTimeSandwichBarStream"    = the name of the stream
        // 30.0                              = service takes between 30 and
        // 90.0                              = 90 seconds
        // true                              = show in report?
        // true                              = show in trace?
        serviceTimeSandwichBar = new ContDistUniform(this, "serviceTimeSandwichBarStream", 30.0, 90.0, true, true);

        // initialise the serviceTimeMenuBarStream
        // Parameters:
        // this                              = belongs to this model
        // "serviceTimeMenuBarStream"        = the name of the stream
        // 15.0                              = service takes between 15 and
        // 75.0                              = 75 seconds
        // true                              = show in report?
        // true                              = show in trace?
        serviceTimeMenuBar = new ContDistUniform(this, "serviceTimeMenuBarStream", 15.0, 75.0, true, true);

        // initialise the serviceTimeDrinksBarStream
        // Parameters:
        // this                              = belongs to this model
        // "serviceTimeDrinksBarStream"      = the name of the stream
        // 15.0                              = service takes between 15 and
        // 25.0                              = 25 seconds
        // true                              = show in report?
        // true                              = show in trace?
        serviceTimeDrinksBar = new ContDistUniform(this, "serviceTimeDrinksBarStream", 15.0, 25.0, true, true);

        // initialise the serviceTimeCheckOutStream
        // Parameters:
        // this                              = belongs to this model
        // "serviceTimeCheckOutStream"       = the name of the stream
        // 15.0                              = service takes between 15 and
        // 35.0                              = 35 seconds
        // true                              = show in report?
        // true                              = show in trace?
        serviceTimeCheckOut = new ContDistUniform(this, "serviceTimeCheckOutStream", 15.0, 35.0, true, true);

        // initialise the serviceTimeDessertBarStream
        // Parameters:
        // this                              = belongs to this model
        // "serviceTimeDessertBarStream"     = the name of the stream
        // 20.0                              = service takes between 20 and
        // 40.0                              = 40 seconds
        // true                              = show in report?
        // true                              = show in trace?
        serviceTimeDessertBar = new ContDistUniform(this, "serviceTimeDessertBarStream", 20.0, 40.0, true, true);

        // initialise the serviceTimeDessertCheckOutStream
        // Parameters:
        // this                               = belongs to this model
        // "serviceTimeDessertCheckOutStream" = the name of the stream
        // 15.0                               = service takes between 15 and
        // 35.0                               = 35 seconds
        // true                               = show in report?
        // true                               = show in trace?
        serviceTimeDessertCheckOut = new ContDistUniform(this, "serviceTimeDessertCheckOutStream", 15.0, 35.0, true, true);


        // resting times

        // initialise the restingTimeMainDishStream
        // Parameters:
        // this                               = belongs to this model
        // "restingTimeMainDishStream"        = the name of the stream
        // 600.0                              = service takes between 600 (10 min) and
        // 1800.0                             = 1800 (30 min) seconds
        // true                               = show in report?
        // true                               = show in trace?
        restingTimeMainDish = new ContDistUniform(this, "restingTimeMainDishStream", 600.0, 1800.0, true, true);

        // initialise the restingTimeDessertStream
        // Parameters:
        // this                               = belongs to this model
        // "restingTimeDessertStream"         = the name of the stream
        // 420.0                              = service takes between 420 (7 min) and
        // 780.0                              = 780 (13 min) seconds
        // true                               = show in report?
        // true                               = show in trace?
        restingTimeDessert = new ContDistUniform(this, "restingTimeDessertStream", 420.0, 780.0, true, true);


        // customer arrival

        // initialise the customerArrivalTimeStream
        // Parameters:
        // this                         = belongs to this model
        // "customerArrivalTimeStream"  = the name of the stream
        // 10.0                         = customers arrive between 10 and
        // 50.0                         = 50 seconds
        // true                         = show in report?
        // true                         = show in trace?
        customerArrivalTime = new ContDistUniform(this, "customerArrivalTimeStream", 10, 50, true, true);


        // customer queues

        // initialise the customerSandwichBarQueue
        // Parameters:
        // this                         = belongs to this model
        // "SandwichBarQueue"           = the name of the Queue
        // true                         = show in report?
        // true                         = show in trace?
        customerSandwichBarQueue = new ProcessQueue<>(this, "SandwichBarQueue", true, true);

        // initialise the customerMenuBarQueue
        // Parameters:
        // this                         = belongs to this model
        // "MenuBarQueue"               = the name of the Queue
        // true                         = show in report?
        // true                         = show in trace?
        customerMenuBarQueue = new ProcessQueue<>(this, "MenuBarQueue", true, true);

        // initialise the customerDrinksBarQueue
        // Parameters:
        // this                         = belongs to this model
        // "DrinksBarQueue"             = the name of the Queue
        // true                         = show in report?
        // true                         = show in trace?
        customerDrinksBarQueue = new ProcessQueue<>(this, "DrinksBarQueue", true, true);

        // initialise the customerCheckOutQueue
        // Parameters:
        // this                         = belongs to this model
        // "CheckOutQueue"              = the name of the Queue
        // true                         = show in report?
        // true                         = show in trace?
        customerCheckOutQueue = new ProcessQueue<>(this, "CheckOutQueue", true, true);

        // initialise the customerDessertBarQueue
        // Parameters:
        // this                         = belongs to this model
        // "DessertBarQueue"              = the name of the Queue
        // true                         = show in report?
        // true                         = show in trace?
        customerDessertBarQueue = new ProcessQueue<>(this, "DessertBarQueue", true, true);

        // initialise the customerDessertCheckOutQueue
        // Parameters:
        // this                         = belongs to this model
        // "DessertCheckOutQueue"       = the name of the Queue
        // true                         = show in report?
        // true                         = show in trace?
        customerDessertCheckOutQueue = new ProcessQueue<>(this, "DessertCheckOutQueue", true, true);

        // initialise the customerDiningHallSeatsQueue
        // Parameters:
        // this                            = belongs to this model
        // "customerDiningHallSeatsQueue"  = the name of the Queue
        // true                            = show in report?
        // true                            = show in trace?
        customerDiningHallSeatsQueue = new ProcessQueue<>(this, "customerDiningHallSeatsQueue", true, true);

        // initialise the customerReservedSeats
        // Parameters:
        // this                         = belongs to this model
        // "customerReservedSeats"      = the name of the Queue
        // true                         = show in report?
        // true                         = show in trace?
        customerReservedSeats = new ProcessQueue<>(this, "customerReservedSeats", true, true);


        // employees queues

        // initialise the idleSandwichBarEmployeesQueue
        // Parameters:
        // this                                  = belongs to this model
        // "idleSandwichBarEmployeesQueue"       = the name of the Queue
        // true                                  = show in report?
        // true                                  = show in trace?
        idleSandwichBarEmployeesQueue = new ProcessQueue<>(this, "idleSandwichBarEmployeesQueue", true, true);

        // initialise the idleMenuBarEmployeesQueue
        // Parameters:
        // this                                  = belongs to this model
        // "idleMenuBarEmployeesQueue"           = the name of the Queue
        // true                                  = show in report?
        // true                                  = show in trace?
        idleMenuBarEmployeesQueue = new ProcessQueue<>(this, "idleMenuBarEmployeesQueue", true, true);

        // initialise the idleCheckOutEmployeesQueue
        // Parameters:
        // this                                  = belongs to this model
        // "idleCheckOutEmployeesQueue"          = the name of the Queue
        // true                                  = show in report?
        // true                                  = show in trace?
        idleCheckOutEmployeesQueue = new ProcessQueue<>(this, "idleCheckOutEmployeesQueue", true, true);

        // initialise the idleDessertBarEmployeesQueue
        // Parameters:
        // this                                  = belongs to this model
        // "idleDessertBarEmployeesQueue"        = the name of the Queue
        // true                                  = show in report?
        // true                                  = show in trace?
        idleDessertBarEmployeesQueue = new ProcessQueue<>(this, "idleDessertBarEmployeesQueue", true, true);

        // initialise the idleDessertCheckOutEmployeesQueue
        // Parameters:
        // this                                  = belongs to this model
        // "idleDessertCheckOutEmployeesQueue"   = the name of the Queue
        // true                                  = show in report?
        // true                                  = show in trace?
        idleDessertCheckOutEmployeesQueue = new ProcessQueue<>(this, "idleDessertCheckOutEmployeesQueue", true, true);

        // initialise the idleDrinksBarQueue
        // Parameters:
        // this                                  = belongs to this model
        // "idleDrinksBarQueue"                  = the name of the Queue
        // true                                  = show in report?
        // true                                  = show in trace?
        idleDrinksBarQueue = new ProcessQueue<>(this, "idleDrinksBarQueue", true, true);

        // initialise the idleDiningHallSeatsQueue
        // Parameters:
        // this                                  = belongs to this model
        // "idleDiningHallSeatsQueue"            = the name of the Queue
        // true                                  = show in report?
        // true                                  = show in trace?
        idleDiningHallSeatsQueue = new ProcessQueue<>(this, "idleDiningHallSeatsQueue", true, true);
    }

    /**
     * Runs the model
     * @param args
     */
    public static void main(String[] args) {

        //create model and experiment
        SelfServiceModel model = new SelfServiceModel(null, "Self Service Cafe Model", true, true);

        Experiment exp = new Experiment("SelfServiceExperiment");

        // connect model and experiment
        model.connectToExperiment(exp);

        // set experiment parameters
        exp.setShowProgressBar(true);
        exp.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
        exp.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));

        // set end of simulation at 4 hours (14400 seconds)
        exp.stop(new TimeInstant(14400));

        exp.start();

        exp.report();

        exp.finish();
    }


}
