package com.academia.domain.model.entities;
import com.academia.domain.model.valueobjects.ids.AccountId;
import lombok.Getter;
@Getter
public class Staff {
    private final AccountId accountId;
    private String title;
    public Staff(AccountId accountId, String title) {
        this.accountId = accountId;
        this.title = title;
    }
}