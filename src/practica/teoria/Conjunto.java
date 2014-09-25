/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package practica.teoria;

import Lista.NodoSimple;

/**
 * Estructura conjunto. Estructura basada en una lista simplemente ligada, de
 * tamaño dinámico. Cada elemento es único dentro del conjunto.
 *
 * @author Camilo Sampedro, Sergio Loaiza
 */
public class Conjunto {

    private NodoSimple primero, ultimo;
    private int modificaciones;
    private int tamaño;

    /**
     * @return Número de elementos dentro del conjunto.
     */
    public int getTamaño() {
        return tamaño;
    }

    /**
     * @param modificaciones Asigna el valor de modificaciones (Usado para
     * heredarlo de un conjunto anterior).
     */
    public void setModificaciones(int modificaciones) {
        this.modificaciones = modificaciones;
    }

    /**
     * @return Número de modificaciones hechas en el conjunto (uniones,
     * intersecciones, ...). Usado en métodos recursivos o ciclos.
     */
    public int getModificaciones() {
        return modificaciones;
    }

    /**
     * @return Primer nodo de la lista, para comenzar a recorrer.
     * @see Lista.NodoSimple.retorneLiga();
     */
    public NodoSimple getPrimero() {
        return primero;
    }

    /**
     * @return Último nodo de la lista.
     */
    public NodoSimple getUltimo() {
        return ultimo;
    }

    /**
     * Constructor vacío de un conjunto, inicializa el conjunto vacío.
     */
    public Conjunto() {
        primero = ultimo = null;
        tamaño = 0;
    }

    /**
     * @return true si la lista no contiene ningún elemento, false en caso
     * contrario.
     */
    public boolean esVacia() {
        return primero == null;
    }

    /**
     * Agrega al final del conjunto un elemento nuevo. Se verifica unicidad, si
     * ya existe el elemento, no se inserta.
     *
     * @param nuevo Objeto a insertar en el conjunto.
     */
    public void agregar(Object nuevo) {
        NodoSimple insertar = new NodoSimple(nuevo);

        //Si es vacía, se inserta como elemento único.
        if (esVacia()) {
            primero = ultimo = insertar;
            tamaño++;
            return;
        }

        //Se verifica que no exista en el conjunto.
        NodoSimple p = primero;
        while (p != null) {
            if (nuevo.equals(p.retornaDato())) {
                return;
            }
            p = p.retornaLiga();
        }

        //Se inserta el nuevo nodo al final.
        ultimo.asignaLiga(insertar);
        ultimo = insertar;
        tamaño++;
    }

    /**
     * Hace el proceso de unión con otro conjunto. Une los elementos del
     * conjunto actual con los elementos de otro conjunto, conservando la
     * unicidad en el nuevo conjunto.
     *
     * @param segundoConjunto Conjunto con el que se unirá.
     * @return Conjunto nuevo con ambos conjuntos unidos.
     */
    public Conjunto unir(Conjunto segundoConjunto) {
        modificaciones = 0;
        NodoSimple p = segundoConjunto.getPrimero();
        Conjunto nuevoConjunto = duplicar();
        Object dato;

        //Se verifica que se cumpla la condición de que si existe en el segundo 
        //conjunto, no se tome en cuenta en el nuevo.
        while (p != null) {
            dato = (Object) p.retornaDato();
            if (!existe(dato)) {
                nuevoConjunto.agregar(dato);
                modificaciones = modificaciones + 1;
            }
            p = p.retornaLiga();
        }

        //Se le asigna el número de modificaciones que se hicieron al nuevo 
        //conjunto.
        nuevoConjunto.setModificaciones(modificaciones);
        return nuevoConjunto;
    }

    /**
     * Crea una réplica exacta del conjunto actual. La réplica contendrá los
     * mismos valores del conjunto actual, pero con direcciones diferentes, con
     * el fin de poder hacer operaciones en el conjunto nuevo sin alterar el
     * conjunto actual.
     *
     * @return Conjunto exactamente igual al conjunto actual.
     */
    public Conjunto duplicar() {
        Conjunto nuevo = new Conjunto();
        NodoSimple nodoActual = primero;
        Object dato;
        while (nodoActual != null) {
            dato = (Object) nodoActual.retornaDato();
            nuevo.agregar(dato);
            nodoActual = nodoActual.retornaLiga();
        }
        return nuevo;
    }

    /**
     * Verifica si existe el elemento dentro del conjunto actual.
     *
     * @param elemento Elemento a verificar.
     * @return true, si existe el objeto en el conjunto, false en caso
     * contrario.
     */
    public boolean existe(Object elemento) {
        NodoSimple nodoActual = primero;
        Object dato;

        //Se verifica que no haya ningún nodo que tenga como dato el elemento a 
        //verificar.
        while (nodoActual != null) {
            dato = (Object) nodoActual.retornaDato();
            if (dato.equals(elemento)) {
                return true;
            }
            nodoActual = nodoActual.retornaLiga();
        }
        return false;
    }

    /**
     * Verifica si un conjunto es igual a otro. Para ser iguales dos conjuntos,
     * deben tener el mismo tamaño y todos sus elementos deben de ser iguales.
     * No se tiene en cuenta el orden.
     *
     * @param otroConjunto
     * @return
     */
    public boolean equals(Conjunto otroConjunto) {
        //Si tienen tamaños diferentes, no pueden ser iguales.
        if (tamaño != otroConjunto.tamaño) {
            return false;
        }

        //Si se da el caso en que uno de los elementos del primero no existe en
        //el segundo, no son iguales. Téngase en cuenta la unicidad de los 
        //elementos.
        NodoSimple nodoActual = primero;
        while (nodoActual != null) {
            if (!otroConjunto.existe(nodoActual.retornaDato())) {
                return false;
            }
            nodoActual = nodoActual.retornaLiga();
        }
        return true;
    }

    public int buscar(Object elemento) {
        NodoSimple p = primero;
        int posicion = 0;
        while (p != null) {
            if (p.retornaDato().equals(elemento)) {
                return posicion;
            }
            posicion = posicion + 1;
            p = p.retornaLiga();
        }
        return -1;
    }

    public void eliminar(Object elemento) {
        NodoSimple p = primero;
        NodoSimple q = null;
        while (p != null && !p.retornaDato().equals(elemento)) {
            q = p;
            p = p.retornaLiga();
        }
        if (p != null) {
            if (primero == p) {
                if (ultimo == p) {
                    primero = ultimo = null;

                } else {
                    primero = p.retornaLiga();
                }
            } else {
                q.asignaLiga(p.retornaLiga());
                if (ultimo == p) {
                    ultimo = q;
                }
            }
            tamaño--;
        }
    }

    void agregarAlInicio(Object nuevo) {
        NodoSimple insertar = new NodoSimple(nuevo);

        //Si es vacía, se inserta como elemento único.
        if (esVacia()) {
            primero = ultimo = insertar;
            tamaño++;
            return;
        }

        //Se verifica que no exista en el conjunto.
        NodoSimple p = primero;
        while (p != null) {
            if (nuevo.equals(p.retornaDato())) {
                return;
            }
            p = p.retornaLiga();
        }

        //Se inserta el nuevo nodo al final.
        insertar.asignaLiga(primero);
        primero = insertar;
        tamaño++;
    }
}
