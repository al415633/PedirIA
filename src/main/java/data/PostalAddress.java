package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
public class PostalAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String street;
    int building;
    int floor;
    String door;

    @Transient
    public static final PostalAddress NO_ADDRESS = new PostalAddress();

    public PostalAddress() {
        super();
    }

    public PostalAddress(String street, int building, int floor, String door) {
        this.street = street;
        this.building = building;
        this.floor = floor;
        this.door = door;
    }
}
