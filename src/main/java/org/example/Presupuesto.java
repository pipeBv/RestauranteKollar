package org.example;

public class Presupuesto {
    double presupuesto;
    double perdidas;
    double ganancias;

    public Presupuesto(double presupuesto, double perdidas, double ganancias) {
        this.presupuesto = presupuesto;
        this.perdidas = perdidas;
        this.ganancias = ganancias;
    }
    public double getPresupuesto() {
        return presupuesto;
    }
    public double getGanancias() {
        return ganancias;
    }
   public double getPerdidas() {
        return perdidas;
   }
    public double calcularResultado() {
        presupuesto =-perdidas;
        System.out.println(presupuesto=-perdidas);
        presupuesto =+ganancias;
        System.out.println(presupuesto+=ganancias);
        return presupuesto;
    }
}
