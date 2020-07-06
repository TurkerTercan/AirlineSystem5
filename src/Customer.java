import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Customer extends User {
    private static Scanner input;
    private boolean LogedIn = false;
    private ArrayList<Ticket> tickets;
    private PriorityQueue<Flight> flights;
    private static SkipList<User> users;
    FlightSystem Fsys;

    public Customer(String id, String password,FlightSystem Fsys, SkipList<User> systemUsers) {
        super(id, password);
        input = new Scanner(System.in);
        tickets = new ArrayList<>();
        this.Fsys = Fsys;
        users = systemUsers;
    }

    @Override
    public void login() {
        while (!LogedIn) {
            System.out.println("Password: ");
            String PWord = input.next();

            if (!PWord.equals(getPassword())) {
                System.out.println("Authentication failed please try again!!");
            }else LogedIn = !LogedIn;
        }
        menu();
    }

    public static void registration() {
        String id;
        String passwd;
        boolean stand;
        System.out.println("Customer Registration");
        do {
            stand = false;
            System.out.print("Id: ");
            id = input.nextLine();
            if (users.find(new User(id, "")) != null) {
                System.out.println("There is already a user registered with this id in the system.");
                stand = true;
            } else {
                System.out.print("\nPassword: ");
                passwd = input.nextLine();
                users.add(new User(id, passwd));
            }
        } while (stand);
        System.out.println("A new customer has been added to the system.");
    }

    @Override
    public void menu() {
        int choice = -1;
        while (choice!=0){
            System.out.println("\nMain menu:");
            System.out.println("please choose an action:");
            System.out.println("0-Up\n1-Buy a ticket\n2-Cancel Ticket\n3-Show Tickets");
            System.out.print("\nchoice:");

            choice = input.nextInt();
            switch (choice){
                case 0:
                    break;
                case 1:
                    buyTicket();
                    break;
                case 2:
                    cancelTicket();
                    break;
                case 3:
                    showTickets();
                    break;
                default:
                    System.out.println("Invalid Input!!\n");
            }
        }
    }

    private void buyTicket(){
        System.out.println("Please enter Setoff city:");
        String source = input.next();
        System.out.println("Please enter Destination city:");
        String dest = input.next();
        //ask by price or time ?
        System.out.println("1.Sort by time\n2.Sort by price\n");
        String choise = input.next();

        flights = Fsys.getFlights(source,dest);
        if (flights == null) {
            System.out.println("There is no flight between " + source + " and " + dest);
            Flight[] temp = Fsys.getPath(source,dest);
            if (temp.length == 0) {
                System.out.println("There is no transfer point either. Please try another");
                return;
            }
            System.out.println("You can transfer from your setOff to your destination with these flights");
            for (int i = 0; i < temp.length; i++) {
                System.out.println((i+1) + ". " + temp[i].toString());
            }
            System.out.println("Do you want to buy all tickets from these flights? (Y/N)");
            String tempStr = input.next();
            switch (tempStr) {
                case "Y":
                    for(Flight F : temp) {
                        F.addNewCustomer(this);
                        tickets.add(new Ticket(F.getDepartTime(), String.valueOf(F.getRemainingSeats())));
                    }
                    System.out.println("Operation is successful");
                    break;
                case "N":
                    System.out.println("Backing to the menu");
                    break;
                default:
                    System.out.println("Wrong Choice!");
            }
            return;
        }
        int index = 1;

        if(choise.matches("2")) {

            List<Flight> flights1 = new ArrayList<>();
            for ( int i = 0; i < flights.size(); i++ ) {
                flights1.add(flights.get(i));
            }

            QuickSort.sort(flights1);
            for ( Flight F : flights1 ) {
                System.out.println( flights.getIndexOf(F)+1 + F.toString());
            }
        }

        else {
            for(Flight F : flights){
                System.out.println(( index++ )+F.toString());
            }
        }

        System.out.println("Please choose a flight: ");
        index = input.nextInt();
        Flight chosen = flights.get(index-1);
        tickets.add(new Ticket(chosen.getDepartTime(),String.valueOf(chosen.getRemainingSeats())));
        chosen.addNewCustomer(this);
        System.out.println("Operation is successful");
    }

    private void cancelTicket(){
        System.out.println("Your Tickets : ");
        showTickets();
        System.out.print("Please enter ticket ID:");
        String TId = input.next();
        for(Ticket T : tickets){
            if(T.getId().equals(TId)){
                tickets.remove(T);
                return;
            }
        }
        System.out.println("Couldn't Find a ticket with provided ID in your active tickets");
    }

    private void showTickets(){
        if(tickets.size() == 0){
            System.out.println("You don't have any purchased tickets");
            return;
        }
        for (Ticket T : tickets){
            System.out.println(T.toString());
        }
    }


    private static class Ticket{
        private static int ID = 0;

        private String id;
        private String deprTime;
        private String seat;

        public Ticket(String id, String deprTime, String seat){
            this.id = id;
            this.deprTime = deprTime;
            this.seat = seat;
        }

        public Ticket(String deprTime, String seat){
            this(String.valueOf(ID++), deprTime, seat);
        }

        public String getDeprTime() {
            return deprTime;
        }

        public String getId() {
            return id;
        }

        public String getSeat() {
            return seat;
        }

        @Override
        public String toString() {
            return  "id: " + id + '\t' +
                    "deprTime: " + deprTime + '\t' +
                    "seat= " + seat + '\n';
        }
    }

    public static class CustomerTester {
        private static final String test_city_file = "cities.txt";
        private static final String test_distances_file = "distances.txt";
        private static final  String test_flights_file = "flights.txt";

        //Unique plane id that will be used for testing
        FlightSystem system;
        static Customer customer;
        static SkipList<User> users;

        public CustomerTester() throws FileNotFoundException {
            system = new FlightSystem(test_city_file,test_distances_file,test_flights_file);
            users = new SkipList<>();
            users.add(new User("test", "test"));

            customer = new Customer("test", "test", system, users);
        }

        public static void test_buyTicket() throws FileNotFoundException {
            System.out.println("Testing buy ticket method of Customer ");
            customer.buyTicket();
            customer.showTickets();
        }

        public static void test_cancelTicket() throws FileNotFoundException {
            System.out.println("Testing cancel ticket method of Customer ");
            customer.cancelTicket();
            customer.showTickets();
        }

        public static void test_registration() throws FileNotFoundException {
            System.out.println("Testing registration method of Customer ");
            customer.registration();
            customer.showTickets();
        }

        public static void main(String[] args) throws FileNotFoundException {
            CustomerTester customerTester = new CustomerTester();
            try {
                //customerTester.test_buyTicket();
                //customerTester.test_cancelTicket();
                //customerTester.test_registration();
            } catch (Exception e) {
                System.out.println("ERROR "+ e.getMessage());
            }
        }
    }
}
