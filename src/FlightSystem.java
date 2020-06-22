import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * FlightSystem class is implemented for storing values of Planes, Graph,
 * Map of the flights and some user's needed methods. All flights stored in a Java.Map interface
 * and all planes stored
 * in a Binary-Balanced Search Tree. Graph's Edges represents a flight coming from source to
 * the destination.
 */
public class FlightSystem {
    //Data Fields

    /** BinaryBalancedSearchTree of Planes, Planes are compared with their capacities */
    private TreeSet<Plane> availablePlanes;

    /** Nested Flight Map of PriorityQueue. First key represents SetOff String,
     * second String represents Destination and PriorityQueue is stores all flights
     * from SetOff to Destination
     */
    private Map<String, Map<String,PriorityQueue<Flight>>> flight_map;

    /**
     * Graph's vertices represent cities and Edges between cities are
     * the flights between source to the destination. An Edge's weight
     * is the distance between these two cities.
     */
    private Graph graph;

    /**
     * Distance between two cities
     * it works like distance[source][destination]
     */
    private ArrayList<List<Integer>> distance;

    /**
     * ArrayList of names of cities
     */
    private ArrayList<String> city;

    /**
     * Basic Constructor for FlightSystem
     * Instantiates all data fields and reads two txt with scanner
     * @throws FileNotFoundException If there is no such file
     */
    public FlightSystem() throws FileNotFoundException {
        distance = new ArrayList<>();
        city = new ArrayList<>();
        availablePlanes = new TreeSet<>();
        flight_map = new HashMap<>();
        scanFromFile();
        graph = new ListGraph(50, true);
    }

    /**
     * Reads txt files and stores it in ArrayLists
     * @throws FileNotFoundException If there is no such file
     */
    private void scanFromFile() throws FileNotFoundException {
        Scanner scanDistance = new Scanner(new File("distances.txt"));
        Scanner scanCities = new Scanner(new File("cities.txt"));
        for (int i = 0; i < 50; i++) {
            distance.add(new ArrayList<>());
            city.add(scanCities.nextLine());
            for (int j = 0; j < 50; j++) {
                distance.get(i).add(scanDistance.nextInt());
            }
        }
    }    

    private int[] shortestPath(Graph graph, int start){
		Queue<Integer> theQueue = new LinkedList<>();
		//Declare array parent and initialize its elements to -1
		int[] parent = new int[graph.getNumV()];
		for(int i = 0; i < graph.getNumV(); i++){
			parent[i] = -1;
		}
		//Declare array identified and initialize its elements to false
		boolean[] identified = new boolean[graph.getNumV()];
		/* Mark the start vertex as identified and insert it into the queue */
		identified[start] = true;
		theQueue.offer(start);
		
		/* Perform breadth-first search until done */
		while(!theQueue.isEmpty()){
			/* Take a vertex, current, out of the queue. Begin visiting current */
			int current = theQueue.remove();
			/* Examine each vertex, neighbor, adjacent to current. */
			Iterator<Edge> itr = graph.edgeIterator(current);
			while(itr.hasNext()){
				Edge edge = itr.next();
				int neighbor = edge.getDest();
				//If neighbor has not been identified
				if(!identified[neighbor]){
					//Mark it identified
					identified[neighbor] = true;
					//Place it into the queue
					theQueue.offer(neighbor);
					/* Insert the edge (current, neighbor) into the tree */
					parent[neighbor] = current;
				}
			}
			//Finished visiting current
		}
		return parent;
    }

    /**
     * If Map contains given element returns false, otherwise; Flight is added to PriorityQueue that is
     * Mapped with SetOff and Destination String
     * @param newFlight New flight to be added to the Map
     * @return If Map contains same elements, false; otherwise, a new Flight is added to the Map and return true
     */
    public boolean addFlight(Flight newFlight) {
        String setOff = newFlight.getSetOff();
        String destination = newFlight.getDestination();
        Map<String, PriorityQueue<Flight>> temp = flight_map.get(setOff);
        PriorityQueue<Flight> flight;
        if (temp == null) {
            flight = new PriorityQueue<>();
            flight.add(newFlight);
            temp = new HashMap<>();
            temp.put(destination, flight);
            flight_map.put(setOff, temp);
        }
        if (temp.containsKey(destination)) {
            flight = temp.get(destination);
            if (flight.contains(newFlight))
                return false;
            flight.add(newFlight);
        } else {
            flight = new PriorityQueue<>();
            flight.add(newFlight);
            temp.put(destination, flight);
        }
        graph.insert(new Edge(city.indexOf(setOff), city.indexOf(destination),
                distance.get(city.indexOf(setOff)).get(city.indexOf(destination))));
        return true;
    }

