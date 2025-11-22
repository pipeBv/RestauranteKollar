package org.example.entidades;

public class Presupuesto {
    private double presupuesto;
    private double perdidas;
    private double ganancias;

    public Presupuesto(double presupuesto, double perdidas, double ganancias) {
        this.presupuesto = presupuesto;
        this.perdidas = perdidas;
        this.ganancias = ganancias;
    }
    public double getPresupuesto() {
        return presupuesto;
    }
    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }
    public double getGanancias() {
        return ganancias;
    }
    public void setGanancias(double ganancias) {
        this.ganancias = ganancias;
    }
   public double getPerdidas() {
        return perdidas;
   }
   public void setPerdidas(double perdidas) {
        this.perdidas = perdidas;
   }

}
