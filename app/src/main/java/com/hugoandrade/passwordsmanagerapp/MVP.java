package com.hugoandrade.passwordsmanagerapp;

import java.util.List;

public interface MVP {
    /* ****************************************************************************************** */
    /** For Main Activity {@link com.hugoandrade.passwordsmanagerapp} **/
    /* ****************************************************************************************** */
    interface RequiredMainViewOps extends ContextView {
        void populatePasswordEntries(List<PasswordEntry> passwordEntryList);
        void removePasswordEntryListFromListAdapter(List<PasswordEntry> passwordEntryList);
        void removePasswordEntryFromListAdapter(PasswordEntry passwordEntry);
    }
    interface ProvidedMainPresenterOps extends PresenterOps<RequiredMainViewOps> {
        void deletePasswordEntryList(List<PasswordEntry> passwordEntryList);
        void updatePasswordEntryItem(PasswordEntry passwordEntry);
    }
    interface RequiredMainPresenterOps extends ContextView {
        void onGetAllPasswordEntries(List<PasswordEntry> passwordEntryList);
        void onDeleteAllPasswordEntries(List<PasswordEntry> passwordEntryList);
        void onDeletePasswordEntry(PasswordEntry passwordEntry);
    }
    interface ProvidedMainModelOps extends ModelOps<MVP.RequiredMainPresenterOps> {
        void getAllPasswordEntries();
        void deletePasswordEntryList(List<PasswordEntry> passwordEntryList);
        void updatePasswordEntryItem(PasswordEntry passwordEntry);
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
        void editPasswordEntry(PasswordEntry passwordEntry, String accountName, String password);
    }
    interface RequiredAddPasswordEntryPresenterOps extends ContextView {
        void onInsertPasswordEntry(PasswordEntry passwordEntry);
        void onUpdatePasswordEntry(PasswordEntry passwordEntry);
    }
    interface ProvidedAddPasswordEntryModelOps extends ModelOps<MVP.RequiredAddPasswordEntryPresenterOps> {
        void addPasswordEntry(String accountName, String password);
        void editPasswordEntry(PasswordEntry passwordEntry);
    }
}
