package org.example;

public class Reporte {
    public static void menuReporte(){
        Presupuesto Presupuesto = new Presupuesto(1000000, 0, 0);
        System.out.println("Reporte general");
        System.out.println("Reporte de presupuesto: ");
        System.out.println("Reporte de ganancias: "+Presupuesto.getGanancias());
        System.out.println("Reporte de perdidas: "+Presupuesto.getPerdidas());
        System.out.println("Reporte de presupuesto total: "+Presupuesto.calcularResultado());
        System.out.println("...");
        System.out.println("Reporte de ");




    }
}
