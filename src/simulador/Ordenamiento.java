/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulador;

import java.util.Comparator;

/**
 *
 * @author casar
 */
public class Ordenamiento implements Comparator<Proceso> {

  public int compare(Proceso proceso1, Proceso proceso2) {
        return Integer.compare(proceso1.getTiempoArribo(), proceso2.getTiempoArribo());
    }
    
}
