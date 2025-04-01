package data.hortofruticola;


import jakarta.persistence.*;


import data.Producto;

@Entity
@Table(name = "HortoFruticola")
@SqlResultSetMapping(
        name = "HortoFruticolaMapping",
        entities = {
                @EntityResult(
                        entityClass = HortoFruticola.class,
                        fields = {
                                @FieldResult(name = "id", column = "id_hortofruticola"),
                                @FieldResult(name = "nombre", column = "nombre"),
                                @FieldResult(name = "unidad", column = "unidad"),
                                @FieldResult(name = "tipoConserva", column = "tipo_conserva"),
                                @FieldResult(name = "idImg", column = "id_img"),
                                @FieldResult(name = "idNegocio", column = "id_negocio")
                        }
                )
        },
        columns = {
                @ColumnResult(name = "imagenNombre", type = String.class),
                @ColumnResult(name = "imagenTipo", type = String.class),
                @ColumnResult(name = "imagenDatos", type = byte[].class)
        }
)
@AttributeOverride(name = "id", column = @Column(name = "id_hortofruticola"))
public class HortoFruticola extends Producto {
}

