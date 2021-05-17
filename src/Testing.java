/*
  Group 3 members:
  Patrick Mertes, 1368734
  Nhat Tran, 1373869
  Philipp Flügger, 1361053
 */

class Testing extends Event {

    Server server = null;
    int arrivalTime;

    /**
     * initiates values for Testing
     * @param timeStamp time at which an event takes place
     * @param carIdentNo ID of the car
     * @param noPeopleInCar number of people in the car
     */
    Testing(int timeStamp, int carIdentNo, int noPeopleInCar, Simulation simp) {
        super(timeStamp, carIdentNo, noPeopleInCar, simp);
    }

    /**
     *
     * processes the actions for Testing events and creates Leaving events
     * @param simp the current simulation object
     */
    @Override
    void processEvent(Simulation simp) {
        int timeToLeave = this.getNoPeopleInCar() * 120;
        Leaving leaving = new Leaving(this.getTimeStamp() + timeToLeave, this.getCarIdentNo(), this.getNoPeopleInCar(), simp);
        leaving.server = server;
        simp.waitingTimes.put(this.getCarIdentNo(), this.getTimeStamp() - simp.waitingTimes.get(this.getCarIdentNo()));
        simp.processTimes.put(this.getCarIdentNo(), this.getTimeStamp());

        simp.removeEvent(this);
        simp.addEvent(leaving);
        if (simp.log) System.out.println(this.toString() + ", People in car: " + getNoPeopleInCar() + ", Event type = Testing, Server: " +  server.id);
    }
}
