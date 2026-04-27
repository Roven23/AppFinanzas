package Servicio;

import Modelo.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class FinanzasService {

    public static void mostrarMenu() {
        System.out.println("\n========= GESTION FINANCIERA =========");
        System.out.println("1. Ver Saldos de Cuentas");
        System.out.println("2. Crear Nueva Cuenta");
        System.out.println("3. Registrar Movimiento (Ingreso/Egreso)");
        System.out.println("4. Ver Historial General");
        System.out.println("5. Configurar Dia de Pago");
        System.out.println("6. Ver Dashboard del Ciclo");
        System.out.println("7. Transferencias (Metas de Ahorro)");
        System.out.println("8. Exportar Reporte a Excel");
        System.out.println("0. Salir y Guardar");
        System.out.println("======================================");
        System.out.print("Seleccione una opcion: ");
    }
    
    private static double leerMontoSeguro(Scanner teclado) {
        while (true) {
            try {
                String entrada = teclado.nextLine().replace(',', '.').trim();
                double monto = Double.parseDouble(entrada);
                if (monto < 0) {
                    System.out.print("Error: El monto no puede ser negativo. Ingrese nuevamente: ");
                    continue;
                } return monto;    
            } catch (NumberFormatException e) {
                System.out.print("Error: Ingrese un valor numerico. Ingrese nuevamente: ");
            }
        }
    }

    public static ArrayList<Categoria> cargarCategoriasIniciales() {
        ArrayList<Categoria> lista = new ArrayList<>();
        lista.add(new Categoria("SUELDO", true));
        lista.add(new Categoria("COMIDA", false));
        lista.add(new Categoria("TRANSPORTE", false));
        lista.add(new Categoria("SERVICIOS", false));
        return lista;
    }

    public static void crearCuenta(ArrayList<Cuenta> cuentas, Scanner teclado) {

        System.out.print("Ingrese el nombre de la cuenta: ");
        String nombre = teclado.nextLine().trim();

        if(nombre.isEmpty()) {
            System.out.println("Error: El nombre de la cuenta no puede estar vacio.");
            return;
        }

        for (Cuenta c: cuentas ){
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                System.out.println("Error: Ya existe una cuenta con ese nombre.");
                return;
            } 
        }
        
        System.out.print("Saldo inicial: ");
        double saldo = leerMontoSeguro(teclado);
        cuentas.add(new Cuenta(nombre, saldo));
        System.out.println("Cuenta " + nombre +" creada exitosamente.");
    }

    public static void mostrarSaldos(ArrayList<Cuenta> cuentas) {
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas registradas.");
            return;
        }
        System.out.println("\n--- ESTADO DE CUENTAS ---");
        for (Cuenta c : cuentas) {
            System.out.printf("- %20s: $%.2f\n", c.getNombre(), c.getSaldo());
        }
    }

    public static void registrarMovimiento(ArrayList<Cuenta> cuentas, ArrayList<Categoria> categorias, ArrayList<transaccion> historial, Scanner teclado) {
        
        if (cuentas.isEmpty()) {
            System.out.println("Error: Cree una cuenta primero.");
            return;
        }

        Cuenta cuentaSel = null;
        Categoria catSel = null;

        //SELECCION DE CUENTA
        while(true) {
                try {
                    System.out.println("\n--- SELECCIONE CUENTA ---");

                    for (int i = 0; i < cuentas.size(); i++) {
                        System.out.printf("%d. %s ($%.2f)\n", (i + 1), cuentas.get(i).getNombre(), cuentas.get(i).getSaldo());
                    }
                    int idxCuenta = Integer.parseInt(teclado.nextLine()) - 1;

                    if(idxCuenta < 0 || idxCuenta >= cuentas.size()) {
                        System.out.println("Error: Seleccione una cuenta valida.");
                    }else{
                        cuentaSel = cuentas.get(idxCuenta);
                    break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Ingrese un valor numerico ");
                }
        }
        while(true) {
            try{    
                System.out.println("\n--- SELECCIONE CATEGORIA ---");
        //menu
                for (int i = 0; i < categorias.size(); i++) {
                    System.out.println((i + 1) + ". " + categorias.get(i).getNombre());}
                    System.out.println((categorias.size() + 1) + ". [ + Crear nueva ]");
                    int idxCat = Integer.parseInt(teclado.nextLine()) - 1;
        
        //valiacion
                    if(idxCat < 0 || idxCat > categorias.size()) {
                        System.out.println("Error: Seleccione una categoria valida.");
                        continue;
                    }
            
                    if (idxCat == categorias.size()) {
                        System.out.print("Nombre nueva categoria: ");
                        String n = teclado.nextLine().trim();
                        
                        if(n.isEmpty()) {
                            System.out.println("Error: El nombre de la categoria no puede estar vacio.");
                            continue;
                        }
                    boolean existe = false;
                    for (Categoria c: categorias ){
                        if (c.getNombre().equalsIgnoreCase(n)) {
                            existe = true;
                            break;
                        }       
                    }
                    if (existe) {
                        System.out.println("Error: Ya existe una categoria con ese nombre, cree una diferente o seleccione una existente.");
                        continue;   
                    }
                
                    boolean t = false;
                    while(true){
                        System.out.print("Tipo de movimiento, 1: Ingreso, 2: Egreso: ");
                        String entrada = teclado.nextLine().trim();
                            

                            if (entrada.equals("1") || entrada.equals("2")) {
                                t = entrada.equals("1");
                                break;
                            } else {
                                System.out.println("Error: Ingrese '1' o '2'.");
                            }
                            
                            
                        }

                    catSel = new Categoria(n, t);
                    categorias.add(catSel); 
                    break;
                    } else {
                        catSel = categorias.get(idxCat);
                        break;
                    } 
                } catch (NumberFormatException e) {
                    System.out.println("Error: Ingrese un valor numerico ");
                    }
            }
        
        System.out.print("Monto: ");
        double monto = leerMontoSeguro(teclado);
        if(monto > cuentaSel.getSaldo() && !catSel.isEsIngreso()) {
            System.out.printf("Error: Saldo insuficiente en la cuenta seleccionada solo tiene $%.2f.\n", cuentaSel.getSaldo());
            System.out.println("Intente registrar un monto menor o cambie a una categoria de ingreso.");
            return;
        }

        System.out.print("Descripcion (Opcional): ");
        String desc = teclado.nextLine();

        cuentaSel.actualizarSaldo(monto, catSel.isEsIngreso());
        String descFull = "[" + cuentaSel.getNombre() + "] " + desc;
        historial.add(new transaccion(monto, descFull, catSel.getNombre(), catSel.isEsIngreso(),cuentaSel.getNombre()));
        System.out.printf("Movimiento de $%.2f registrado en la cuenta %s.\n", monto , cuentaSel.getNombre());
        
    }

    public static void mostrarHistorialGeneral(ArrayList<transaccion> historial) {
        if (historial.isEmpty()) {
            System.out.println("Historial vacio.");
            return;
        }
        System.out.println("\n--- HISTORIAL DE TRANSACCIONES ---");
        System.out.printf("%-12s | %-15s | %-30s | %-10s\n", "FECHA", "CATEGORIA", "DESCRIPCION", "MONTO");
        System.out.println("---------------------------------------------------------------------------");
        
        for (transaccion t : historial) {
            String signo = t.isEsIngreso() ? "+" : "-";
            String montoFormateado = String.format("%s$%.2f", signo, t.getMonto());
            System.out.printf("%-12s | %-15s | %-30s | %10s\n",
             
                t.getFechaHora().toLocalDate().toString(), 
                t.getCategoria(), 
                t.getDescripcion(), 
                montoFormateado);

        }
    }

    public static void realizarTransferencia(ArrayList<Cuenta> cuentas, ArrayList<transaccion> historial, Scanner teclado) {

    if (cuentas.size() < 2) {
        System.out.println("Necesita al menos 2 cuentas para transferir.");
        return;
    }

    boolean procesoTerminado = false;
    int des = -1;
    int or = -1;

    while (!procesoTerminado) {
        while (true) {
            try {
                System.out.println("\n--- ORIGEN DEL DINERO ---");
                for (int i = 0; i < cuentas.size(); i++) {
                    System.out.printf("%d. %s (Saldo: $%.2f)\n", (i + 1), cuentas.get(i).getNombre(), cuentas.get(i).getSaldo());
                }
                System.out.println("0. Cancelar transferencia");
                System.out.print("Seleccione cuenta de origen: ");
                int seleccion = Integer.parseInt(teclado.nextLine()) - 1;

                if (seleccion == -1) {
                    System.out.println("Transferencia cancelada.");
                    return;
                }

                if (seleccion >= 0 && seleccion < cuentas.size()) {
                    if (cuentas.get(seleccion).getSaldo() <= 0) {
                        System.out.println("Error: La cuenta seleccionada no tiene saldo disponible.");
                        continue;
                    }
                    or = seleccion;
                    break;
                }
                System.out.println("Error: Seleccione una cuenta valida.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numerico.");
            }
        }

        while (true) {
            try {
                System.out.println("\n--- DESTINO DEL DINERO ---");
                for (int i = 0; i < cuentas.size(); i++) {
                    if (i == or) continue;
                    System.out.printf("%d. %s (Saldo: $%.2f)\n", (i + 1), cuentas.get(i).getNombre(), cuentas.get(i).getSaldo());
                }
                System.out.println("0. <--- Cambiar cuenta de origen");
                System.out.print("Seleccione cuenta de destino: ");
                int destino = Integer.parseInt(teclado.nextLine()) - 1;

                if (destino == -1) {
                    System.out.println("Volviendo a seleccionar cuenta de origen...");
                    or = -1; 
                    break; 
                }

                if (destino >= 0 && destino < cuentas.size() && destino != or) {
                    des = destino;
                    procesoTerminado = true; 
                    break;
                }
                System.out.println("Cuenta no valida.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numerico.");
            }
        }
    } 

    while (true) {
        System.out.print("Monto a transferir: ");
        double m = leerMontoSeguro(teclado);

        if (m <= cuentas.get(or).getSaldo()) {
            cuentas.get(or).actualizarSaldo(m, false);
            cuentas.get(des).actualizarSaldo(m, true);

            String d = "Transferencia: " + cuentas.get(or).getNombre() + " -> " + cuentas.get(des).getNombre();
            historial.add(new transaccion(m, d, "TRANSFERENCIA", false,cuentas.get(or).getNombre()));

            System.out.printf("Transferencia de $%.2f realizada con exito.\n", m);
            break; 
        } else {
            System.out.printf("Error: Saldo insuficiente. Solo tiene $%.2f.", cuentas.get(or).getSaldo());
            System.out.println(" Ingrese un monto menor.");
            // Aquí podrías agregar una opción para cancelar si el usuario se queda sin opciones
        }
    }
}
   
    public static int configurarDiaPago(Scanner teclado) {
        java.time.YearMonth mesActual = java.time.YearMonth.now();
        int diasMax = mesActual.lengthOfMonth();
        int dia = -1;
        while (true) {
            try{
                System.out.print("Ingrese dia de pago (1-" + diasMax + "): ");
                dia = Integer.parseInt(teclado.nextLine());

                if (dia >= 1 && dia <= diasMax) {
                    return dia;
                }
                System.out.println("Error: El dia debe estar entre 1 y " + diasMax);
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un numero valido para el dia.");
            }
        }
            
    }
        
    public static LocalDateTime obtenerInicioCiclo(int diaPago) {
        LocalDateTime ahora = LocalDateTime.now();
        java.time.YearMonth mesAct = java.time.YearMonth.now();
        int diaAju = Math.min(diaPago, mesAct.lengthOfMonth());
        
        if (ahora.getDayOfMonth() >= diaAju) {
            return ahora.withDayOfMonth(diaAju).withHour(0).withMinute(0).withSecond(0);
        } else {
            java.time.YearMonth mesPas = mesAct.minusMonths(1);
            int diaAjuPas = Math.min(diaPago, mesPas.lengthOfMonth());
            return ahora.minusMonths(1).withDayOfMonth(diaAjuPas).withHour(0).withMinute(0).withSecond(0);
        }
    }

    public static void mostrarResumenCiclo(LocalDateTime inicio, ArrayList<transaccion> historial, ArrayList<Cuenta> misCuentas) {
        LocalDateTime fin = inicio.plusMonths(1);
        double ingresosMes = 0, egresosMes = 0, sueldoDet = 0, saldoAhorros = 0, deudaAhorro = 0;
        Map<String, Double> gastosCat = new HashMap<>();

        for (Cuenta c : misCuentas) {
            if (c.getNombre().toUpperCase().contains("AHORRO")) saldoAhorros += c.getSaldo();
        }

        for (transaccion t : historial) {
            if (!t.getFechaHora().isBefore(inicio) && t.getFechaHora().isBefore(fin)) {
                if (t.getCategoria().equalsIgnoreCase("SUELDO") && t.isEsIngreso()) sueldoDet += t.getMonto();
                
                if (t.getCategoria().equalsIgnoreCase("TRANSFERENCIA")) {
                    if (t.getDescripcion().toUpperCase().contains("DE AHORRO")) deudaAhorro += t.getMonto();
                    continue;
                }

                if (t.isEsIngreso()) {
                    ingresosMes += t.getMonto();
                } else {
                    egresosMes += t.getMonto();
                    gastosCat.put(t.getCategoria(), gastosCat.getOrDefault(t.getCategoria(), 0.0) + t.getMonto());
                }
            }
        }

        System.out.println("\n=========================================");
        System.out.println("   DASHBOARD FINANCIERO DEL CICLO ACTUAL");
        System.out.println("   Periodo: " + inicio.toLocalDate() + " al " + fin.toLocalDate().minusDays(1));
        System.out.println("=========================================");
        System.out.printf(" SUELDO DETECTADO:     $%.2f\n", sueldoDet);
        System.out.printf(" (+) Otros Ingresos:   $%.2f\n", (ingresosMes - sueldoDet));
        System.out.printf(" (-) Gastos del Mes:   $%.2f\n", egresosMes);
        System.out.printf(" (=) Sobrante Ciclo:   $%.2f\n", (ingresosMes - egresosMes));
        System.out.println(" ---------------------------------------");
        System.out.printf(" SALDO EN AHORROS:     $%.2f\n", saldoAhorros);
        if (deudaAhorro > 0) System.out.printf(" DEUDA POR REPONER:   -$%.2f\n", deudaAhorro);
        System.out.printf(" PATRIMONIO TOTAL:     $%.2f\n", (saldoAhorros + (ingresosMes - egresosMes)));
        System.out.println(" ---------------------------------------");

        if (sueldoDet > 0) {
            System.out.println("\n DESGLOSE DE GASTOS (% DEL SUELDO):");
            for (Map.Entry<String, Double> entry : gastosCat.entrySet()) {
                double porc = (entry.getValue() / sueldoDet) * 100;
                String barra = "";
                for (int i = 0; i < (int)(porc/2); i++) barra += "#";
                System.out.printf("%-15s [%-20s] %3.1f%% ($%.2f)\n", entry.getKey(), barra, porc, entry.getValue());
            }
        }
        System.out.println("=========================================\n");
    }

    public static void guardarDatos(ArrayList<Cuenta> cuentas, ArrayList<Categoria> categorias, ArrayList<transaccion> historial, int diaPago) {
    // Usamos try-with-resources para el cierre automático (Blindaje de Memoria)
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream("datos_finanzas.dat"))) {
        
            oos.writeObject(cuentas);      // 1. Cuentas
            oos.writeObject(categorias);   // 2. Categorías
            oos.writeObject(historial);    // 3. Historial
            oos.writeInt(diaPago);         // 4. El día de pago que configuró el usuario

            
        } catch (java.io.IOException e) {
            System.out.println("[Error Crítico] No se pudo escribir en el disco: " + e.getMessage());
        }
    }

    public static int cargarDatos(ArrayList<Cuenta> cuentas, ArrayList<Categoria> categorias, ArrayList<transaccion> historial) {
    int diaPagoRecuperado = 1;
    java.io.File archivo = new java.io.File("datos_finanzas.dat");

    if (!archivo.exists()) return 1;

    try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(archivo))) {
        
        // El orden debe ser IDENTICO al de guardado
        cuentas.addAll((ArrayList<Cuenta>) ois.readObject());
        categorias.addAll((ArrayList<Categoria>) ois.readObject());
        historial.addAll((ArrayList<transaccion>) ois.readObject());
        diaPagoRecuperado = ois.readInt();

        System.out.println("[Sistema] Memoria cargada: " + historial.size() + " movimientos detectados.");
    } catch (Exception e) {
        System.out.println("[Advertencia] Error de sincronización al cargar datos.");
    }
    return diaPagoRecuperado;
}

    public static void exportarExcel(ArrayList<transaccion> historial) {
    try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File("Reporte_Finanzas.csv"))) {
        // Encabezados con columna de Cuenta
        writer.println("Fecha;Hora;Descripcion;Monto;Tipo;Categoria;Cuenta");

        for (transaccion t : historial) {
            writer.print(t.getFechaHora().toLocalDate() + ";");
            writer.print(t.getFechaHora().toLocalTime().toString().substring(0, 5) + ";");
            writer.print(t.getDescripcion() + ";");
            writer.print(String.format(java.util.Locale.US, "%.2f", t.getMonto()) + ";");
            writer.print((t.isEsIngreso() ? "INGRESO" : "EGRESO") + ";");
            writer.print(t.getCategoria() + ";");
            writer.println(t.getNombreCuenta()); // Ahora sí funcionará sin errores
        }
        System.out.println("[Sistema] Reporte generado con éxito.");
    } catch (java.io.IOException e) {
        System.out.println("[Error] Error: " + e.getMessage());
    }
}

}