package app.ecommerce.customer.entity;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "t_customer_uuid")
public class CustomerUid {
    @Column(name = "customer_uid", nullable = false, unique = true)
    private UUID uuid;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uid_seq")
    @SequenceGenerator(name = "uid_seq", sequenceName = "t_customer_uuid_seq")
    private long id;

    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false, unique = true)
    private Customer customer;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerUid() {
    }

    public CustomerUid(UUID uuid, Customer customer) {
        this.uuid = uuid;
        this.customer = customer;
    }
}
