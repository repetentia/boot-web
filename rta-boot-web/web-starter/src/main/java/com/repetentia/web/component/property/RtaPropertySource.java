package com.repetentia.web.component.property;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="zt_property_source")
public class RtaPropertySource {
    @Id
    @Column(name="site")
    private String site;
    @Id
    @Column(name="profile")
    private String profile;    
    @Id
    @Column(name="property_key")
    private String propertyKey;
    @Column(name="se")
    private String se;
    @Column(name="property_name")
    private String propertyName;
    @Column(name="property_value")
    private String propertyValue;
}