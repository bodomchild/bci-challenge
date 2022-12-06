package bci.challenge.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "phone")
public class Phone {

    @Id
    @Column(name = "number", nullable = false)
    private Long number;

    @Column(name = "city_code", nullable = false)
    private Integer cityCode;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Phone phone = (Phone) o;
        return number != null && Objects.equals(number, phone.number);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}