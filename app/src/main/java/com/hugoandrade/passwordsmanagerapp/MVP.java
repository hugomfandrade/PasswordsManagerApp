package com.hugoandrade.passwordsmanagerapp;

import java.util.List;

public interface MVP {
    /* ****************************************************************************************** */
    /** For Main Activity {@link com.hugoandrade.passwordsmanagerapp} **/
    /* ****************************************************************************************** */
    interface RequiredMainViewOps extends ContextView {
        void populatePasswordEntries(List<PasswordEntry> passwordEntryList);
    }
    interface ProvidedMainPresenterOps extends PresenterOps<RequiredMainViewOps> {
    }
    interface RequiredMainPresenterOps extends ContextView {
        void onGetAllPasswordEntries(List<PasswordEntry> passwordEntryList);
    }
    interface ProvidedMainModelOps extends ModelOps<MVP.RequiredMainPresenterOps> {
        void getAllPasswordEntries();
    }

    /* ****************************************************************************************** */
    /** For Main Activity {@link com.hugoandrade.passwordsmanagerapp} **/
    /* ****************************************************************************************** */
    interface RequiredAddPasswordEntryViewOps extends ContextView {
        void enableInputFields(boolean areEnabled);
        void reportMessage(String message);
        void successfulAddPasswordEntry();
    }
    interface ProvidedAddPasswordEntryPresenterOps extends PresenterOps<RequiredAddPasswordEntryViewOps> {
        void addPasswordEntry(String accountName, String password);
    }
    interface RequiredAddPasswordEntryPresenterOps extends ContextView {
        void onInsertPasswordEntry(PasswordEntry passwordEntry);
    }
    interface ProvidedAddPasswordEntryModelOps extends ModelOps<MVP.RequiredAddPasswordEntryPresenterOps> {
        void addPasswordEntry(String accountName, String password);
    }
}
