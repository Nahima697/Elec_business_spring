package com.elec_business.service;

import java.util.List;
import java.util.UUID;

public interface BaseService<T UUID> {

    /**
     * Creates a new entity
     * @param entity - Entity to be created
     */
    void create(T entity);

    /**
     * Returns a list of all existing entities
     * @return List of entities
     */
    List<T> readAll();

    /**
     * Returns a customer based on its UUID
     * @param id - Entity ID
     * @return - Entity object with the given ID
     */
    T read(UUID id);

    /**
     * Updates the customer with the given ID,
     * according to the passed customer
     * @param entity - Customer to use to update the data
     * @param id - ID of the customer you want to update
     * @return - true if the data has been updated, otherwise false
     */
    boolean update(T entity, UUID id);

    /**
     * Deletes the entity with the given ID
     * @param id - ID of the entity to be deleted
     * @return - true if the entity was deleted, otherwise false
     */
    boolean delete(UUID id);
}
