/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package practica.teoria;

import Lista.NodoSimple;
import java.util.StringTokenizer;

/**
 *
 * @author Camilo Sampedro, Sergio Loaiza
 */
public class AutomataFinito {

    private boolean[] esFinal;
    private String[][] transiciones;
    private String[] simbolos;
    final static String DELIMITADOR = " ,";

    /**
     * Constructor de un automata finito vacío.
     */
    public AutomataFinito() {
        esFinal = null;
        transiciones = null;
        simbolos = null;
    }

    /**
     * Constructor del automata finito con parámetros dados.
     *
     * @param esFinal Representa qué estados son finales o de aceptación dentro
     * del autómata.
     * @param transiciones Matriz de transiciones (estados por fila y simbolos
     * por columna) representada como índices y puede contener una o más
     * transiciones por campo, o ninguna en su defecto.
     * @param simbolos Arreglo de símbolos de entrada del automata finito.
     */
    public AutomataFinito(boolean[] esFinal, String[][] transiciones, String[] simbolos) {
        this.esFinal = esFinal;
        this.transiciones = transiciones;
        this.simbolos = simbolos;
    }

    /**
     * @return Arreglo que representa cuáles estados son finales o de aceptación
     * dentro del autómata.
     */
    public boolean[] getEsFinal() {
        return esFinal;
    }

    /**
     * @return Matriz de transiciones (estados por fila y simbolos por columna)
     * representada como índices y puede contener una o más transiciones por
     * campo, o ninguna en su defecto.
     */
    public String[][] getTransiciones() {
        return transiciones;
    }

    /**
     * @return Arreglo de símbolos de entrada del automata finito.
     */
    public String[] getSimbolos() {
        return simbolos;
    }

    /**
     * @return Número de estados que tiene el autómata finito.
     */
    public int getNumeroEstados() {
        return transiciones.length;
    }

    /**
     * @return Número de símbolos de entrada que tiene el autómata finito.
     */
    public int getNumeroSimbolos() {
        return transiciones[0].length;
    }

    /**
     *
     * @param esFinal Representa qué estados son finales o de aceptación dentro
     * del autómata.
     * @exception El número de elementos en esFinal debe coincidir con el número
     * de estados actuales del autómata, se debe de aplicar primero
     * setTransiciones.
     * @see practica.teoria.AutomataFinito.setTransiciones(String[][])
     */
    public void setEsFinal(boolean[] esFinal) {
        if (getNumeroEstados() != esFinal.length) {
            this.esFinal = esFinal;
        } else {
            System.err.println("(setEsFinal) ERROR: El número de estados a ingresar no coincide con el número de estados del autómata actual.");
        }
    }

    /**
     *
     * @param transiciones Matriz de transiciones (estados por fila y simbolos
     * por columna) representada como índices y puede contener una o más
     * transiciones por campo, o ninguna en su defecto.
     * @exception Posteriormente se deberá de asignar los finales.
     * @see practica.teoria.AutomataFinito.setTransiciones(String[][])
     */
    public void setTransiciones(String[][] transiciones) {
        this.transiciones = transiciones;
    }

    /**
     *
     * @param simbolos Arreglo de símbolos de entrada del automata finito.
     */
    public void setSimbolos(String[] simbolos) {
        this.simbolos = simbolos;
    }

