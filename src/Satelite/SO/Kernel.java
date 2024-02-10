package Satelite.SO;

import Satelite.SO.MEM.BufferKernelMiddlewere;
import Satelite.SO.MEM.BufferResponses;
import Satelite.SO.MEM.GPSBuffer;
import Satelite.SO.MEM.MEM;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;

public class Kernel {
    private final BufferKernelMiddlewere bufferKernelMiddlewere;
    private final BufferResponses bufferResponses;
    private final CPU cpu;
    private final MEM memory;
    private final SistemaGPS gps;
    private GPSBuffer realTimeLocationBuffer;

    public Kernel() {
        cpu = new CPU();
        memory = new MEM();
        bufferKernelMiddlewere = new BufferKernelMiddlewere();
        bufferResponses = new BufferResponses();
        gps = new SistemaGPS(80.0, Math.PI / 4);
        realTimeLocationBuffer = new GPSBuffer(gps);
        new GetFromBuffer().start();
        new GetRealTimeLocation().start();

    }

    public synchronized SistemaGPS getGps() {
        return memory.getCurrentLocation();
    }

    public MEM getMemory() {
        return memory;
    }

    public BufferKernelMiddlewere getKBufferKernelMiddleware() {
        return bufferKernelMiddlewere;
    }

    public BufferResponses getBufferResponses() {
        return bufferResponses;
    }

    public void writeToFileThread() {
        new WriteToFileThread(memory.getHistory()).start();
    }


    class WriteToFileThread extends Thread {
        private Deque<SistemaGPS> gpsHistory;
        private String fileName;

        public WriteToFileThread(Deque<SistemaGPS> gpsHistory) {
            this.gpsHistory = gpsHistory;
            fileName = "logs.txt";
        }

        @Override
        public void run() { //escreve coordenadas no ficheiro logs.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                for (SistemaGPS sistemaGPS : gpsHistory) {
                    String gpsData = getCurrentDateTime() + " - " + sistemaGPS.toString();
                    writer.write(gpsData);
                    writer.newLine();
                }
                System.out.println("Data appended to file: " + fileName);
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }

        private String getCurrentDateTime() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(new Date());
        }
    }

    class GetFromBuffer extends Thread {
        String task;

        //Tradução das mensagens presentes no buffer para tarefas a adicionar ao cache
        private void addToCache() {
            switch (task) {
                case "SAVE": //guardar no historico
                    cpu.getCache().add(new LocationThread(Kernel.this, "SAVE"));
                    break;
                case "GET"://obtem a localizacao atual
                    cpu.getCache().add(new LocationThread(Kernel.this, "GET"));
                    break;
                case "GET_HISTORY": //obtem o historico
                    cpu.getCache().add(new LocationThread(Kernel.this, "GET_HISTORY"));
                    break;
                case "WRITE_TO_FILE": //escreve logs
                    cpu.getCache().add(new LocationThread(Kernel.this, "WRITE_TO_FILE"));
                    break;
                case "PROPULSORFORA": //ligar e desliga os propulsores fora
                    cpu.getCache().add(new PropulsorThread( realTimeLocationBuffer, "FORA"));
                    break;
                case "PROPULSORDENTRO"://ligar e desliga os propulsores dentro
                    cpu.getCache().add(new PropulsorThread(realTimeLocationBuffer, "DENTRO"));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + task);
            }
        }

//        Leitura e traducao de mensagens e escreve na cache do CPU
        @Override
        public void run() {
            while (true) {
                getKBufferKernelMiddleware().waitForTasks();
                while (!getKBufferKernelMiddleware().heap.isEmpty()) {
                    task = getKBufferKernelMiddleware().extractMax();
                    addToCache();
                }
            }
        }
    }
//    Leitura do buffer de GPS e Escreve no buffer the memoria
    class GetRealTimeLocation extends Thread {
        @Override
        public void run() {
            while (true) {
                memory.setCurrentLocation(realTimeLocationBuffer.getRealTimeLocation());
            }
        }
    }
}
