package bankinfo.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table(name = "client_address")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class ClientAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "address", nullable = false, length = 400)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, columnDefinition = "address_type_enum")
    @Type(type = "pgsql_enum")
    private AddressType addressType;

    public ClientAddress() {
    }

    public ClientAddress(String address, AddressType addressType) {
        this.address = address;
        this.addressType = addressType;
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    @Override
    public String toString() {
        return "ClientAddress{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", addressType=" + addressType +
                '}';
    }
}