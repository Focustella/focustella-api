package com.example.focustella.domain.model;

import lombok.Getter;

@Getter
public class ChecklistItem {
    private String itemUuid;
    private String title;
    private Boolean isCompleted;

    public ChecklistItem(String itemUuid, String title, Boolean isCompleted) {
        this.itemUuid = itemUuid;
        this.title = title;
        this.isCompleted = isCompleted;
    }
}