
import Modelo.*;
import Servicio.FinanzasService;
import java.util.Scanner;
import java.util.ArrayList;

public class AppFinanzas {
    public static void main(String[] args) {
    
        Scanner teclado = new Scanner(System.in);
        ArrayList<Cuenta> misCuentas = new ArrayList<>();
        ArrayList<transaccion> historial = new ArrayList<>();
        ArrayList<Categoria> listaCategorias = FinanzasService.cargarCategoriasIniciales();
        int diaPago = 27; 
        int opcion = -1;

        do {
            try {
                FinanzasService.mostrarMenu();
                String entrada = teclado.nextLine();
                opcion = Integer.parseInt(entrada);

                switch (opcion) {
                    case 1:
                        FinanzasService.mostrarSaldos(misCuentas);
                        break;

                    case 2:
                        FinanzasService.crearCuenta(misCuentas, teclado);
                        break;

                    case 3:
                        FinanzasService.registrarMovimiento(misCuentas, listaCategorias, historial, teclado);
                        break;

                    case 4:
                        FinanzasService.mostrarHistorialGeneral(historial);
                        break;

                    case 5:
                        diaPago = FinanzasService.configurarDiaPago(teclado);
                        // Limpiamos el buffer despues de configurarDiaPago
                        teclado.nextLine(); 
                        break;

                    case 6:
                        FinanzasService.mostrarResumenCiclo(
                            FinanzasService.obtenerInicioCiclo(diaPago), 
                            historial, 
                            misCuentas
                        );
                        break;

                    case 7:
                        FinanzasService.realizarTransferencia(misCuentas, historial, teclado);
                        break;

                    case 0:
                        System.out.println("Guardando cambios y cerrando sistema...");
                        // Aqui se llamara a la persistencia en el siguiente paso
                        break;

                    default:
                        System.out.println("Opcion no valida. Intente de nuevo.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor ingrese solo numeros para las opciones y montos.");
            } catch (Exception e) {
                System.out.println("Ocurrio un error inesperado: " + e.getMessage());
                System.out.println("Recuperando sistema... Por favor intente de nuevo.");
            }

        } while (opcion != 0);

        teclado.close();
        System.out.println("App finalizada con exito.");
    }
}