package app.ecommerce.customer.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "t_customer_role")
public class Role {
    @Id
    private int roleId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerRole roleType;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public CustomerRole getRoleType() {
        return roleType;
    }

    public void setRoleType(CustomerRole roleType) {
        this.roleType = roleType;
    }

    public Role() {
    }

    public Role(int roleId, CustomerRole roleType) {
        this.roleId = roleId;
        this.roleType = roleType;
    }
}
