/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Arcke
 */
@Entity
@Table(name = "detalleorden")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalleorden.findAll", query = "SELECT d FROM Detalleorden d")
    , @NamedQuery(name = "Detalleorden.findByIdDetalleOrden", query = "SELECT d FROM Detalleorden d WHERE d.idDetalleOrden = :idDetalleOrden")
    , @NamedQuery(name = "Detalleorden.findByCantidad", query = "SELECT d FROM Detalleorden d WHERE d.cantidad = :cantidad")})
public class Detalleorden implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idDetalleOrden")
    private Integer idDetalleOrden;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private int cantidad;
    @JoinColumn(name = "idArticulo", referencedColumnName = "idArticulo")
    @ManyToOne(optional = false)
    private Articulos idArticulo;
    @JoinColumn(name = "idOrden", referencedColumnName = "idOrden")
    @ManyToOne(optional = false)
    private Ordenes idOrden;

    public Detalleorden() {
    }

    public Detalleorden(Integer idDetalleOrden) {
        this.idDetalleOrden = idDetalleOrden;
    }

    public Detalleorden(Integer idDetalleOrden, int cantidad) {
        this.idDetalleOrden = idDetalleOrden;
        this.cantidad = cantidad;
    }

    public Integer getIdDetalleOrden() {
        return idDetalleOrden;
    }

    public void setIdDetalleOrden(Integer idDetalleOrden) {
        this.idDetalleOrden = idDetalleOrden;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Articulos getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Articulos idArticulo) {
        this.idArticulo = idArticulo;
    }

    public Ordenes getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Ordenes idOrden) {
        this.idOrden = idOrden;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetalleOrden != null ? idDetalleOrden.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalleorden)) {
            return false;
        }
        Detalleorden other = (Detalleorden) object;
        if ((this.idDetalleOrden == null && other.idDetalleOrden != null) || (this.idDetalleOrden != null && !this.idDetalleOrden.equals(other.idDetalleOrden))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Detalleorden[ idDetalleOrden=" + idDetalleOrden + " ]";
    }
    
}
