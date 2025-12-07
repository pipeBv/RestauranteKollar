package org.example.entidades;

public class Usuario extends Entidad{
    private String contrasenia;

    public Usuario(Object id,String nombre,String contrasenia) {
        super(id, nombre);
        this.contrasenia = contrasenia;
    }

    public String getContrasenia() {
        return contrasenia;
    }

}
