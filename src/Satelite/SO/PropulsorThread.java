package Satelite.SO;

import Satelite.SO.MEM.GPSBuffer;

public class PropulsorThread extends Thread {
    private String propulsor;
    private GPSBuffer buffer;

    public PropulsorThread(GPSBuffer buffer, String propulsor) {
        this.buffer = buffer;
        this.propulsor = propulsor;
    }

//    Escreve no buffer do sistema GPS se o estado dos propulsores
    @Override
    public void run() {
        switch (propulsor) {
            case "DENTRO":
                buffer.setPropulsorDentro();
                break;
            case "FORA":
                buffer.setPropulsorFora();
                break;
        }
    }
}
