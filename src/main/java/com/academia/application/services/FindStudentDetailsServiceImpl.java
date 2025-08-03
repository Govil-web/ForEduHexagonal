package com.academia.application.services;

import com.academia.application.mappers.StudentDTOMapper;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Student;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.ports.in.dtos.StudentDetailsDTO;
import com.academia.domain.ports.in.queries.GetStudentDetailsQuery;
import com.academia.domain.ports.in.student.FindStudentDetailsQuery;
import com.academia.domain.ports.out.StudentRepository;
import com.academia.domain.ports.out.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindStudentDetailsServiceImpl implements FindStudentDetailsQuery {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final StudentDTOMapper studentDTOMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDetailsDTO> findStudentDetails(GetStudentDetailsQuery query) {
        AccountId accountId = new AccountId(query.studentAccountId());

        // Usamos las firmas corregidas de los repositorios
        Optional<UserAccount> userAccountOpt = userAccountRepository.findById(accountId);
        Optional<Student> studentOpt = studentRepository.findByAccountId(accountId);

        if (userAccountOpt.isPresent() && studentOpt.isPresent()) {
            StudentDetailsDTO dto = studentDTOMapper.toDTO(userAccountOpt.get(), studentOpt.get());
            return Optional.of(dto);
        }

        return Optional.empty();
    }
}