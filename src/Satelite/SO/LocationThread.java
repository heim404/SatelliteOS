package Satelite.SO;

import java.util.Deque;

public class LocationThread extends Thread {
    private static int id = 1;
    private Kernel kernel;
    private static SistemaGPS currentLocation;
    private static Deque<SistemaGPS> history;
    private String task;

    private boolean addedToBuffer = false;

    public LocationThread(Kernel kernel, String task) {
        this.kernel = kernel;
        this.task = task;
        switch (this.task) {
            case "SAVE":
                this.setName("SaveLocationThread-" + id);
                break;
            case "GET":
                this.setName("GetLocationThread-" + id);
                break;
            case "GET_HISTORY":
                this.setName("GetHistoryLocationThread-" + id);
                break;
            case "WRITE_TO_FILE":
                this.setName("WriteToFile-" + id);
                break;
            default:
                setName("Unexpected task: " + this.task);
        }
        ++id;
    }

    //comportamento da threads
    public void run() {
        while (!addedToBuffer) {
            switch (this.task) {
                case "SAVE":
                    this.saveLocation();
                    break;
                case "GET":
                    this.getLocation();
                    kernel.getBufferResponses().addLoaction(currentLocation); //Adicionar resposta ao buffer
                    break;
                case "GET_HISTORY":
                    this.getLocationsHistory();
                    kernel.getBufferResponses().addHistory(history); //Adicionar resposta ao buffer
                    break;
                case "WRITE_TO_FILE":
                    this.writeToFile();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + this.task);
            }
            addedToBuffer = true;
        }
    }

    private void writeToFile() {
        kernel.writeToFileThread();
    }

    private void getLocationsHistory() {
        history = kernel.getMemory().getHistory();
    }


    private void saveLocation() {
        currentLocation = new SistemaGPS(kernel.getGps());
        System.out.println("Saved Current Location: " + currentLocation);
        kernel.getMemory().saveLocation(currentLocation);
    }

    public void getLocation() {
        currentLocation = kernel.getGps();
    }

}