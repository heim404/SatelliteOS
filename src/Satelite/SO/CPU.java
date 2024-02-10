package Satelite.SO;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CPU {
    private BlockingQueue<Thread> cache;

    public CPU() {
        cache = new LinkedBlockingQueue<>();
        new CheckCache().start();
    }

    public BlockingQueue<Thread> getCache() {
        return cache;
    }

//    Verifica se existe tarefas na cache e executa-as
    public class CheckCache extends Thread {
        @Override
        public void run() {
            while (true) {
                if (!cache.isEmpty()) {
                    Thread thread;
                    thread = cache.remove();
                    thread.start();
                }
            }
        }
    }
}