/*
  Group 3 members:
  Patrick Mertes, 1368734
  Nhat Tran, 1373869
  Philipp Flügger, 1361053
 */

class Leaving extends Event {

    Server server = null;

    /**
     * initiates values for Leaving
     * @param timeStamp time at which an event takes place
     * @param carIdentNo ID of the car
     * @param noPeopleInCar number of people in the car
     */
    Leaving(int timeStamp, int carIdentNo, int noPeopleInCar) {
        super(timeStamp, carIdentNo, noPeopleInCar);
    }

    /**
     * processes the actions for Leaving events and reduces the number of cars in system
     * @param simp the current simulation object
     */
    @Override
    void processEvent(Simulation simp) {
        server.blocked = false;
        simp.removeEvent(this);
        if (simp.log) System.out.println(this.toString() + ", Event type = Leaving" + ", Server: " +  server.id);
        simp.carsInSystem--;
    }
}
