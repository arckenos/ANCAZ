/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebas;

//import Entidades.*;
//import controladores.*;
import entidades.Articulos;
import entidades.Detalleorden;
import entidades.Empleados;
import entidades.Ordenes;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import logica.Control;

/**
 *
 * @author Arcke
 */
public class pruebas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Ordenes orden = new Ordenes(5);
        orden.setFecha(Timestamp.valueOf(LocalDateTime.now()));
        orden.setIdEmpleado(Control.consultarEmpleadoNombre("rafael"));
        orden.setIdProveedor(Control.consultarProveedordoNombre("CEMEX"));
        orden.setIdProyecto(Control.consultarProyecto(1));
        List<Detalleorden> lista = new ArrayList<>();
        Articulos a = Control.consultarArticulo(1);
        Detalleorden d = new Detalleorden();
        System.out.println(a.getNombre());
        
        d.setIdArticulo(a);
        d.setIdOrden(orden);
        d.setCantidad(10);
        lista.add(d);
        //rden.setDetalleordenCollection(lista);
        
        try {
            Control.registrarOrden(orden, lista);
        } catch (Exception ex) {
            Logger.getLogger(pruebas.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
}
