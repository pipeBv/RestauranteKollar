package org.example.entidades;

public class Usuario extends Entidad{
    private String contraseña;

    public Usuario(int id,String nombre,String contraseña) {
        super(id, nombre);
        this.contraseña = contraseña;
    }

    public String getContraseña() {
        return contraseña;
    }
    public String getNombre() {
        return getNombre();
    }
}
