
import Modelo.*;
import Servicio.FinanzasService;
import java.util.Scanner;
import java.util.ArrayList;

public class AppFinanzas {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        ArrayList<Cuenta> misCuentas = new ArrayList<>();
        ArrayList<transaccion> historial = new ArrayList<>();
        ArrayList<Categoria> listaCategorias = new ArrayList<>();

        int diaPago = 27;
        int diaPagoGuardado = FinanzasService.cargarDatos(misCuentas, listaCategorias, historial); 
        if (diaPagoGuardado != 27) { // Si el día de pago guardado es diferente al valor por defecto, lo usamos 
            diaPago = diaPagoGuardado;
        }

        if(listaCategorias.isEmpty()){
            System.out.println("No se encontraron categorias guardadas. Se cargaran categorias basicas por defecto.");
            listaCategorias = FinanzasService.cargarCategoriasIniciales();
        }

        
        
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
                        FinanzasService.guardarDatos(misCuentas, listaCategorias, historial, diaPago);
                        break;

                    case 3:
                        FinanzasService.registrarMovimiento(misCuentas, listaCategorias, historial, teclado);
                        FinanzasService.guardarDatos(misCuentas, listaCategorias, historial, diaPago);
                        break;

                    case 4:
                        FinanzasService.mostrarHistorialGeneral(historial);
                        FinanzasService.guardarDatos(misCuentas, listaCategorias, historial, diaPago);
                        break;

                    case 5:
                        diaPago = FinanzasService.configurarDiaPago(teclado);
                        // Limpiamos el buffer despues de configurarDiaPago
                        teclado.nextLine(); 
                        FinanzasService.guardarDatos(misCuentas, listaCategorias, historial, diaPago);
                        break;

                    case 6:
                        FinanzasService.mostrarResumenCiclo(
                            FinanzasService.obtenerInicioCiclo(diaPago), 
                            historial, 
                            misCuentas
                        );
                        FinanzasService.guardarDatos(misCuentas, listaCategorias, historial, diaPago);
                        break;

                    case 7:
                        FinanzasService.realizarTransferencia(misCuentas, historial, teclado);
                        FinanzasService.guardarDatos(misCuentas, listaCategorias, historial, diaPago);
                        break;

                    case 0:
                        System.out.println("Guardando cambios y cerrando sistema...");
                        FinanzasService.guardarDatos(misCuentas, listaCategorias, historial, diaPago);
                        break;
                    
                    case 8:
                        FinanzasService.exportarExcel(historial);
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