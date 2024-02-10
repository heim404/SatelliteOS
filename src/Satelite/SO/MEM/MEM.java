package Satelite.SO.MEM;

import Satelite.SO.SistemaGPS;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MEM {
    private final ReadWriteLock readWriteLock;
    private Deque<SistemaGPS> gpsHistory;
    private SistemaGPS currentLocation;

    public MEM() {
        readWriteLock = new ReentrantReadWriteLock();
        gpsHistory = new LinkedBlockingDeque<>();
    }

//    Guarda a localização no histórico
    public synchronized void saveLocation(SistemaGPS gpsLocation) {
        gpsHistory.addFirst(gpsLocation);
    }
//    Retorna o histórico de localizações
    public Deque<SistemaGPS> getHistory() {
        return new LinkedList<>(gpsHistory);
    }
//    Retorna a localização atual
    public SistemaGPS getCurrentLocation() {
        readWriteLock.readLock().lock();
        try {
            return currentLocation;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

//    Atualiza a localização atual
    public void setCurrentLocation(SistemaGPS currentLocation) {
        this.currentLocation = currentLocation;
    }
}
