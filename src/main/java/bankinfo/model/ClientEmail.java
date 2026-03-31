package bankinfo.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table(name = "client_email")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class ClientEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_type", nullable = false, columnDefinition = "email_type_enum")
    @Type(type = "pgsql_enum")
    private EmailType emailType;

    public ClientEmail() {
    }

    public ClientEmail(String email, EmailType emailType) {
        this.email = email;
        this.emailType = emailType;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    @Override
    public String toString() {
        return "ClientEmail{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", emailType=" + emailType +
                '}';
    }
}