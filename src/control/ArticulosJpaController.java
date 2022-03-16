/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import entidades.Articulos;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Proveedores;
import entidades.Detalleorden;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Arcke
 */
public class ArticulosJpaController implements Serializable {

    public ArticulosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public ArticulosJpaController() {
        this.emf = Persistence.createEntityManagerFactory("InventariosANCAZPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Articulos articulos) {
        if (articulos.getDetalleordenCollection() == null) {
            articulos.setDetalleordenCollection(new ArrayList<Detalleorden>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedores idProveedor = articulos.getIdProveedor();
            if (idProveedor != null) {
                idProveedor = em.getReference(idProveedor.getClass(), idProveedor.getIdProveedor());
                articulos.setIdProveedor(idProveedor);
            }
            Collection<Detalleorden> attachedDetalleordenCollection = new ArrayList<Detalleorden>();
            for (Detalleorden detalleordenCollectionDetalleordenToAttach : articulos.getDetalleordenCollection()) {
                detalleordenCollectionDetalleordenToAttach = em.getReference(detalleordenCollectionDetalleordenToAttach.getClass(), detalleordenCollectionDetalleordenToAttach.getIdDetalleOrden());
                attachedDetalleordenCollection.add(detalleordenCollectionDetalleordenToAttach);
            }
            articulos.setDetalleordenCollection(attachedDetalleordenCollection);
            em.persist(articulos);
            if (idProveedor != null) {
                idProveedor.getArticulosCollection().add(articulos);
                idProveedor = em.merge(idProveedor);
            }
            for (Detalleorden detalleordenCollectionDetalleorden : articulos.getDetalleordenCollection()) {
                Articulos oldIdArticuloOfDetalleordenCollectionDetalleorden = detalleordenCollectionDetalleorden.getIdArticulo();
                detalleordenCollectionDetalleorden.setIdArticulo(articulos);
                detalleordenCollectionDetalleorden = em.merge(detalleordenCollectionDetalleorden);
                if (oldIdArticuloOfDetalleordenCollectionDetalleorden != null) {
                    oldIdArticuloOfDetalleordenCollectionDetalleorden.getDetalleordenCollection().remove(detalleordenCollectionDetalleorden);
                    oldIdArticuloOfDetalleordenCollectionDetalleorden = em.merge(oldIdArticuloOfDetalleordenCollectionDetalleorden);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Articulos articulos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulos persistentArticulos = em.find(Articulos.class, articulos.getIdArticulo());
            Proveedores idProveedorOld = persistentArticulos.getIdProveedor();
            Proveedores idProveedorNew = articulos.getIdProveedor();
            Collection<Detalleorden> detalleordenCollectionOld = persistentArticulos.getDetalleordenCollection();
            Collection<Detalleorden> detalleordenCollectionNew = articulos.getDetalleordenCollection();
            List<String> illegalOrphanMessages = null;
            for (Detalleorden detalleordenCollectionOldDetalleorden : detalleordenCollectionOld) {
                if (!detalleordenCollectionNew.contains(detalleordenCollectionOldDetalleorden)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalleorden " + detalleordenCollectionOldDetalleorden + " since its idArticulo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idProveedorNew != null) {
                idProveedorNew = em.getReference(idProveedorNew.getClass(), idProveedorNew.getIdProveedor());
                articulos.setIdProveedor(idProveedorNew);
            }
            Collection<Detalleorden> attachedDetalleordenCollectionNew = new ArrayList<Detalleorden>();
            for (Detalleorden detalleordenCollectionNewDetalleordenToAttach : detalleordenCollectionNew) {
                detalleordenCollectionNewDetalleordenToAttach = em.getReference(detalleordenCollectionNewDetalleordenToAttach.getClass(), detalleordenCollectionNewDetalleordenToAttach.getIdDetalleOrden());
                attachedDetalleordenCollectionNew.add(detalleordenCollectionNewDetalleordenToAttach);
            }
            detalleordenCollectionNew = attachedDetalleordenCollectionNew;
            articulos.setDetalleordenCollection(detalleordenCollectionNew);
            articulos = em.merge(articulos);
            if (idProveedorOld != null && !idProveedorOld.equals(idProveedorNew)) {
                idProveedorOld.getArticulosCollection().remove(articulos);
                idProveedorOld = em.merge(idProveedorOld);
            }
            if (idProveedorNew != null && !idProveedorNew.equals(idProveedorOld)) {
                idProveedorNew.getArticulosCollection().add(articulos);
                idProveedorNew = em.merge(idProveedorNew);
            }
            for (Detalleorden detalleordenCollectionNewDetalleorden : detalleordenCollectionNew) {
                if (!detalleordenCollectionOld.contains(detalleordenCollectionNewDetalleorden)) {
                    Articulos oldIdArticuloOfDetalleordenCollectionNewDetalleorden = detalleordenCollectionNewDetalleorden.getIdArticulo();
                    detalleordenCollectionNewDetalleorden.setIdArticulo(articulos);
                    detalleordenCollectionNewDetalleorden = em.merge(detalleordenCollectionNewDetalleorden);
                    if (oldIdArticuloOfDetalleordenCollectionNewDetalleorden != null && !oldIdArticuloOfDetalleordenCollectionNewDetalleorden.equals(articulos)) {
                        oldIdArticuloOfDetalleordenCollectionNewDetalleorden.getDetalleordenCollection().remove(detalleordenCollectionNewDetalleorden);
                        oldIdArticuloOfDetalleordenCollectionNewDetalleorden = em.merge(oldIdArticuloOfDetalleordenCollectionNewDetalleorden);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = articulos.getIdArticulo();
                if (findArticulos(id) == null) {
                    throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulos articulos;
            try {
                articulos = em.getReference(Articulos.class, id);
                articulos.getIdArticulo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detalleorden> detalleordenCollectionOrphanCheck = articulos.getDetalleordenCollection();
            for (Detalleorden detalleordenCollectionOrphanCheckDetalleorden : detalleordenCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articulos (" + articulos + ") cannot be destroyed since the Detalleorden " + detalleordenCollectionOrphanCheckDetalleorden + " in its detalleordenCollection field has a non-nullable idArticulo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Proveedores idProveedor = articulos.getIdProveedor();
            if (idProveedor != null) {
                idProveedor.getArticulosCollection().remove(articulos);
                idProveedor = em.merge(idProveedor);
            }
            em.remove(articulos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Articulos> findArticulosEntities() {
        return findArticulosEntities(true, -1, -1);
    }

    public List<Articulos> findArticulosEntities(int maxResults, int firstResult) {
        return findArticulosEntities(false, maxResults, firstResult);
    }

    private List<Articulos> findArticulosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Articulos.class));
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

    public Articulos findArticulos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Articulos.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticulosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Articulos> rt = cq.from(Articulos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
