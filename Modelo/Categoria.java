package Modelo;
import java.io.Serializable;

public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L; // Para la serialización
    private String nombre;
    private boolean esIngreso; // true si es categoría de ingreso, false si es de gasto

    public Categoria(String nombre, boolean esIngreso) {
        this.nombre = nombre;
        this.esIngreso = esIngreso;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isEsIngreso() {
        return esIngreso;
    }

    @Override
    public String toString() {  
        return String.format("%s (%s)", nombre, esIngreso ? "Ingreso" : "Gasto");
    }
}
