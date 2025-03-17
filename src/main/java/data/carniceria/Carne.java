package data.carniceria;

import data.Producto;
import jakarta.persistence.*;

@Entity
@Table(name = "Carne")
@SqlResultSetMapping(
        name = "CarneMapping",
        entities = {
                @EntityResult(
                        entityClass = Carne.class,
                        fields = {
                                @FieldResult(name = "id", column = "id_carne"),
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
public class Carne extends Producto {
}
