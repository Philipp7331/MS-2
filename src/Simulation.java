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
    boolean log;
    private LinkedList<Server> serverList;
    private int numberOfServers;
    HashMap<Integer, Integer> waitingTimes;
    HashMap<Integer, Integer> processTimes;
    private HashMap<Integer, Integer> dwellTimes;
    private String queuePrinciple;

    /**
     * initiates values for simulation
     *
     * @param log whether or not a log should be printed to the console
     */
    private Simulation(boolean log, String queuePrinciple) {
        this.eventList = new LinkedList<>();
        this.systemTime = 0;
        this.carArrivalTime = 0;
        this.nextCarArrivalTime = 0;
        this.maxTime = 7200;
        this.carsInSystem = 0;
        this.log = log;
        this.numberOfServers = 3;
        this.serverList = new LinkedList<>();
        this.waitingTimes = new HashMap<>();
        this.processTimes = new HashMap<>();
        this.dwellTimes = new HashMap<>();
        this.queuePrinciple = queuePrinciple;
    }

    /**
     * Creates servers for simulation run
     */
    private void createServers() {
        for (int i = 0; i < this.numberOfServers; i++) serverList.add(new Server(false));
    }

    /**
     * Returns server which are free
     *
     * @return list of free servers
     */
    private LinkedList<Server> freeServers() {
        LinkedList<Server> freeServerList = new LinkedList<>();
        for (Server s : serverList) {
            if (!s.blocked) {
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
                    // change this value to get relevant output for task 2.1.3
                    int peopleInNextCar = (int) (Math.random() * (4 - 1)) + 1;
                    Arriving arriving = new Arriving(carArrivalTime, ++currentId, peopleInNextCar, this);
                    addEvent(arriving);
                    nextCarArrivalTime = carArrivalTime;
                }
            }

            // execute testing events if a server is available
            if (!eventList.isEmpty() && !freeServers().isEmpty()) {
                LinkedList<Server> servers = freeServers();
                for (int i = 0; i < eventList.size(); i++) {
                    if (eventList.get(i) instanceof Testing) {
                        ((Testing) eventList.get(i)).server = servers.getFirst();
                        servers.remove(servers.getFirst());
                        ((Testing) eventList.get(i)).server.blocked = true;
                        eventList.get(i).setTimeStamp(systemTime);
                        eventList.get(i).processEvent(this);
                    }
                    if (servers.isEmpty()) break;
                }
            }

            // execute Arriving and Leaving events
            if (!eventList.isEmpty()) {
                for (int i = 0; i < eventList.size(); i++) {
                    if (eventList.get(i).getTimeStamp() == systemTime && !(eventList.get(i) instanceof Testing)) {
                        eventList.get(i).processEvent(this);
                        i -= 1;
                    }
                }
            }

            systemTime++;
        }
    }


    /**
     * gets max time
     *
     * @return max simulation time
     */
    private int getMaxTime() {
        return maxTime;
    }

    static class timeComparator implements Comparator<Event> {

        /**
         * compares two events and returns true if they are in order
         * differentiates between different queuing principles with switch case
         * @param e1 first event
         * @param e2 second event
         * @return boolean
         */
        @Override
        public int compare(Event e1, Event e2) {
            Simulation simp = e1.simp;
            switch (simp.queuePrinciple) {
                case "FIFO":
                    return Integer.compare(e1.getTimeStamp(), e2.getTimeStamp());
                case "LIFO":
                    return Integer.compare(e2.getTimeStamp(), e1.getTimeStamp());
                case "SPT":
                    return Integer.compare(e1.getNoPeopleInCar(), e2.getNoPeopleInCar());
                case "LPT":
                    return Integer.compare(e2.getNoPeopleInCar(), e1.getNoPeopleInCar());
            }
            return 0;
        }
    }

    /**
     * adds an event to the eventlist and sorts the eventlist based on timestamp
     *
     * @param event of type Arriving, Testing or Leaving
     */
    void addEvent(Event event) {

        eventList.add(event);
        eventList.sort(new timeComparator());
    }

    /**
     * removes an event from the eventlist
     *
     * @param event of type Arriving, Testing or Leaving
     */
    void removeEvent(Event event) {
        eventList.remove(event);
    }


    private int avgTime(HashMap<Integer, Integer> map) {
        int sum = 0;
        for (int i : map.values()) {
            sum += i;
        }
        return sum / map.size();
    }

    /**
     * creates Simulation objects and runs them
     * calculates different values for task 1.3
     *
     * @param args arguments for the program
     */
    public static void main(String[] args) {

        // change log to true if output needed
        Simulation simp = new Simulation(false, "FIFO");
        simp.run();

        // case FIFO
        System.out.println("Change peopleInNextCar to get relevant results");
        System.out.println("-------------CASE FIFO-------------");
        ArrayList<Integer> fifoWaitingTimes = new ArrayList<>();
        ArrayList<Integer> fifoProcessingTimes = new ArrayList<>();
        ArrayList<Integer> fifoDwellTimes = new ArrayList<>();
        for (int j = 0; j < 1000; j++) {
            Simulation s = new Simulation(false, "FIFO");
            s.run();
            fifoWaitingTimes.add(s.avgTime(s.waitingTimes));
            fifoProcessingTimes.add(s.avgTime(s.processTimes));
            for(Map.Entry<Integer, Integer> entry : s.waitingTimes.entrySet()) {
                int key = entry.getKey();
                s.dwellTimes.put(key, s.waitingTimes.get(key) + s.processTimes.get(key));
            }
            fifoDwellTimes.add(s.avgTime(s.dwellTimes));
        }
        System.out.println("FIFO average waiting time: " + fifoWaitingTimes.stream()
                                                                .mapToDouble(d -> d)
                                                                .average()
                                                                .orElse(0.0));
        System.out.println("FIFO average processing time: " + fifoProcessingTimes.stream()
                                                                .mapToDouble(d -> d)
                                                                .average()
                                                                .orElse(0.0));
        System.out.println("FIFO average dwell time: " + fifoDwellTimes.stream()
                                                                .mapToDouble(d -> d)
                                                                .average()
                                                                .orElse(0.0));
        System.out.println("-----------END CASE FIFO------------");
        System.out.println();


        // case LIFO
        System.out.println("-------------CASE LIFO-------------");
        ArrayList<Integer> lifoWaitingTimes = new ArrayList<>();
        ArrayList<Integer> lifoProcessingTimes = new ArrayList<>();
        ArrayList<Integer> lifoDwellTimes = new ArrayList<>();
        for (int j = 0; j < 1000; j++) {
            Simulation s = new Simulation(false, "LIFO");
            s.run();
            lifoWaitingTimes.add(s.avgTime(s.waitingTimes));
            lifoProcessingTimes.add(s.avgTime(s.processTimes));
            for(Map.Entry<Integer, Integer> entry : s.waitingTimes.entrySet()) {
                int key = entry.getKey();
                s.dwellTimes.put(key, s.waitingTimes.get(key) + s.processTimes.get(key));
            }
            lifoDwellTimes.add(s.avgTime(s.dwellTimes));
        }
        System.out.println("LIFO average waiting time: " + lifoWaitingTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("LIFO average processing time: " + lifoProcessingTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("LIFO average dwell time: " + lifoDwellTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("-----------END CASE LIFO------------");
        System.out.println();


        // case SPT
        System.out.println("-------------CASE SPT-------------");
        ArrayList<Integer> sptWaitingTimes = new ArrayList<>();
        ArrayList<Integer> sptProcessingTimes = new ArrayList<>();
        ArrayList<Integer> sptDwellTimes = new ArrayList<>();
        for (int j = 0; j < 1000; j++) {
            Simulation s = new Simulation(false, "SPT");
            s.run();
            sptWaitingTimes.add(s.avgTime(s.waitingTimes));
            sptProcessingTimes.add(s.avgTime(s.processTimes));
            for(Map.Entry<Integer, Integer> entry : s.waitingTimes.entrySet()) {
                int key = entry.getKey();
                s.dwellTimes.put(key, s.waitingTimes.get(key) + s.processTimes.get(key));
            }
            sptDwellTimes.add(s.avgTime(s.dwellTimes));
        }
        System.out.println("SPT average waiting time: " + sptWaitingTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("SPT average processing time: " + sptProcessingTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("SPT average dwell time: " + sptDwellTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("-----------END CASE SPT------------");
        System.out.println();


        // case LPT
        System.out.println("-------------CASE LPT-------------");
        ArrayList<Integer> lptWaitingTimes = new ArrayList<>();
        ArrayList<Integer> lptProcessingTimes = new ArrayList<>();
        ArrayList<Integer> lptDwellTimes = new ArrayList<>();
        for (int j = 0; j < 1000; j++) {
            Simulation s = new Simulation(false, "LPT");
            s.run();
            lptWaitingTimes.add(s.avgTime(s.waitingTimes));
            lptProcessingTimes.add(s.avgTime(s.processTimes));
            for(Map.Entry<Integer, Integer> entry : s.waitingTimes.entrySet()) {
                int key = entry.getKey();
                s.dwellTimes.put(key, s.waitingTimes.get(key) + s.processTimes.get(key));
            }
            lptDwellTimes.add(s.avgTime(s.dwellTimes));
        }
        System.out.println("LPT average waiting time: " + lptWaitingTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("LPT average processing time: " + lptProcessingTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("LPT average dwell time: " + lptDwellTimes.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0));
        System.out.println("-----------END CASE LPT------------");

    }

}