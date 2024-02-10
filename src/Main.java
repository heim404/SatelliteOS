import Satelite.Middlewere;

public class Main {
    public static void main(String[] args) {
//        Executar sem interface
        Middlewere middlewere = new Middlewere();


        Thread thread2 = new Thread(() -> {
            while (true) {
                System.out.println("Get Location " + middlewere.getLocation());
            }
        });


        Thread thread3 = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    middlewere.writeToFile();
                    System.out.println("History: " + middlewere.getHistory());

                } catch (InterruptedException ignored) {
                }
            }
        });

        middlewere.setAutoWriteToFile();

        Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(5000);
                middlewere.setAutoSaveOff();
            } catch (InterruptedException ignored) {
            }
        });
        Thread thread6 = new Thread(() -> {
            try {
                Thread.sleep(500);
                middlewere.setAutoSaveOff();

            } catch (InterruptedException ignored) {
            }
        });

        Thread thread4 = new Thread(() -> {
           try {
               Thread.sleep(2000);
               middlewere.setPropulsorOn("FORA");

           } catch (InterruptedException ignored){}
        });

        Thread thread5 = new Thread(() -> {
            try {
                Thread.sleep(6000);
                middlewere.setPropulsorOn("FORA");

            } catch (InterruptedException ignored){}
        });

        thread1.start();
        thread6.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

//        middlewere.setAutoSaveOff();



    }
}