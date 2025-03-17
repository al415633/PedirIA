package data.pescaderia;

import data.Producto;
import jakarta.persistence.*;

@Entity
@Table(name = "Pescado")
@SqlResultSetMapping(
        name = "PescadoMapping",
        entities = {
                @EntityResult(
                        entityClass = Pescado.class,
                        fields = {
                                @FieldResult(name = "id", column = "id_pescado"),
                                @FieldResult(name = "nombre", column = "nombre"),
                                @FieldResult(name = "unidad", column = "unidad"),
                                @FieldResult(name = "tipoConserva", column = "tipo_conserva"),
                                @FieldResult(name = "idImg", column = "id_img")
                        }
                )
        },
        columns = {
                @ColumnResult(name = "imagenNombre", type = String.class),
                @ColumnResult(name = "imagenTipo", type = String.class),
                @ColumnResult(name = "imagenDatos", type = byte[].class)
        }
)
public class Pescado extends Producto {
}
