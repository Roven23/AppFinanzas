import Modelo.transaccion;
import java.util.Scanner;
import java.util.ArrayList; // Para guardar todos los registros

public class AppFinanzas {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        ArrayList<transaccion> listaTransacciones = new ArrayList<>();
        String respuesta;

        System.out.println("--- BIENVENIDO A TU APP DE FINANZAS ---");

        do {
            // 1. Ingreso de datos
            System.out.print("\nIngrese la cantidad ($): ");
            double monto = teclado.nextDouble();
            teclado.nextLine(); // Limpia buffer

            System.out.print("Ingrese descripción: ");
            String desc = teclado.nextLine();

            System.out.print("¿Es un ingreso? (true/false): ");
            boolean esIngreso = teclado.nextBoolean();
            teclado.nextLine(); // Limpiar buffer después del boolean

            // 2. Crear y guardar en la lista
            transaccion nuevaTransaccion = new transaccion(monto, desc, esIngreso);
            listaTransacciones.add(nuevaTransaccion);
            
            System.out.println("Registrado correctamente.");

            // 3. Preguntar si desea continuar
            System.out.print("\n¿Desea ingresar otro movimiento? (si/no): ");
            respuesta = teclado.nextLine().toLowerCase();

        } while (respuesta.equals("si"));

        // 4. Mostrar resumen final de lo ingresado en la sesión
        System.out.println("\n--- RESUMEN DE LA SESIO20N ---");
        for (transaccion t : listaTransacciones) {
            System.out.println(t.toString());
        }

        System.out.println("\n¡Gracias por usar la app!");
        teclado.close();
    }
}