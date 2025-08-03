package com.academia.domain.model.entities;
import com.academia.domain.model.valueobjects.ids.AccountId;
import lombok.Getter;
@Getter
public class Guardian {
    private final AccountId accountId;
    public Guardian(AccountId accountId) { this.accountId = accountId; }
}