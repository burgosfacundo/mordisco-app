package utn.back.mordiscoapi.service;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;

import java.util.List;


/**
 * Interfaz genérica para servicios CRUD.
 */
public interface CrudService <T,R,ID>{
    void save(T t) throws BadRequestException;
    List<R> findAll();
    R findById(ID id) throws NotFoundException;
    void update(ID id, T t) throws NotFoundException,BadRequestException;
    void delete(ID id) throws NotFoundException;
}
