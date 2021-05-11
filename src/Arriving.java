/*
  Group 3 members:
  Patrick Mertes, 1368734
  Nhat Tran, 1373869
  Philipp Flügger, 1361053
 */

class Arriving extends Event {

    /**
     * initiates values for Arriving
     * @param timeStamp time at which an event takes place
     * @param carIdentNo ID of the car
     * @param noPeopleInCar number of people in the car
     */
    Arriving(int timeStamp, int carIdentNo, int noPeopleInCar) {
        super(timeStamp, carIdentNo, noPeopleInCar);
    }

    /**
     * processes the actions for Arriving events and creates Testing events
     * checks if the queue is full
     * @param simp the current simulation object
     */
    @Override
    void processEvent(Simulation simp) {
        if (simp.carsInSystem < simp.queueCapacity) {
            simp.carsInSystem++;
            int timeUntilTesting = (int) (Math.random() * (121 - 60)) + 60;
            int testingStartTime = (this.getTimeStamp() + timeUntilTesting);
            simp.dwellTime.put(this.getCarIdentNo(), this.getTimeStamp());
            Testing testing = new Testing(testingStartTime, this.getCarIdentNo(), this.getNoPeopleInCar());
            simp.removeEvent(this);
            simp.addEvent(testing);
            if (simp.log) System.out.print(this.toString() + ", Event type = Arriving");
        } else {
            simp.leaveBecauseFull++;
            simp.carsInSystem++;
            Leaving leaving = new Leaving(this.getTimeStamp(), this.getCarIdentNo(), this.getNoPeopleInCar());
            simp.removeEvent(this);
            simp.addEvent(leaving);
            if (simp.log) System.out.print(this.toString() + ", Event type = Arriving");
        }
    }

}
