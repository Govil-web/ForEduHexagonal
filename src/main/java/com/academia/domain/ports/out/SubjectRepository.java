package com.academia.domain.ports.out;

import com.academia.domain.model.entities.Subject;

public interface SubjectRepository {

    Subject save(Subject subject);

    Subject findById(Long id);

}
