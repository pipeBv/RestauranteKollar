package org.example.entidades;

public class Empleado extends Usuario{
    private String rolEmpleado;
    public Empleado(int id, String nombre, String contraseña,String rolEmpleado){
        super(id, nombre, contraseña);
        this.rolEmpleado = rolEmpleado;
    }
}
