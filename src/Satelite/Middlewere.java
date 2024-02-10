package Satelite;

import Satelite.SO.Kernel;
import Satelite.SO.SistemaGPS;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

public class Middlewere {
    Kernel kernel;
    private boolean autoSave;
    private boolean autoWriteToFile;
    private final BlockingQueue<SistemaGPS> getLocationResponse;
    private final BlockingQueue<Deque<SistemaGPS>> getHistoryResponse;

    public Middlewere() {
        getHistoryResponse = new LinkedBlockingQueue<>();
        getLocationResponse = new LinkedBlockingQueue<>();
        this.kernel = new Kernel();
        autoSave = true;
        autoWriteToFile = true;
        new AutoSavePosition().start();
        new GetLoactionResponses().start();
        new GetHistoryResponses().start();
    }
//    Adicionar tarefas ao buffer de Mensagens
    public void addTasks(String task, int priority) {
        InsertToBuffer insertToBuffer = new InsertToBuffer(task, priority);
        Thread thread = new Thread(insertToBuffer);
        thread.start();
    }

//    Receber localiçaão atual
    public SistemaGPS getLocation() {
        SistemaGPS response = null;
        try {
            addTasks("GET", 2); //adiciona a mensagaem ao buffer de mensagens
            while (getLocationResponse.isEmpty()) { //Espera pela resposta
                sleep(1);
//                System.out.println("Waiting for response");
            }
            response = getLocationResponse.remove();

        } catch (InterruptedException ignored) {}
        return response;
    }

//    Guardar localização atual na memoria (historico de localizações)
    public void saveLocation() {
        addTasks("SAVE", 3);
    }

    //receber historico de localizações
    public Deque<SistemaGPS> getHistory() {
        Deque<SistemaGPS> response = null;
        try {
            addTasks("GET_HISTORY", 2);// adiciona ao buffer de mensagens
            while (getHistoryResponse.isEmpty()) { //Esperar resposta
                sleep(1); //espera 1ms ate ter resposta
//                System.out.println("Waiting for response");
            }
            response = getHistoryResponse.remove();

        } catch (InterruptedException ignored) {}
        return response; //Depois de obter resposta retorna a mesma
    }

//    Escrever Logs
    public void writeToFile() {
        addTasks("WRITE_TO_FILE", 5);
    }

//    Ligar/Desligar propulsores
    public void setPropulsorOn(String propulsor) {
        switch (propulsor) {
            case "FORA":
                addTasks("PROPULSORFORA", 6);
                break;
            case "DENTRO":
                addTasks("PROPULSORDENTRO", 6);
                break;
        }
    }


    public void setAutoSaveOff() {
        autoSave = !autoSave;
        System.out.println("AutoSave: " + autoSave);
    }

    public void setAutoWriteToFile() {
        autoWriteToFile = !autoWriteToFile;
        System.out.println("AutoWriteToFile: " + autoWriteToFile);
    }

   //a cada chamada cria Threads que vão escrever no buffer de mensagens
    class InsertToBuffer implements Runnable {
        String task;
        int priority;
        boolean addedToBuffer = false;

        public InsertToBuffer(String task, int priority) {
            this.task = task;
            this.priority = priority;
        }

        @Override
        public void run() {
            while (!addedToBuffer) {
                kernel.getKBufferKernelMiddleware().insert(task, priority);
                addedToBuffer = true;
            }
        }
    }
    //thread para guardar as posicoes automaticamente do satelite
    //e escreve logs a cada 500ms
    class AutoSavePosition extends Thread {
        @Override
        public void run() {
            while (true) {
                if (autoSave) {
                    saveLocation();
                }
                if (autoWriteToFile) {
                    writeToFile();
                }
                try {
                    sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }


    //vai ao buffer de respontas e obtem as localizacoes
    class GetLoactionResponses extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    while (!kernel.getBufferResponses().getLocationResponse.isEmpty()) {
                        getLocationResponse.add(kernel.getBufferResponses().removeGetLocationResponse());
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    //thread que vai ler do buffer de respostas e vai obter as localizacoes
    class GetHistoryResponses extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    while (!kernel.getBufferResponses().getHistoryResponse.isEmpty()) {
                        getHistoryResponse.add(kernel.getBufferResponses().removeGetHistoryResponse());
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}