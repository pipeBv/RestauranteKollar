package org.example.entidades;

public class Empleado extends Usuario{
    private double sueldo;
    private String rolEmpleado;
    public Empleado(Object id, String nombre, String contraseña,String rolEmpleado, double sueldo){
        super(id, nombre, contraseña);
        this.rolEmpleado = rolEmpleado;
        this.sueldo = sueldo;
    }
    public double getSueldo() {
        return sueldo;
    }
    public void setSueldo(double sueldo) {
        this.sueldo = sueldo;
    }
    public String getRolEmpleado() {
        return rolEmpleado;
    }
    public void setRolEmpleado(String rolEmpleado) {
        this.rolEmpleado = rolEmpleado;
    }
}
