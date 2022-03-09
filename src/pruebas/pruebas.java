/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebas;

//import Entidades.*;
//import controladores.*;
import entidades.Empleado;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

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
        Empleado empleado1 = new Empleado(1, "rafael", "carballo", "sistemas");
        Empleado empleado2 = new Empleado(2, "andrea", "borboa", "administracion");
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventariosANCAZPU");
        EntityManager em = emf.createEntityManager();
        
        em.getTransaction().begin();
//        em.persist(empleado1);
//        em.persist(empleado2);
        
        em.getTransaction().commit();
        // Lista de videojuegos cuyo desarrolladora es nintendo 
        String jpql = "SELECT a FROM Empleado a";        
        Query query = em.createQuery(jpql);        
        System.out.println(query);
        List<Empleado> resultado = query.getResultList();
        for (Empleado empleado : resultado) {
            System.out.println(empleado.getNombre());
        }
        
        
        
        
        
    }
    
}
