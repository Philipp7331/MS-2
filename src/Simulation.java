/*
  Group 3 members:
  Patrick Mertes, 1368734
  Nhat Tran, 1373869
  Philipp Flügger, 1361053
 */


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class Simulation {

    private LinkedList<Event> eventList;
    private int systemTime;
    private int carArrivalTime;
    private int nextCarArrivalTime;
    private int maxTime;
    int carsInSystem;
    private ArrayList<Integer> peopleInCar;
    private ArrayList<Integer> peopleInLane;
    int leaveBecauseFull;
    int queueCapacity;
    boolean log;
    private LinkedList<Server> serverList;
    private int numberOfServers;

    HashMap<Integer, Integer> waitingTime = new HashMap<>();

    /**
     * initiates values for simulation
     * @param log whether or not a log should be printed to the console
     */
    private Simulation(boolean log) {
        this.eventList = new LinkedList<>();
        this.systemTime = 0;
        this.carArrivalTime = 0;
        this.nextCarArrivalTime = 0;
        this.maxTime = 7200;
        this.carsInSystem = 0;
        this.peopleInCar = new ArrayList<>();
        this.peopleInLane = new ArrayList<>();
        this.leaveBecauseFull = 0;
        this.queueCapacity = 10;
        this.log = log;
        this.numberOfServers = 3;
        this.serverList = new LinkedList<>();
    }

    /**
     * Creates servers for simulation run
     */
    private void createServers() {
        for (int i = 0; i < this.numberOfServers ; i++) serverList.add(new Server(false));
    }

    /**
     * Returns server which are free
     * @return list of free servers
     */
    private LinkedList<Server> freeServers() {
        LinkedList<Server> freeServerList = new LinkedList<>();
        for (Server s : serverList) {
            if(!s.blocked) {
                freeServerList.add(s);
            }
        }
        return freeServerList;
    }


    /**
     * runs the simulation while eventlist is not empty or simulation time is lower than maxTime (7200)
     * creates Arriving objects and adds them to the eventlist
     * starts the processing of events
     * creates log if this.log is true
     */
    private void run() {
        createServers();

        int currentId = 0;
        while (!eventList.isEmpty() || systemTime <= maxTime) {
            if (systemTime <= maxTime && nextCarArrivalTime == systemTime) {
                carArrivalTime = ((int) (Math.random() * (181 - 120)) + 120) + systemTime;
                if (carArrivalTime <= getMaxTime()) {
                    int peopleInNextCar = (int) (Math.random() * (4 - 1)) + 1;
                    peopleInCar.add(peopleInNextCar);
                    peopleInLane.add(carsInSystem);
                    Arriving arriving = new Arriving(carArrivalTime, ++currentId, peopleInNextCar);
                    addEvent(arriving);
                    nextCarArrivalTime = carArrivalTime;
                }
            }

            if (!eventList.isEmpty() && !freeServers().isEmpty()) {
                LinkedList<Server> servers = freeServers();
                for (int i = 0; i < eventList.size(); i++) {
                    if(eventList.get(i) instanceof Testing) {
                        ((Testing) eventList.get(i)).server = servers.getFirst();
                        servers.remove(servers.getFirst());
                        ((Testing) eventList.get(i)).server.blocked = true;
                        eventList.get(i).setTimeStamp(systemTime);
                        eventList.get(i).processEvent(this);
                    }
                    if(servers.isEmpty()) break;
                }
            }

            if (!eventList.isEmpty() && eventList.getFirst().getTimeStamp() == systemTime) {
                int length = eventList.size();
                for (int i = 0; i < length; i++) {
                    if (eventList.getFirst().getTimeStamp() == systemTime) {
                        eventList.getFirst().processEvent(this);
                    }
                }
            }

            systemTime++;
        }
    }


    /**
     * gets max time
     * @return max simulation time
     */
    private int getMaxTime() {
        return maxTime;
    }


    static class timeComparator implements Comparator<Event> {

        /**
         * compares two events and returns true if they are in order
         * @param e1 first event
         * @param e2 second event
         * @return boolean
         */
        @Override
        public int compare(Event e1, Event e2) {
            return Integer.compare(e1.getTimeStamp(), e2.getTimeStamp());
        }
    }

    /**
     * adds an event to the eventlist and sorts the eventlist based on timestamp
     * @param event of type Arriving, Testing or Leaving
     */
    void addEvent(Event event) {
        eventList.add(event);
        eventList.sort(new timeComparator());
    }

    /**
     * removes an event from the eventlist
     * @param event of type Arriving, Testing or Leaving
     */
    void removeEvent(Event event) {
        eventList.remove(event);
    }

    /**
     * writes list to a csv file
     * @param dataname name of the csv file
     * @param list list of Integers
     */
    public static void writeList(String dataname, ArrayList<Integer> list) {
        try (BufferedWriter nbf = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(dataname, true)))) {

            int counter = 0;
            for (int i:list) {
                nbf.write((list.size() > counter) ? i + "," : i + "");
                counter++;
                nbf.newLine();
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }

    }

    /**
     * writes hash map to a csv file
     * @param dataname name of the csv file
     * @param map map of Integers
     */
    public static void writeMap(String dataname, HashMap<Integer, Integer> map){
        try (BufferedWriter nbf = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(dataname, true)))) {

            int counter = 0;
            for (Map.Entry<Integer,Integer> time: map.entrySet()) {
                nbf.write((map.size() > counter) ? time.getValue() + "," : time.getValue() + "");
                counter++;
                nbf.newLine();
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }


    /**
     * creates Simulation objects and runs them
     * calculates different values for task 1.3
     * @param args arguments for the program
     */
    public static void main(String[] args) {

        Simulation simp = new Simulation(true);
        simp.run();
/*
        double averagePeopleInCar = simp.peopleInCar
                .stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics()
                .getAverage();
        System.out.println("Task 1.3");
        System.out.println("Average people in one car: " + averagePeopleInCar);

        double averageCarsInTestingLane = simp.peopleInLane
                .stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics()
                .getAverage();
        System.out.println("Average cars in testing lane: " + averageCarsInTestingLane);

        System.out.println("Leaving because of full queue: " + simp.leaveBecauseFull);
*/
/*
        HashMap<Integer, Double> averageLeavings = new HashMap<>();
        for (int i = 10; i < 20; i += 2) {
            ArrayList<Integer> averageLeaving = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                Simulation s = new Simulation(false);
                s.queueCapacity = i;
                s.run();
                averageLeaving.add(s.leaveBecauseFull);
            }
            averageLeavings.put(i, averageLeaving.stream()
                    .mapToInt(Integer::intValue)
                    .summaryStatistics()
                    .getAverage());
        }

        System.out.println("increase for 12: " + averageLeavings.get(10) / averageLeavings.get(12) * 100 + " %");
        System.out.println("increase for 14: " + averageLeavings.get(10) / averageLeavings.get(14) * 100 + " %");
        System.out.println("increase for 16: " + averageLeavings.get(10) / averageLeavings.get(16) * 100 + " %");
        System.out.println("increase for 18: " + averageLeavings.get(10) / averageLeavings.get(18) * 100 + " %");
*/
        //writeList("vehiclesOverTime.csv", simp.peopleInLane);
        //writeMap("dwellTime.csv", simp.dwellTime);

    }

}