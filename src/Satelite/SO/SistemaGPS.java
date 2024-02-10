package Satelite.SO;

import Satelite.SO.MEM.GPSBuffer;

import javax.swing.*;

public class SistemaGPS {
    private int x, y;

    private double radius; // raio do movimento circular
    private double angle; // ângulo em radianos



    public SistemaGPS(double radius, double initialAngle) {
        this.radius = radius;
        this.angle = initialAngle;

        new UpdateLocation().start();
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveCircular(double deltaAngle) {
        // Atualiza as coordenadas com base no movimento circular
        this.angle += deltaAngle;
        this.x = (int) (radius * Math.cos(angle));
        this.y = (int) (radius * Math.sin(angle));
    }

    public SistemaGPS() {
        this.radius = 1.0; // valor padrão do raio
        this.angle = 0.0; // ângulo inicial
        this.x = 0;
        this.y = 0;
    }

    public SistemaGPS(SistemaGPS sistemaGPS) {
        this.radius = sistemaGPS.radius;
        this.angle = sistemaGPS.angle;
        this.x = sistemaGPS.x;
        this.y = sistemaGPS.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "SistemaGPS{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

//    Simula o movimente circular do satélite
    class UpdateLocation extends Thread {
        private Timer timer;

        @Override
        public void run() {
            timer = new Timer(100, e -> {
                moveCircular(Math.toRadians(1));
            });
            timer.start();
        }
    }



}
