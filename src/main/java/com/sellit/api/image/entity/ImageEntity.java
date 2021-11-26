package com.sellit.api.image.entity;

import com.sellit.api.commons.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="images")
@Entity
public class ImageEntity extends BaseEntity {

    @Column(name="image_name")
    String imageName;

    @Column(name="image_type", nullable = false)
    String imageType;

    @Column(name="on_display")
    boolean onDisplay;

}
