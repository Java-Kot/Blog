package com.codegym.cms.repository;

import com.codegym.cms.model.Catergory;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CatergoryRepository extends PagingAndSortingRepository<Catergory, Long> {
}
