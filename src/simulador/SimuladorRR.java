/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author casar
 */
public class SimuladorRR {
    
    Proceso procesoActual= null;
    static double porcentajeUso=0;   //porcetaje de uso de CPU
    static int tiempoRT=0;       //tiempo de retorno de la tanda
    static int totalSO=0;        //tiempo Total del SO
    static int tiempoActual=0;
    static int tiempoCPUdesocupada=0;  //tiempo de CPU desocupada
    static int tiempoNP=0;
    static int tiempoRetorno=0;
    static double tiempoRNT=0;   //tiempo de retorno normalizado de la tanda
    static double tiempoRNP=0;  //aculador del tiempo de retono de cada proceso  
    static int tiempoCPU=0;
    static int tiempoTotalCPU=0;
    static int tiempoC=0;    //contado de tiempos de conmutacion
    static int tiempoF=0;    //contador de tiempos de finalizacion de procesos
    static int q;            //QUANTUM
    

    public SimuladorRR() {
    }
 public void simularRR(String politica, ArrayList<Proceso> listaDeProcesos, ConfiguracionSistema configuracion){
    DefaultTableModel modelo = (DefaultTableModel) SimulacionProceso.jTableProces.getModel();
    modelo.setColumnCount(7);
    
    //SE ASUME QUE TODOS LOS PROCESOS HAN LLEGADO Y ESTAN EN ESTADO LISTO
     // Ordenar la lista por orden de llegada
    Collections.sort(listaDeProcesos, new Ordenamiento()); //LOS ORDENA POR ORDEN DE LLEGADA PRIMERAMENTE
         
    //CONFIGURO LOS TIEMPOS DEL SO
     configuracionDeinicioDelSO(configuracion);
    
    //ANTES DE INICIAR LA EJECUCION, LE ASIGNO A CADA PROCESO EL TIEMPO DE RAFAGA  DE CPU RESTANTE PARA UTILIZARLA DURANTE LA EJECUCION
    inicializarProcesos();
    
    //CARGA LA LISTA A UNA AUXILIAR
    cargarLista(listaDeProcesos, Proceso.listaEjecutando);
    
    //ASIGNO EL QUANTUM
    q = configuracion.getQuantum();
    
    Thread simulacionThread = new Thread(() -> {
        
   while(procesosPendiente(listaDeProcesos)){
        for(Proceso p : Proceso.listaDeProcesos){
            if(p.getRafagasCPU() > 0){
              procesoActual=p;
              
             //while(!Proceso.listaEjecutando.isEmpty() && procesoActual != null){ 
            while(procesoActual.getEstado().equals("Listo")  && procesoActual.getRafagaCPUrestante() >0 && procesoActual.getQuantum() < q ){
               //EJECUTA EL PROCESO ACTUAL
                procesoActual.setEstado("En Ejecucion");
                procesoActual.setTiempoCPU(procesoActual.getTiempoCPU() + 1);
                
                procesoActual.setQuantum(procesoActual.getQuantum() + 1);
                esperar(500);
                        
                //ACTUALIZO LAS RAFAGAS DE CPU EJECUTADAS
                procesoActual.setRafagasCPUejecutadas(procesoActual.getRafagasCPUejecutadas()+1);
                    
                //CARGO LOS DATOS EN LA TABAL   
                actualizarTabla(modelo);
                          
                //ACTUALIZO LAS RAFAGAS RESTANTE
                procesoActual.setRafagaCPUrestante(procesoActual.getRafagaCPUrestante() - 1);
              
                //VERIFICA SI EXISTEN PROCESOS EN ESPERA 
                verificarProcesosEspera(modelo,tiempoActual);
           
                esperar(500);
                        
              
                //VERIFICO SI HAY PROCESOS BLOQUEADOS POR E/S
                verificarProcesosBloqueados(modelo, tiempoActual);
            
                
                tiempoActual++;
                procesoActual.setEstado("Listo");
                
            }
           } 
         
            //SETEO EL QUANTUM DEL PROCESO PARA CONTINUAR EJECUTANDO
            if(procesoActual.getQuantum() == q){
               int aux=0;
               procesoActual.setQuantum(aux);
         
            } 
                
            if(procesoActual.getRafagaCPUrestante() == 0){   //SI EJECUTO LAS RAFAGAS DE CPU 
                     
                int rafagasCPU = procesoActual.getRafagasCPU();
                rafagasCPU --;
                procesoActual.setRafagasCPU(rafagasCPU);
                      
                if(rafagasCPU >0){
                    if(configuracion.getTiempoConmutacion() > 0){
                        tiempoActual += configuracion.getTiempoConmutacion();       //SE ACTUALIZA EL TIEMPO CON EL TIEMPO DE CONMUTACION
                        tiempoC += configuracion.getTiempoConmutacion();
                    }    
                            
                    //SETO NUEVAMENTE LOS TIEMPOS DE RESTANTE DE RAFAGAS
                    for(Proceso proc: listaDeProcesos){
                        if(proc == procesoActual){
                            procesoActual.setRafagaCPUrestante(proc.getDuracionRafagaCPU());
                        }
                    }
                
                    //SETEO EL QUANTUM DEL PROCESO PARA CONTINUAR EJECUTANDO
                      if(procesoActual.getQuantum() < q ){
                          int aux=0;
                          procesoActual.setQuantum(aux);
                      }       
                    
                    //VERIFICA SI HAY RAFAGAS DE E/S
                    verificarRafagasES(modelo);
                         
                   esperar(500);
                         
                                                
                }else{  //EJECUTO TODA LAS RAFAGAS DE CPU
                       if(!procesoActual.getEstado().equals("Terminado")){
                            if(configuracion.getTiempoTerminacionProceso() >0){
                                tiempoActual += configuracion.getTiempoTerminacionProceso();  //INCREMENTA EL TIEMPO DE EJECUCION CON EL TIEMPO DE FINALIZACION DEL PROCESO
                            tiempoF +=configuracion.getTiempoTerminacionProceso();
                            }
                        
                             //CAMBIA EL ESTADO Y SETEA EL TIEMPO DE FINALIZACION DEL PROCESO
                            procesoActual.setEstado("Terminado");
                            procesoActual.setTiempoFinalizado(tiempoActual);
                        
                            //CARGA LOS DATOS A LA TABLA
                            actualizarTabla(modelo);
                        
                            Proceso.listaDeTerminados.add(procesoActual);
                            Proceso.listaEjecutando.remove(procesoActual);               
                        }
                 }      
              }
        
            //}
            }//fin del for   
       }//while
         informacionTanda();
        
    });//hilo de procesos
        
   simulacionThread.start(); // Iniciar el hilo de simulación         
}//metodo    


//Verificar si hay procesos pendientes
private static boolean procesosPendiente(ArrayList<Proceso> listaDeProcesos) {
    for(Proceso procesos: listaDeProcesos){
         if (!procesos.getEstado().equals("Terminado")) {
            return true;
        }
    }       
        return false;            
}


private void inicializarProcesos() {
        for(Proceso pr: Proceso.listaDeProcesos){
            pr.setRafagaCPUrestante(pr.getDuracionRafagaCPU());       
            }
    }

private void verificarProcesosEspera(DefaultTableModel modelo, int tiempoActual) {    
        for (Proceso p : Proceso.listaEjecutando) {
            if (p != procesoActual && tiempoActual >= p.getTiempoArribo()) {
                     p.setTiempoEspera(p.getTiempoEspera() + 1);
                     //CARGA LOS DATOS A LA TABLA
                     SwingUtilities.invokeLater(() -> {
                        modelo.addRow(new Object[]{tiempoActual,p.getNombre(), p.getTiempoArribo(), 
                        p.getRafagasCPUejecutadas(),p.getRafagasCPU(), p.getDuracionRafagaES(), p.getEstado()});        
                    });
                 
                }
            }
    }

private void verificarProcesosBloqueados(DefaultTableModel modelo, int tiempoActual) {
        if(!Proceso.listaBloqueados.isEmpty()){
             Iterator<Proceso> iteradorBloqueados = Proceso.listaBloqueados.iterator();
              while (iteradorBloqueados.hasNext()) {
                Proceso pBloqueado = iteradorBloqueados.next();
                pBloqueado.setTiempoRestanteES(pBloqueado.getTiempoRestanteES()-1);
                if (pBloqueado.getTiempoRestanteES() == 0) {
                    pBloqueado.setTotalES(pBloqueado.getTotalES() + 1);
                    
                    //CARGO LOS DATOS EN LA TABLE
                    SwingUtilities.invokeLater(() -> {
                        modelo.addRow(new Object[]{tiempoActual,pBloqueado.getNombre(), pBloqueado.getTiempoArribo(), 
                        pBloqueado.getRafagasCPUejecutadas(),pBloqueado.getRafagasCPU(), pBloqueado.getDuracionRafagaES(), pBloqueado.getEstado()});        
                     });
                    
                    //EL PROCESO VUELVE A LA LISTA DE EJECUTANDO ESPERANDO SU TURNO
                    pBloqueado.setEstado("Listo") ;
                    Proceso.listaEjecutando.add(pBloqueado);
                    iteradorBloqueados.remove();
                }else{
                    //CARGA LOS DATOS A LA TABLA
                   SwingUtilities.invokeLater(() -> {
                        modelo.addRow(new Object[]{tiempoActual,pBloqueado.getNombre(), pBloqueado.getTiempoArribo(), 
                        pBloqueado.getRafagasCPUejecutadas(),pBloqueado.getRafagasCPU(), pBloqueado.getDuracionRafagaES(), pBloqueado.getEstado()});        
                     });
                    pBloqueado.setTotalES(pBloqueado.getTotalES() + 1);
                   
                  
                }
             }
          }
  
    }

private void configuracionDeinicioDelSO(ConfiguracionSistema configuracion) {
        for(Proceso p: Proceso.listaDeProcesos){
            p.setTiempoArribo(p.getTiempoArribo() + configuracion.getTiempoAceptacionNuevoProceso());
            if(configuracion.getTiempoAceptacionNuevoProceso() > 0){
            tiempoNP ++;
            }
        }
          tiempoActual=Proceso.listaDeProcesos.get(0).getTiempoArribo();
    }

private void cargarLista(ArrayList<Proceso> listaDeProcesos, ArrayList<Proceso> listaEjecutando) {
        for(Proceso procesos: listaDeProcesos){
                Proceso.listaEjecutando.add(procesos);
          }        
    }
    
    
  
