package bankinfo.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, columnDefinition = "client_type_enum")
    @Type(type = "pgsql_enum")
    private ClientType clientType;

    @Column(name = "display_name", nullable = false, length = 300)
    private String displayName;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("id ASC")
    private List<ClientAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("id ASC")
    private List<ClientPhone> phones = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("id ASC")
    private List<ClientEmail> emails = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("id ASC")
    private List<ContactPerson> contactPersons = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    @OrderBy("id ASC")
    private List<Account> accounts = new ArrayList<>();

    public Client() {
    }

    public Client(ClientType clientType, String displayName, OffsetDateTime createdAt) {
        this.clientType = clientType;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ClientAddress> getAddresses() {
        return addresses;
    }

    public void addAddress(ClientAddress address) {
        addresses.add(address);
        address.setClient(this);
    }

    public void removeAddress(ClientAddress address) {
        addresses.remove(address);
        address.setClient(null);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<ClientPhone> getPhones() {
        return phones;
    }

    public void addPhone(ClientPhone phone) {
        phones.add(phone);
        phone.setClient(this);
    }

    public void removePhone(ClientPhone phone) {
        phones.remove(phone);
        phone.setClient(null);
    }

    public List<ClientEmail> getEmails() {
        return emails;
    }

    public void addEmail(ClientEmail email) {
        emails.add(email);
        email.setClient(this);
    }

    public void removeEmail(ClientEmail email) {
        emails.remove(email);
        email.setClient(null);
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void addContactPerson(ContactPerson contactPerson) {
        contactPersons.add(contactPerson);
        contactPerson.setClient(this);
    }

    public void removeContactPerson(ContactPerson contactPerson) {
        contactPersons.remove(contactPerson);
        contactPerson.setClient(null);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", clientType=" + clientType +
                ", displayName='" + displayName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}