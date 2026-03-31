package bankinfo.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table(name = "client_phone")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class ClientPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "phone", nullable = false, length = 50)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type", nullable = false, columnDefinition = "phone_type_enum")
    @Type(type = "pgsql_enum")
    private PhoneType phoneType;

    public ClientPhone() {
    }

    public ClientPhone(String phone, PhoneType phoneType) {
        this.phone = phone;
        this.phoneType = phoneType;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public String toString() {
        return "ClientPhone{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", phoneType=" + phoneType +
                '}';
    }
}