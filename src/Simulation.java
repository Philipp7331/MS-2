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
                //TODO
                //carArrivalTime = ((int) (Math.random() * (121 - 60)) + 60) + systemTime;
                if (carArrivalTime <= getMaxTime()) {
                    int peopleInNextCar = (int) (Math.random() * (7 - 1)) + 1;
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
         *
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
        /*System.out.println("New Loop:");
        for (int i = 0; i <  eventList.size(); i++) {
            System.out.println(eventList.get(i).toString());
        }*/
    }

    /**
     * removes an event from the eventlist
     *
     * @param event of type Arriving, Testing or Leaving
     */
    void removeEvent(Event event) {
        eventList.remove(event);
    }

    /**
     * writes list to a csv file
     *
     * @param dataname name of the csv file
     * @param list     list of Integers
     */
    public static void writeList(String dataname, ArrayList<Integer> list) {
        try (BufferedWriter nbf = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(dataname, true)))) {

            int counter = 0;
            for (int i : list) {
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
     *
     * @param dataname name of the csv file
     * @param map      map of Integers
     */
    public static void writeMap(String dataname, HashMap<Integer, Integer> map) {
        try (BufferedWriter nbf = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(dataname, true)))) {

            int counter = 0;
            for (Map.Entry<Integer, Integer> time : map.entrySet()) {
                nbf.write((map.size() > counter) ? time.getValue() + "," : time.getValue() + "");
                counter++;
                nbf.newLine();
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    private int avgTime(HashMap<Integer, Integer> map) {
        int sum = 0;
        for (int i : map.values()) {
            sum += i;
        }
        //map.size();
        //System.out.println("SUM = " + sum + ", SIZE = " + map.size());
        return sum / map.size();
    }

    /**
     * creates Simulation objects and runs them
     * calculates different values for task 1.3
     *
     * @param args arguments for the program
     */
    public static void main(String[] args) {

        //Simulation simp = new Simulation(true, "FIFO");
        //simp.run();

        /*
        simp.waitingTimes.forEach((k, v) -> {
            System.out.println("ID: " + k + ", waiting time: " + v);
        });

        System.out.println("Process Time:");
        simp.processTimes.forEach((k, v) -> {
            System.out.println("ID: " + k + ", process time: " + v);
        });

        for(Map.Entry<Integer, Integer> entry : simp.waitingTimes.entrySet()) {
            int key = entry.getKey();
            simp.dwellTimes.put(key, simp.waitingTimes.get(key) + simp.processTimes.get(key));
        }
         */

        // print the average waiting, process and dwell time
        // TODO when waiting time is > 1, the dwell time is wait+process+1 and not wait+process
        //System.out.println(simp.avgTime(simp.waitingTimes));
        //System.out.println(simp.avgTime(simp.processTimes));
        //System.out.println(simp.avgTime(simp.dwellTimes));


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

        // case FIFO
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


        // case FIFO
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


        // case FIFO
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


        // case FIFO
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