    /**
     * Removes given element from the Map. If element is not found return false
     * @param removed The element will be removed
     * @return If element is not found return false
     */
    public boolean removeFlight(Flight removed) {
        String setOff = removed.getSetOff();
        String destination = removed.getDestination();
        Map<String, PriorityQueue<Flight>> temp = flight_map.get(setOff);
        if (temp == null)
            return false;
        if (!temp.containsKey(destination))
            return false;

        PriorityQueue<Flight> flight = temp.get(destination);
        boolean result = flight.remove(removed);
        if (flight.size() == 0) {
            temp.remove(destination);
        }
        if (result) {
            return graph.remove(new Edge(city.indexOf(setOff), city.indexOf(destination)));  
        } else {
            return false;
        }
    }

    /**
     * Adds new element to the TreeSet
     * @param plane The object will be added
     */
    public void addPlane(Plane plane) {
        availablePlanes.add(plane);
    }

    /**
     * Removes first element in the TreeSet and returns it
     * @return First Plane of the TreeSet
     */
    public Plane popPlane() {
        Plane temp = availablePlanes.first();
        availablePlanes.remove(temp);
        return temp;
    }

    /**
     * Shows all planes in the TreeSet
     */
    public void ShowAllPlanes() {
        System.out.println(availablePlanes.toString());
    }

    /**
     * Getter method for availablePlanes
     * @return availablePlanes
     */
    public TreeSet<Plane> getAvailablePlanes() {
        return availablePlanes;
    }

    /**
     * Getter method for flight_map
     * @return flight_map
     */
    public Map<String,Map<String, PriorityQueue<Flight>>> getFlight_map() {
        return flight_map;
    }

    /**
     * Gets PriorityQueue between setOff and Destination
     * @param setOff The string that plane's setOff
     * @param destination The string that flight's destination
     * @return PriorityQueue that is contains all flights setOff to Destination
     */
    public PriorityQueue<Flight> getFlights(String setOff, String destination) {
        return flight_map.get(setOff).get(destination);
    }

    /**
     * If there is no flight between source and destination
     * Customer needs to get shortest path to get to the destination
     * It searches graph with using Dijkstra's Shortest Path Algorithm
     * and finds the Flights that are shortest to get to the destination
     * @param source The customer's setOff String
     * @param destination The customer's destination String
     * @return Flight array that is shortest path
     */
    public Flight[] getPath(String source, String destination) {
        int index_source = city.indexOf(source);
        int index_destination = city.indexOf(destination);

        int[] pred = new int[graph.getNumV()];
        double[] distance = new double[graph.getNumV()];
        dijkstraAlgorithm(graph, index_source, pred, distance);
        //If there no path between them
        if (pred[index_destination] == Double.POSITIVE_INFINITY)
            return null;

        Stack<Integer> temp = new Stack<>();
        while(index_destination != index_source) {
            temp.push(index_destination);
            index_destination = pred[index_destination];
        }
        temp.push(index_source);
        Flight[] temp_flight = new Flight[temp.size() - 1];
        int temp_source = temp.pop();
        int temp_destination;
        int i = 0;
        while(!temp.isEmpty()) {
            temp_destination = temp.pop();
            temp_flight[i++] = flight_map.get(city.get(temp_source)).get(city.get(temp_destination)).peek();
            temp_source = temp_destination;
        }
        return temp_flight;
    }


    /**
     * Dijkstra's Shortest Path Algorithm
     * pre: graph to be searched is a weighted directed graph with only positive weight
     *      pred and dist are arrays of size V
     * @param graph The weighted graph to be searched
     * @param start The start vertex
     * @param pred Output array to contain the predecessors in the shortest path
     * @param dist Output array to contain the distance in the shortest path
     */
    private static void dijkstraAlgorithm(Graph graph, int start, int[] pred, double[] dist) {
        int numV = graph.getNumV();
        HashSet<Integer> vMinusS = new HashSet<>(numV);
        //Initialize V - S
        for(int i = 0; i < numV; i++){
            if(i != start)
                vMinusS.add(i);
        }
        // Initialize pred and dist
        for(int v : vMinusS){
            pred[v] = start;
            dist[v] = graph.getEdge(start, v).getWeight();
        }
        //Main loop
        while(vMinusS.size() != 0){
            //Find the value u in V - S with the smallest dist[u]
            double minDist = Double.POSITIVE_INFINITY;
            int u = -1;
            for(int v : vMinusS){
                if(dist[v] < minDist){
                    minDist = dist[v];
                    u = v;
                }
            }
            // Remove u from vMinusS
            vMinusS.remove(u);
            //Update the distances
            Iterator<Edge> edgeIter = graph.edgeIterator(u);
            while(edgeIter.hasNext()){
                Edge edge = edgeIter.next();
                int v = edge.getDest();
                if(vMinusS.contains(v)){
                    double weight = edge.getWeight();
                    if(dist[u] + weight < dist[v]){
                        dist[v] = dist[u] + weight;
                        pred[v] = u;
                    }
                }
            }
        }
    }
}
