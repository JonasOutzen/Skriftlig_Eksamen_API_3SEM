# The Dolphin JPA Exercise

## Gode ting at huske på ifm JPA og Lombok

1. Opret resource mappe i main, og lav filen config.properties

```plaintext
DB_NAME=dolphin
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

2. Opret en database i postgres

3. Opret en EntetiManagerFactory (EMF) i Main og kør for at lave tabellerne

4. ```Java
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();```

## Lombok
```plaintext
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@EqualsAndHashCode
@ToString.Exclude
@EqualsAndHashCode.Exclude
```

## JPA annotationer
```plaintext
@Entity
@Id
@OneToOne
@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
@ManyToOne
@GeneratedValue(strategy = GenerationType.IDENTITY)
@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
@MapsId
@Column(nullable = false)
@PrePersist
```