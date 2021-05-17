/*
  Group 3 members:
  Patrick Mertes, 1368734
  Nhat Tran, 1373869
  Philipp Flügger, 1361053
 */

class Arriving extends Event {

    /**
     * initiates values for Arriving
     *
     * @param timeStamp     time at which an event takes place
     * @param carIdentNo    ID of the car
     * @param noPeopleInCar number of people in the car
     */
    Arriving(int timeStamp, int carIdentNo, int noPeopleInCar, Simulation simp) {
        super(timeStamp, carIdentNo, noPeopleInCar, simp);
    }

    /**
     * processes the actions for Arriving events and creates Testing events
     * checks if the queue is full
     *
     * @param simp the current simulation object
     */
    @Override
    void processEvent(Simulation simp) {

        simp.carsInSystem++;
        simp.waitingTimes.put(this.getCarIdentNo(), this.getTimeStamp());
        Testing testing = new Testing(this.getTimeStamp(), this.getCarIdentNo(), this.getNoPeopleInCar(), simp);
        simp.removeEvent(this);
        simp.addEvent(testing);
        if (simp.log) System.out.println(this.toString()+ ", People in car: " + getNoPeopleInCar() + ", Event type = Arriving");

    }

}
