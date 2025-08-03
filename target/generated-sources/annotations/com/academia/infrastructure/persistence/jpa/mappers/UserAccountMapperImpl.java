package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import com.academia.infrastructure.persistence.jpa.entities.UserJpaEntity;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-03T17:31:09-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class UserAccountMapperImpl implements UserAccountMapper {

    @Override
    public User toDomain(UserJpaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AccountId id = null;
        OrganizationId organizationId = null;
        Name name = null;
        Email email = null;
        LocalDate birthDate = null;
        String passwordHash = null;

        id = userJpaEntityToAccountId( entity );
        organizationId = userJpaEntityToOrganizationId( entity );
        name = userJpaEntityToName( entity );
        email = userJpaEntityToEmail( entity );
        birthDate = entity.getBirthDate();
        passwordHash = entity.getPasswordHash();

        AccountStatus status = null;

        User user = new User( id, organizationId, name, email, birthDate, passwordHash, status );

        return user;
    }

    @Override
    public UserJpaEntity toJpa(UserAccount aggregate) {
        if ( aggregate == null ) {
            return null;
        }

        UserJpaEntity userJpaEntity = new UserJpaEntity();

        userJpaEntity.setId( aggregateUserIdValue( aggregate ) );
        userJpaEntity.setOrganizationId( aggregateUserOrganizationIdValue( aggregate ) );
        userJpaEntity.setFirstName( aggregateUserNameFirstName( aggregate ) );
        userJpaEntity.setLastName( aggregateUserNameLastName( aggregate ) );
        userJpaEntity.setEmail( aggregateUserEmailValue( aggregate ) );

        return userJpaEntity;
    }

    protected AccountId userJpaEntityToAccountId(UserJpaEntity userJpaEntity) {
        if ( userJpaEntity == null ) {
            return null;
        }

        Long value = null;

        value = userJpaEntity.getId();

        AccountId accountId = new AccountId( value );

        return accountId;
    }

    protected OrganizationId userJpaEntityToOrganizationId(UserJpaEntity userJpaEntity) {
        if ( userJpaEntity == null ) {
            return null;
        }

        Long value = null;

        value = userJpaEntity.getOrganizationId();

        OrganizationId organizationId = new OrganizationId( value );

        return organizationId;
    }

    protected Name userJpaEntityToName(UserJpaEntity userJpaEntity) {
        if ( userJpaEntity == null ) {
            return null;
        }

        String firstName = null;
        String lastName = null;

        firstName = userJpaEntity.getFirstName();
        lastName = userJpaEntity.getLastName();

        Name name = new Name( firstName, lastName );

        return name;
    }

    protected Email userJpaEntityToEmail(UserJpaEntity userJpaEntity) {
        if ( userJpaEntity == null ) {
            return null;
        }

        String value = null;

        value = userJpaEntity.getEmail();

        Email email = new Email( value );

        return email;
    }

    private Long aggregateUserIdValue(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        AccountId id = user.getId();
        if ( id == null ) {
            return null;
        }
        Long value = id.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private Long aggregateUserOrganizationIdValue(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        OrganizationId organizationId = user.getOrganizationId();
        if ( organizationId == null ) {
            return null;
        }
        Long value = organizationId.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private String aggregateUserNameFirstName(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        Name name = user.getName();
        if ( name == null ) {
            return null;
        }
        String firstName = name.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

    private String aggregateUserNameLastName(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        Name name = user.getName();
        if ( name == null ) {
            return null;
        }
        String lastName = name.getLastName();
        if ( lastName == null ) {
            return null;
        }
        return lastName;
    }

    private String aggregateUserEmailValue(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        Email email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        String value = email.value();
        if ( value == null ) {
            return null;
        }
        return value;
    }
}
