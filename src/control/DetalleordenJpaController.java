/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.NonexistentEntityException;
import control.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Articulos;
import entidades.Detalleorden;
import entidades.Ordenes;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Arcke
 */
public class DetalleordenJpaController implements Serializable {

    public DetalleordenJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalleorden detalleorden) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulos idArticulo = detalleorden.getIdArticulo();
            if (idArticulo != null) {
                idArticulo = em.getReference(idArticulo.getClass(), idArticulo.getIdArticulo());
                detalleorden.setIdArticulo(idArticulo);
            }
            Ordenes idOrden = detalleorden.getIdOrden();
            if (idOrden != null) {
                idOrden = em.getReference(idOrden.getClass(), idOrden.getIdOrden());
                detalleorden.setIdOrden(idOrden);
            }
            em.persist(detalleorden);
            if (idArticulo != null) {
                idArticulo.getDetalleordenCollection().add(detalleorden);
                idArticulo = em.merge(idArticulo);
            }
            if (idOrden != null) {
                idOrden.getDetalleordenCollection().add(detalleorden);
                idOrden = em.merge(idOrden);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDetalleorden(detalleorden.getIdDetalleOrden()) != null) {
                throw new PreexistingEntityException("Detalleorden " + detalleorden + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalleorden detalleorden) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalleorden persistentDetalleorden = em.find(Detalleorden.class, detalleorden.getIdDetalleOrden());
            Articulos idArticuloOld = persistentDetalleorden.getIdArticulo();
            Articulos idArticuloNew = detalleorden.getIdArticulo();
            Ordenes idOrdenOld = persistentDetalleorden.getIdOrden();
            Ordenes idOrdenNew = detalleorden.getIdOrden();
            if (idArticuloNew != null) {
                idArticuloNew = em.getReference(idArticuloNew.getClass(), idArticuloNew.getIdArticulo());
                detalleorden.setIdArticulo(idArticuloNew);
            }
            if (idOrdenNew != null) {
                idOrdenNew = em.getReference(idOrdenNew.getClass(), idOrdenNew.getIdOrden());
                detalleorden.setIdOrden(idOrdenNew);
            }
            detalleorden = em.merge(detalleorden);
            if (idArticuloOld != null && !idArticuloOld.equals(idArticuloNew)) {
                idArticuloOld.getDetalleordenCollection().remove(detalleorden);
                idArticuloOld = em.merge(idArticuloOld);
            }
            if (idArticuloNew != null && !idArticuloNew.equals(idArticuloOld)) {
                idArticuloNew.getDetalleordenCollection().add(detalleorden);
                idArticuloNew = em.merge(idArticuloNew);
            }
            if (idOrdenOld != null && !idOrdenOld.equals(idOrdenNew)) {
                idOrdenOld.getDetalleordenCollection().remove(detalleorden);
                idOrdenOld = em.merge(idOrdenOld);
            }
            if (idOrdenNew != null && !idOrdenNew.equals(idOrdenOld)) {
                idOrdenNew.getDetalleordenCollection().add(detalleorden);
                idOrdenNew = em.merge(idOrdenNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleorden.getIdDetalleOrden();
                if (findDetalleorden(id) == null) {
                    throw new NonexistentEntityException("The detalleorden with id " + id + " no longer exists.");
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
            Detalleorden detalleorden;
            try {
                detalleorden = em.getReference(Detalleorden.class, id);
                detalleorden.getIdDetalleOrden();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleorden with id " + id + " no longer exists.", enfe);
            }
            Articulos idArticulo = detalleorden.getIdArticulo();
            if (idArticulo != null) {
                idArticulo.getDetalleordenCollection().remove(detalleorden);
                idArticulo = em.merge(idArticulo);
            }
            Ordenes idOrden = detalleorden.getIdOrden();
            if (idOrden != null) {
                idOrden.getDetalleordenCollection().remove(detalleorden);
                idOrden = em.merge(idOrden);
            }
            em.remove(detalleorden);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detalleorden> findDetalleordenEntities() {
        return findDetalleordenEntities(true, -1, -1);
    }

    public List<Detalleorden> findDetalleordenEntities(int maxResults, int firstResult) {
        return findDetalleordenEntities(false, maxResults, firstResult);
    }

    private List<Detalleorden> findDetalleordenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalleorden.class));
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

    public Detalleorden findDetalleorden(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalleorden.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleordenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalleorden> rt = cq.from(Detalleorden.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