    /**
     * Verifica si las transiciones tienen correspondencia en los estados.
     *
     * @return true si no existen problemas en la estructura del automata.
     */
    public boolean esValido() {
        try {
            for (int i = 0; i < transiciones.length; i++) {
                for (int j = 0; j < transiciones[i].length; j++) {
                    if (!"".equals(transiciones[i][j])) {
                        StringTokenizer tokenizador = new StringTokenizer(transiciones[i][j], DELIMITADOR);
                        while (tokenizador.hasMoreElements()) {
                            int transicion = Integer.parseInt(tokenizador.nextToken());
                            if (transicion > transiciones.length || transicion <= 0) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
            System.err.println("Error en el formato de número.");
            return false;
        }
        return true;
    }

    /**
     * Minimiza el automata finito.
     *
     * @param automataFinito Automata finito a minimizar.
     * @return automata finito mínimo.
     */
    public AutomataFinito AFMinimo() {
        return convertirAF().minimizar();
    }

    /**
     * Convierte a autómata finito determinista. Siendo un autómata finito no
     * determinista, se convierte a determinista.
     *
     * @return Autómata finito determinista.
     */
    public AutomataFinito convertirAF() {
        int modificaciones;
        int numeroEstados = this.getNumeroEstados();
        int numeroSimbolos = this.getNumeroSimbolos();
        int i, j;
        StringTokenizer transicion;
        Conjunto AFD;

        //El arreglo de cierres lambda contendrá como número de elementos, 
        //el número de estados.
        Conjunto[] cierresLambda = new Conjunto[numeroEstados];

        //Creación de cierres lambda básicos, (Se detiene el ciclo en el numero 
        //de estados por la misma razón).
        for (i = 0; i < numeroEstados; i++) {
            cierresLambda[i] = new Conjunto();
            cierresLambda[i].agregar(Integer.toString(i + 1));
            transicion = new StringTokenizer(transiciones[i][numeroSimbolos - 1], DELIMITADOR);
            while (transicion.hasMoreTokens()) {
                cierresLambda[i].agregar(transicion.nextToken());
            }
        }

        //Creación de cierres lambda completos. Se entra el ciclo siempre y 
        //cuando en el paso anterior se haya hecho alguna modificación.
        do {
            modificaciones = 0;
            for (i = 0; i < numeroEstados; i++) {
                NodoSimple p = cierresLambda[i].getPrimero();
                while (p != null) {
                    j = Integer.parseInt((String) p.retornaDato());
                    cierresLambda[i] = cierresLambda[i].unir(cierresLambda[j - 1]);
                    modificaciones = modificaciones + cierresLambda[i].getModificaciones();
                    p = p.retornaLiga();
                }
            }
        } while (modificaciones != 0);
        AFD = new Conjunto();

        //Completación recursiva de los cierres lambda.
        completarCierresLambda(cierresLambda, cierresLambda[0], numeroSimbolos, AFD);

        //Construyendo el nuevo automata:
        AutomataFinito nuevo = exportarAutomata(AFD, numeroSimbolos);
        return nuevo;
    }

    /**
     * Completa los cierres lambda recursivamente.
     *
     * @param cierresLambda Arreglo de los cierres lambda primitivos.
     * @param conjuntoActual Conjunto de estados a analizar (Estado del
     * determinista)
     * @param numeroSimbolos Numero de símbolos del automata finito que llama.
     * @param AFD Conjunto de estados del automata finita determinista.
     */
    private void completarCierresLambda(Conjunto[] cierresLambda, Conjunto conjuntoActual, int numeroSimbolos, Conjunto AFD) {
        if (existeEstadoEnAFD(AFD, conjuntoActual)) {
            return;
        }

        Conjunto[] transicionesPorEstado = new Conjunto[numeroSimbolos];
        transicionesPorEstado[0] = conjuntoActual;
        AFD.agregar(transicionesPorEstado);

        //Se recorre cada uno de los simbolos por el cierre lambda actual.
        for (int j = 1; j < numeroSimbolos; j++) {
            NodoSimple p = conjuntoActual.getPrimero();
            Conjunto conjunto = new Conjunto();

            //Se recorren cada uno de los componentes del cierre lambda actual,
            //buscando cuáles tienen transiciones no vacías y se unen.
            while (p != null) {
                int k = Integer.parseInt((String) p.retornaDato());
                if (!("".equals(transiciones[k - 1][j - 1]))) {
                    conjunto = conjunto.unir(cierresLambda[Integer.parseInt(transiciones[k - 1][j - 1]) - 1]);
                }
                p = p.retornaLiga();
            }

            //Se agregan solamente las transiciones que no sean vacías, las demás
            //se dejan en nulo.
            if (!conjunto.esVacia()) {
                transicionesPorEstado[j] = conjunto;
                completarCierresLambda(cierresLambda, conjunto, numeroSimbolos, AFD);
            }
        }

    }

    /**
     * Traduce AFD a un autómata finito.
     *
     * @param AFD Conjunto de estados para el nuevo autómata finito
     * determinista.
     * @param numeroSimbolos Número de símbolos del antiguo automata finito.
     * @return Autómata finito construido completamente.
     */
    private AutomataFinito exportarAutomata(Conjunto AFD, int numeroSimbolos) {
        int numeroEstados = AFD.getTamaño();
        int i;
        AutomataFinito nuevo;
        boolean[] esFinalAntiguo = esFinal;

        //Se construyen las transiciones nuevas, al mismo tiempo se verificarán 
        //finales, se verifica que no sea vacío el nuevo autómata:
        if (numeroEstados > 1) {
            transiciones = new String[numeroEstados][numeroSimbolos - 1];
            esFinal = new boolean[numeroEstados];
        } else {
            transiciones = new String[1][numeroSimbolos - 1];
            esFinal = new boolean[1];
        }

        //Se comienzan a validar e insertar transiciones
        NodoSimple p = AFD.getPrimero();
        i = 0;
        int pos;
        while (p != null) {
            Conjunto[] fila = (Conjunto[]) p.retornaDato();
            esFinal[i] = false;

            //Se verifica si es de aceptación:
            cicloEsFinal:
            if (fila[0] != null) {
                NodoSimple q = fila[0].getPrimero();
                while (q != null) {
                    if (esFinalAntiguo[Integer.parseInt((String) q.retornaDato()) - 1]) {
                        esFinal[i] = true;
                        break cicloEsFinal;
                    }
                    q = q.retornaLiga();
                }
            }

            //Se traducen los números de estado a nuevos números de estado:
            for (int j = 1; j < fila.length; j++) {
                if (fila[j] != null) {
                    pos = buscarEstado(AFD, fila[j]);
                    //Si existe dicho elemento se agrega, en caso contrario
                    //se descarta:
                    if (pos != -1) {
                        transiciones[i][j - 1] = Integer.toString(pos + 1);
                    } else {
                        transiciones[i][j - 1] = "";
                    }
                }
            }
            p = p.retornaLiga();
            i++;
        }

        //Nuevos símbolos contendrán todos menos el símbolo lambda:
        String[] simbolosNuevos = new String[numeroSimbolos - 1];
        for (i = 0; i < numeroSimbolos - 1; i++) {
            simbolosNuevos[i] = this.simbolos[i];
        }

        //Se reunen los datos en un nuevo autómata:
        nuevo = new AutomataFinito(esFinal, transiciones, simbolosNuevos);
        return nuevo;
    }

    /**
     * Minimiza el autómata actual.
     *
     * @return Automata finito mínimo.
     */
    public AutomataFinito minimizar() {
        Conjunto particiones = new Conjunto();
        Conjunto part1 = new Conjunto();
        Conjunto part0 = new Conjunto();
        int i, j, k, l;
        AutomataFinito nuevo = new AutomataFinito();

        for (i = 0; i < esFinal.length; i++) {
            if (esFinal[i]) {
                part1.agregar(i);
            } else {
                part0.agregar(i);
            }
        }
        part0.agregar(-1);

        particiones.agregar(part0);
        particiones.agregar(part1);

        NodoSimple p = particiones.getPrimero();

        cicloModificaciones:
        while (p != null) {
            Conjunto particionActual = (Conjunto) p.retornaDato();
            for (j = 0; j < getNumeroSimbolos(); j++) {

                NodoSimple q = particionActual.getPrimero();

                i = (Integer) q.retornaDato();
                if (i != -1) {
                    if ("".equals(transiciones[i][j]) || transiciones[i][j] == null) {
                        k = -1;
                    } else {
                        k = Integer.parseInt(transiciones[i][j]);
                    }
                } else {
                    k = -1;
                }
                int prim = buscarEnParticiones(particiones, k);
                Conjunto huevo1 = new Conjunto();
                Conjunto huevo2 = new Conjunto();

                huevo1.agregar(i);

                q = q.retornaLiga();

                while (q != null) {

                    i = (Integer) q.retornaDato();

                    if (i != -1) {
                        if ("".equals(transiciones[i][j]) || transiciones[i][j] == null) {
                            k = -1;
                        } else {
                            k = Integer.parseInt(transiciones[i][j]);
                        }
                    } else {
                        k = -1;
                    }

                    l = buscarEnParticiones(particiones, k);

                    if (l == prim) {
                        huevo1.agregar(i);
                    } else {
                        huevo2.agregar(i);
                    }
                    q = q.retornaLiga();
                }
                if (!huevo2.esVacia()) {
                    particiones.eliminar(particionActual);
                    particiones.agregar(huevo1);
                    particiones.agregar(huevo2);
                    p = particiones.getPrimero();
                    continue cicloModificaciones;
                }
            }
            p = p.retornaLiga();
        }

        //Ordenamiento de particiones:
        part0 = buscarParticion(particiones, 1);
        if (part0 == null) {
            return new AutomataFinito();
        }
        particiones.eliminar(part0);
        particiones.agregarAlInicio(part0);

        //Creación de particiones
        String[][] nTransiciones = new String[particiones.getTamaño() - 1][getNumeroSimbolos()];
        boolean[] nFinal = new boolean[particiones.getTamaño() - 1];

        NodoSimple nodoParticion = particiones.getPrimero();
        Conjunto particion;

        i = 1;
        while (nodoParticion != null) {
            particion = (Conjunto) nodoParticion.retornaDato();
            k = (int) particion.getPrimero().retornaDato();
            //l = buscarEnParticiones(particiones, k);

            if (k == -1) {
                nodoParticion = nodoParticion.retornaLiga();
                continue;
            }
            nFinal[i - 1] = esFinal[k];
            for (j = 0; j < getNumeroSimbolos(); j++) {
                if (transiciones[k][j] != null) {
                    l = buscarEnParticiones(particiones, Integer.parseInt(transiciones[k][j]));

                    nTransiciones[i - 1][j] = Integer.toString(l + 1);
                }
            }

            i = i + 1;
            nodoParticion = nodoParticion.retornaLiga();
        }

        return new AutomataFinito(nFinal, nTransiciones, simbolos);
    }

    private static int buscarEnParticiones(Conjunto particiones, int buscar) {
        NodoSimple p = particiones.getPrimero();
        int posicion = 0;

        while (p != null) {
            Conjunto particion = (Conjunto) p.retornaDato();
            NodoSimple q = particion.getPrimero();

            while (q != null) {
                if ((int) q.retornaDato() == buscar) {
                    return posicion;
                }
                q = q.retornaLiga();
            }
            posicion = posicion + 1;
            p = p.retornaLiga();
        }
        return -1;
    }

    /**
     * Verifica si en el conjunto de estados AFD, existe el estado.
     *
     * @param AFD Conjunto de estados del automata finito, donde se verificará.
     * @param estado Estado a buscar en AFD.
     * @return true, si se encuentra, false si no se encuentra.
     */
    private static boolean existeEstadoEnAFD(Conjunto AFD, Conjunto estado) {
        NodoSimple p = AFD.getPrimero();
        while (p != null) {
            Conjunto[] fila = (Conjunto[]) p.retornaDato();
            if (fila[0].equals(estado)) {
                return true;
            }
            p = p.retornaLiga();
        }
        return false;
    }

    /**
     * Busca la posición del estado en el automata finito.
     *
     * @param AFD
     * @param estado
     * @return Posición del estado, -1 si no se encuentra.
     */
    private static int buscarEstado(Conjunto AFD, Conjunto estado) {
        NodoSimple p = AFD.getPrimero();
        int posicion = 0;

        while (p != null) {
            Conjunto[] fila = (Conjunto[]) p.retornaDato();

            if (fila[0].equals(estado)) {
                return posicion;
            }
            posicion = posicion + 1;
            p = p.retornaLiga();
        }
        return -1;
    }

    private Conjunto buscarParticion(Conjunto particiones, int buscar) {
        NodoSimple p = particiones.getPrimero();
        int posicion = 0;

        while (p != null) {
            Conjunto particion = (Conjunto) p.retornaDato();
            NodoSimple q = particion.getPrimero();

            while (q != null) {
                if ((int) q.retornaDato() == buscar) {
                    return particion;
                }
                q = q.retornaLiga();
            }
            p = p.retornaLiga();
        }

        return null;
    }
}
