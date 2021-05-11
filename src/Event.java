/*
  Group 3 members:
  Patrick Mertes, 1368734
  Nhat Tran, 1373869
  Philipp Flügger, 1361053
 */

/**
 * abstract class from which the other events inherit
 */
abstract class Event {

    private int timeStamp;
    private int carIdentNo;
    private int NoPeopleInCar;

    abstract void processEvent(Simulation simulation);

    /**
     * initiates values for Event
     * @param timeStamp time at which an event takes place
     * @param carIdentNo ID of the car
     * @param noPeopleInCar number of people in the car
     */
    Event(int timeStamp, int carIdentNo, int noPeopleInCar) {
        this.timeStamp = timeStamp;
        this.carIdentNo = carIdentNo;
        this.NoPeopleInCar = noPeopleInCar;
    }

    /**
     * various getter methods to get different values
     */

    int getTimeStamp() {
        return timeStamp;
    }

    int getCarIdentNo() {
        return carIdentNo;
    }

    int getNoPeopleInCar() {
        return NoPeopleInCar;
    }

    public void setTimeStamp(int timeStamp) { this.timeStamp = timeStamp; }

    @Override
    public String toString() {
        return "{" +
                "Time stamp = " + timeStamp +
                ", Car ID = " + carIdentNo;
    }
}
