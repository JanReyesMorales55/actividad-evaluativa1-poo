import java.util.Scanner;

// Definimos la clase Servidor sin public porque solo puede haber una clase public 
class Servidor {
    // Atributos del servidor. No uso private porque todavía no hemos dado encapsulación
    String nombre;
    String origen;
    String critico;
    int intentosTotales;
    int intentosFallidos;

    // Método para validar si los datos ingresados tienen sentido
    boolean datosValidos() {
        // El nombre debe tener algún carácter que no sea espacio
        if (nombre == null || nombre.trim().length() == 0) {
            return false;
        }

        // Limpio de espacios y paso a minúsculas para comparar bien
        String orig = origen.trim().toLowerCase();
        if (!orig.equals("interno") && !orig.equals("externo")) {
            return false;
        }

        String crit = critico.trim().toLowerCase();
        if (!crit.equals("si") && !crit.equals("no")) {
            return false;
        }

        // Los totales deben ser mayor a cero
        if (intentosTotales <= 0) {
            return false;
        }

        // Los fallos deben estar entre cero y el total
        if (intentosFallidos < 0 || intentosFallidos > intentosTotales) {
            return false;
        }

        return true; // Si pasa todo, los datos son válidos
    }

    // Método para determinar el riesgo
    String evaluarEstado() {
        if (!datosValidos()) {
            return "DATOS INVALIDOS";
        }

        String orig = origen.trim().toLowerCase();
        String crit = critico.trim().toLowerCase();

        // 1. BLOQUEADO
        if (intentosFallidos >= 5 || (crit.equals("si") && orig.equals("externo") && intentosFallidos >= 3)) {
            return "BLOQUEADO";
        }
        // 2. ALERTA (Uso else if porque BLOQUEADO tiene prioridad)
        else if (intentosFallidos == 3 || intentosFallidos == 4 || (orig.equals("externo") && intentosFallidos >= 2)) {
            return "ALERTA";
        }
        // 3. NORMAL
        else {
            return "NORMAL";
        }
    }

    // Calcula el porcentaje convirtiendo a double para que la división sea exacta
    double porcentajeFallos() {
        if (datosValidos()) {
            return (intentosFallidos * 100.0) / intentosTotales;
        }
        return 0.0;
    }

    // Muestra los datos por pantalla
    void mostrarResumen() {
        String estado = evaluarEstado();
        
        if (estado.equals("DATOS INVALIDOS")) {
            System.out.printf("%s | N/D | %s\n", nombre.trim().toUpperCase(), estado);
        } else {
            // Uso .1f para un decimal y %% para pintar el simbolo de porcentaje
            System.out.printf("%s | %d/%d | %.1f%% | %s\n", nombre.trim().toUpperCase(), intentosFallidos, intentosTotales, porcentajeFallos(), estado);
        }
    }
}

public class Main {
    
    // Método auxiliar para darle un valor numérico a la prioridad y que sea más fácil comparar
    static int prioridad(String estado) {
        if (estado.equals("BLOQUEADO")) {
            return 3;
        } else if (estado.equals("ALERTA")) {
            return 2;
        } else if (estado.equals("NORMAL")) {
            return 1;
        }
        return 0; // Por si son inválidos
    }

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        // CREAR Y LEER SERVIDOR 1 
        // Instancio usando el constructor por defecto porque no nos dejan crear personalizados
        Servidor s1 = new Servidor(); 
        
        System.out.println("Introduce los datos del primer servidor:");
        System.out.print("Nombre: ");
        s1.nombre = teclado.nextLine();
        System.out.print("Origen (interno/externo): ");
        s1.origen = teclado.nextLine();
        System.out.print("Critico (si/no): ");
        s1.critico = teclado.nextLine();
        System.out.print("Intentos totales: ");
        s1.intentosTotales = teclado.nextInt();
        System.out.print("Intentos fallidos: ");
        s1.intentosFallidos = teclado.nextInt();

        // Limpio el buffer del Scanner que se queda colgado tras leer un int
        teclado.nextLine();

        // CREAR Y LEER SERVIDOR 2
        Servidor s2 = new Servidor();
        
        System.out.println("\nIntroduce los datos del segundo servidor:");
        System.out.print("Nombre: ");
        s2.nombre = teclado.nextLine();
        System.out.print("Origen (interno/externo): ");
        s2.origen = teclado.nextLine();
        System.out.print("Critico (si/no): ");
        s2.critico = teclado.nextLine();
        System.out.print("Intentos totales: ");
        s2.intentosTotales = teclado.nextInt();
        System.out.print("Intentos fallidos: ");
        s2.intentosFallidos = teclado.nextInt();

        // REPORTE Y SALIDA 
        System.out.println("\n==== REPORTE DE SEGURIDAD ====");
        s1.mostrarResumen();
        s2.mostrarResumen();

        String estadoS1 = s1.evaluarEstado();
        String estadoS2 = s2.evaluarEstado();

        System.out.print("\nATENCION PRIORITARIA: ");
        
        // Si alguno es inválido, no se pueden comparar
        if (estadoS1.equals("DATOS INVALIDOS") || estadoS2.equals("DATOS INVALIDOS")) {
            System.out.println("COMPARACION NO DISPONIBLE");
        } else {
            int p1 = prioridad(estadoS1);
            int p2 = prioridad(estadoS2);

            if (p1 > p2) {
                System.out.println(s1.nombre.trim().toUpperCase());
            } else if (p2 > p1) {
                System.out.println(s2.nombre.trim().toUpperCase());
            } else {
                System.out.println("IGUAL PRIORIDAD");
            }
        }
        
        teclado.close();
    }
}

/* 
 * RESPUESTAS CONCEPTUALES
 * 1. ¿Por qué dos objetos de la misma clase pueden almacenar valores diferentes en sus atributos?
 * Respuesta: Porque la clase funciona como un molde. Al usar new Servidor(), el programa reserva un 
 * espacio de memoria independiente para cada servidor. Como son instancias separadas en 
 * la memoria, cambiar el atributo de un objeto no afecta a los del otro.
 * 
 * 2. ¿Qué diferencia existe entre Servidor b = a; y Servidor b = new Servidor();?
 * Respuesta: Servidor b = a; no crea un objeto nuevo; hace que la variable b apunte exactamente 
 * al mismo objeto en la memoria que a (si modificas b, modificas a). Por otro lado, 
 * Servidor b = new Servidor(); crea un objeto completamente nuevo e independiente de a en la memoria.
 * 
 * 3. ¿Por qué es importante comprobar datosValidos() antes de calcular el porcentaje?
 * Respuesta: Porque si los datosTotales son 0, al hacer la división estaríamos 
 * intentando dividir por cero, lo que provocaría un error grave y pararía el programa o 
 * generaría porcentajes imposibles.
 */
