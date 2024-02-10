import GUI.SimuladorGPS;
import Satelite.Middlewere;

import javax.swing.*;

public class MainInterface {
    public static void main(String[] args) {
//        Executar com interface
        Middlewere middlawere = new Middlewere();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
//                new SimuladorGPS(middlawere).setVisible(true);
                new SimuladorGPS(middlawere).setVisible(true);
            }
        });

    }
}