 private static void informacionTanda(){
    DefaultTableModel modelo = (DefaultTableModel) SimulacionProceso.jTableInfoProcesos.getModel();
    modelo.setColumnCount(4);
    
    for(Proceso procesos: Proceso.listaDeTerminados){
      
         tiempoRetorno = (procesos.getTiempoFinalizado()- procesos.getTiempoArribo());
         tiempoRNP= (double) tiempoRetorno /procesos.getRafagasCPUejecutadas();
                
         modelo.addRow(new Object[]{procesos.getNombre(), tiempoRetorno, tiempoRNP, procesos.getTiempoEspera()});
        }
     
    informacionSistema();
}

private static void informacionSistema(){
    DefaultTableModel modelo2 = (DefaultTableModel) SimulacionProceso.jTableTanda.getModel();
    modelo2.setColumnCount(6);
 
    //OBTENGO LA INFORMACION DE  LA TANDA
    for(Proceso procesos: Proceso.listaDeTerminados){
         if(procesos.getTiempoFinalizado() > tiempoRT){  //BUSCO EL VALOR MAS ALTO DE FINALIZACION
              tiempoRT=procesos.getTiempoFinalizado();
          }       
         
    
         tiempoRetorno = (procesos.getTiempoFinalizado()- procesos.getTiempoArribo());
         tiempoCPU= procesos.getRafagasCPUejecutadas();
         tiempoTotalCPU += tiempoCPU;
    }
    
    porcentajeUso= ((double) tiempoTotalCPU/tiempoRT)*100;
    tiempoRNT= (double) tiempoRT/Proceso.listaDeTerminados.size();
    
    //calculo el tiempo total del SO
    totalSO= tiempoNP + tiempoC + tiempoF; 
    modelo2.addRow(new Object[]{tiempoRT, tiempoRNT,tiempoCPUdesocupada,totalSO,tiempoTotalCPU, porcentajeUso});
  
}

  
private void esperar(int milisegundos) {
    try {
        Thread.sleep(milisegundos);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

private void verificarRafagasES(DefaultTableModel modelo) {
    if (procesoActual.getDuracionRafagaES() >0){ 
         //CAMBIA EL ESTADO 
         procesoActual.setEstado("En E/S");
                 
         //SETEO LAS RAFAGAS DE E/S
        for(Proceso pr: Proceso.listaEjecutando){
            if(pr == procesoActual){ 
                pr.setTiempoRestanteES(pr.getDuracionRafagaES());
                                    
             }
        }   
        Proceso.listaBloqueados.add(procesoActual);
        Proceso.listaEjecutando.remove(procesoActual);
                        
        //SI ES EL UTLIMO PROCESO HACE E/S HASTA FINALIZAR 
        if(Proceso.listaEjecutando.isEmpty() && !Proceso.listaBloqueados.isEmpty()){
            while(!Proceso.listaBloqueados.isEmpty()&& Proceso.listaEjecutando.isEmpty()){
                tiempoCPUdesocupada++;
                verificarProcesosBloqueados(modelo, tiempoActual);
                 tiempoActual ++;
                 
            }
        }
                        
    }
      
}

private void actualizarTabla(DefaultTableModel modelo) {
    SwingUtilities.invokeLater(() -> {
       modelo.addRow(new Object[]{tiempoActual,procesoActual.getNombre(), procesoActual.getTiempoArribo(), 
            procesoActual.getRafagasCPUejecutadas(),procesoActual.getRafagasCPU(), procesoActual.getDuracionRafagaES(), procesoActual.getEstado()});        
        });
    }
}
