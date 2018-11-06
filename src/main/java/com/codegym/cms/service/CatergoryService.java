package com.codegym.cms.service;

import com.codegym.cms.model.Catergory;

public interface CatergoryService {
    Iterable<Catergory> findAll();

    Catergory findById(Long id);

    void save(Catergory catergory);

    void remove(Long id);
}
