/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import Vistas.exceptions.NonexistentEntityException;
import entidades.Ordenes;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Proveedores;
import entidades.Proyectos;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Arcke
 */
public class OrdenesJpaController implements Serializable {

    public OrdenesJpaController() {
        this.emf = Persistence.createEntityManagerFactory("InventariosANCAZPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ordenes ordenes) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedores idProveedor = ordenes.getIdProveedor();
            if (idProveedor != null) {
                idProveedor = em.getReference(idProveedor.getClass(), idProveedor.getIdProveedor());
                ordenes.setIdProveedor(idProveedor);
            }
            Proyectos idProyecto = ordenes.getIdProyecto();
            if (idProyecto != null) {
                idProyecto = em.getReference(idProyecto.getClass(), idProyecto.getIdProyecto());
                ordenes.setIdProyecto(idProyecto);
            }
            em.persist(ordenes);
            if (idProveedor != null) {
                idProveedor.getOrdenesCollection().add(ordenes);
                idProveedor = em.merge(idProveedor);
            }
            if (idProyecto != null) {
                idProyecto.getOrdenesCollection().add(ordenes);
                idProyecto = em.merge(idProyecto);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ordenes ordenes) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordenes persistentOrdenes = em.find(Ordenes.class, ordenes.getIdOrden());
            Proveedores idProveedorOld = persistentOrdenes.getIdProveedor();
            Proveedores idProveedorNew = ordenes.getIdProveedor();
            Proyectos idProyectoOld = persistentOrdenes.getIdProyecto();
            Proyectos idProyectoNew = ordenes.getIdProyecto();
            if (idProveedorNew != null) {
                idProveedorNew = em.getReference(idProveedorNew.getClass(), idProveedorNew.getIdProveedor());
                ordenes.setIdProveedor(idProveedorNew);
            }
            if (idProyectoNew != null) {
                idProyectoNew = em.getReference(idProyectoNew.getClass(), idProyectoNew.getIdProyecto());
                ordenes.setIdProyecto(idProyectoNew);
            }
            ordenes = em.merge(ordenes);
            if (idProveedorOld != null && !idProveedorOld.equals(idProveedorNew)) {
                idProveedorOld.getOrdenesCollection().remove(ordenes);
                idProveedorOld = em.merge(idProveedorOld);
            }
            if (idProveedorNew != null && !idProveedorNew.equals(idProveedorOld)) {
                idProveedorNew.getOrdenesCollection().add(ordenes);
                idProveedorNew = em.merge(idProveedorNew);
            }
            if (idProyectoOld != null && !idProyectoOld.equals(idProyectoNew)) {
                idProyectoOld.getOrdenesCollection().remove(ordenes);
                idProyectoOld = em.merge(idProyectoOld);
            }
            if (idProyectoNew != null && !idProyectoNew.equals(idProyectoOld)) {
                idProyectoNew.getOrdenesCollection().add(ordenes);
                idProyectoNew = em.merge(idProyectoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ordenes.getIdOrden();
                if (findOrdenes(id) == null) {
                    throw new NonexistentEntityException("The ordenes with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordenes ordenes;
            try {
                ordenes = em.getReference(Ordenes.class, id);
                ordenes.getIdOrden();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordenes with id " + id + " no longer exists.", enfe);
            }
            Proveedores idProveedor = ordenes.getIdProveedor();
            if (idProveedor != null) {
                idProveedor.getOrdenesCollection().remove(ordenes);
                idProveedor = em.merge(idProveedor);
            }
            Proyectos idProyecto = ordenes.getIdProyecto();
            if (idProyecto != null) {
                idProyecto.getOrdenesCollection().remove(ordenes);
                idProyecto = em.merge(idProyecto);
            }
            em.remove(ordenes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ordenes> findOrdenesEntities() {
        return findOrdenesEntities(true, -1, -1);
    }

    public List<Ordenes> findOrdenesEntities(int maxResults, int firstResult) {
        return findOrdenesEntities(false, maxResults, firstResult);
    }

    private List<Ordenes> findOrdenesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ordenes.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ordenes findOrdenes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ordenes.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdenesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ordenes> rt = cq.from(Ordenes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
