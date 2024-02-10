package Satelite.SO.MEM;

import Satelite.SO.SistemaGPS;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GPSBuffer {
    private boolean propulsorFora;
    private boolean propulsorDentro;
    private final SistemaGPS realTimeLocation;
    private final SistemaGPS gps;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public GPSBuffer(SistemaGPS gps) {
        propulsorFora = false;
        propulsorDentro = false;
        realTimeLocation = new SistemaGPS();
        this.gps = gps;
        new Propulsor().start();
        new WriteRealTimeLocationBuffer().start();
    }

    public SistemaGPS getRealTimeLocation() {
        readWriteLock.readLock().lock();
        try {
            return realTimeLocation;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

//    Atualiiza o estado do propulsor no buffer
    public void setPropulsorFora() {
        if (propulsorDentro) propulsorDentro = false;
        propulsorFora= !propulsorFora;
    }

    public void setPropulsorDentro() {
        if (propulsorFora) propulsorFora = false;
        propulsorDentro = !propulsorDentro;
    }

//    Vai buscar a posição ao hardware(Sensor de GPS)
//    e guarda no buffer para ser acedida pelo sistema Operativo
    public class WriteRealTimeLocationBuffer extends Thread {
        @Override
        public void run() {
            while (true) {
                readWriteLock.writeLock().lock();
                try {
                    realTimeLocation.setXY(gps.getX(), gps.getY());
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        }
    }

//Verifica o estado dos propulsores no buffer e atualiza no Hardware(Sistema GPS)
    class Propulsor extends Thread {
        @Override
        public void run() {
            while (true) {
                if (propulsorDentro) {
                    if (gps.getRadius() >= 3) gps.setRadius(gps.getRadius()-1);
                } else if (propulsorFora) {
                    gps.setRadius(gps.getRadius()+1);
                }
//                Este print é para debug, o sleep serve para não haver flood na consola
                System.out.println("Raio: " + gps.getRadius());
                try {
                    sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
