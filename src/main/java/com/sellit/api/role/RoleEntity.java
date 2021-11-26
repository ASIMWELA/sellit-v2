package com.sellit.api.role;

import com.sellit.api.Enum.ERole;
import com.sellit.api.commons.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "roles")
public class RoleEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 20)
    ERole name;
}
