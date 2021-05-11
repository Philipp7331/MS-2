class Server {

    static int idCounter = 1;

    boolean blocked;
    int id;

    Server(boolean blocked){
        this.blocked = blocked;
        this.id = idCounter++;
    }

}
