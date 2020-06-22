import java.util.ArrayList;

/**
 * This class represents a flight from this airline company.
 * It has all necessary data fields and methods for a flight
 */
@SuppressWarnings("rawtypes")
public class Flight implements Comparable {
    //Data fields
    /** The ID of the flight */
    private String ID;
    /** The Plane that is will fly */
    private Plane plane;

    /** Destination address */
    private String destination;
    /** The plane's set off zone */
    private String setOff;

    /** The time that the plane will be departed */
    private String departTime;
    /** List of crew */
    private ArrayList<User> crew;
    /** List of customers */
    private ArrayList<Customer> customers;
    /** Total number of remaining seats */
    private int remainingSeats;

    public double getPricePerSeat() {
        return pricePerSeat;
    }

    /** A price for seat */
    private double pricePerSeat;

    //Constructors
    /**
     * Basic constructor for flight class.
     * @param ID Flight's ID
     * @param plane The plane that will fly
     * @param destination Flight's destination place
     * @param setOff Flight's set off place
     * @param departTime Flight's depart time
     */
    public Flight( String ID, Plane plane, String destination, String setOff, String departTime, double price) {
        this.ID = ID;
        this.plane = plane;
        this.destination = destination;
        this.setOff = setOff;
        this.departTime = departTime;
        remainingSeats = plane.getCapacity();
        this.pricePerSeat = price;
        crew = new ArrayList<>();
    }

    //Methods
    /** Adds new crew member in to the arraylist */
    public void addCrewMember(User user){
        crew.add(user);
    }
    /** Removes crew member from the arraylist */
    public boolean removeCrewMember(User user){return crew.remove(user);}

    /** Adds new customer in to the list */
    public void addNewCustomer(Customer customer){
        customers.add(customer);
        remainingSeats--;
    }

    /** Getter method for depart time */
    public String getDepartTime() {
        return departTime;
    }

    /**
     * Returns how many seats are available int this flight.
     * @return how many seats are available in this flight.
     */
    public int getRemainingSeats(){
        return remainingSeats;
    }

    public String getSetOff(){
        return setOff;
    }

    /**
     * Override toString method to represent a String for this class
     * @return returns a String that represents a flight
     */
    @Override
    public String toString()  {
        return "Flight ID: " + ID + "\n" +
                "Set Off: " + setOff + "\tDestination: " + destination + "\n" +
                "Depart Time: " + departTime + "\n" +
                customers.size() + " Customers, " + crew.size() + " Crew members\n";
    }

    /**
     * This method compares two flight objects and returns extraction of
     * converted depart times
     * @param o The object that will be compared
     * @return extraction of two calculated depart times
     */
    @Override
    public int compareTo(Object o) {
        //Split departTime
        String[] time1 = departTime.split("/");
        int day = Integer.parseInt(time1[0]);
        int month = Integer.parseInt(time1[1]);

        String[] time2 = time1[2].split(",");
        int year = Integer.parseInt(time2[0]);
        String[] time3 = time2[1].split(":");
        int hour = Integer.parseInt(time3[0]);
        int minute = Integer.parseInt(time3[1]);

        //Split parameter's departTime
        time1 = ((Flight) o).getDepartTime().split("/");
        int o_day = Integer.parseInt(time1[0]);
        int o_month = Integer.parseInt(time1[1]);

        time2 = time1[2].split(",");
        int o_year = Integer.parseInt(time2[0]);
        time3 = time2[1].split(":");
        int o_hour = Integer.parseInt(time3[0]);
        int o_minute = Integer.parseInt(time3[1]);

        int total_1 = minute + 60 * (hour + 24 * (day + 30 * (month + 12 * year)));
        int total_2 = o_minute + 60 * (o_hour + 24 * (o_day + 30 * (o_month + 12 * o_year)));

        return total_1 - total_2;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        return setOff.equals(((Flight) obj).setOff) && destination.equals(((Flight) obj).destination)
                && departTime.equals(((Flight) obj).departTime);
    }

    @Override
    public int hashCode() {
        return setOff.hashCode() + destination.hashCode() + departTime.hashCode();
    }

    public String getID() {
        return ID;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination){this.destination = destination;}

    public void setSetOff(String setOff){this.setOff = setOff;}

    public void setDepartTime(String departTime){this.departTime = departTime;}

    public void setPlane(Plane plane){this.plane = plane;}
}
