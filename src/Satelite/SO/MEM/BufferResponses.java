package Satelite.SO.MEM;

import Satelite.SO.SistemaGPS;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BufferResponses {
    public BlockingQueue<SistemaGPS> getLocationResponse;
    public BlockingQueue<Deque<SistemaGPS>> getHistoryResponse;
    private final Object monitor = new Object();

    public BufferResponses() {
        getHistoryResponse = new LinkedBlockingQueue<>();
        getLocationResponse = new LinkedBlockingQueue<>();
    }

    public void addLoaction(SistemaGPS response) {
        synchronized (monitor) {
            getLocationResponse.add(response);
            monitor.notifyAll(); // Notifica todas as threads que á dados no buffer
        }
    }

    public void addHistory(Deque<SistemaGPS> response) {
        synchronized (monitor) {
            getHistoryResponse.add(response);
            monitor.notifyAll(); // Notifica todas as threads que á dados no buffer
        }
    }

    public SistemaGPS removeGetLocationResponse() throws InterruptedException {
        synchronized (monitor) {
            while (getLocationResponse.isEmpty()) {
                monitor.wait(); // Aguarda até que haja uma resposta disponível no buffer
            }
            return getLocationResponse.remove();
        }
    }

    public Deque<SistemaGPS> removeGetHistoryResponse() throws InterruptedException {
        synchronized (monitor) {
            while (getHistoryResponse.isEmpty()) {
                monitor.wait(); // Aguarda até que haja uma resposta disponível no buffer
            }
            return getHistoryResponse.remove();
        }
    }
}
