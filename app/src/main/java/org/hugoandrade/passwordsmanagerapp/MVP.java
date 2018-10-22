package org.hugoandrade.passwordsmanagerapp;

import org.hugoandrade.passwordsmanagerapp.common.ContextView;
import org.hugoandrade.passwordsmanagerapp.common.ModelOps;
import org.hugoandrade.passwordsmanagerapp.common.PresenterOps;
import org.hugoandrade.passwordsmanagerapp.passwordmanager.PasswordEntry;

import java.util.List;

public interface MVP {

    /* ****************************************************************************************** */
    /** For Main Activity {@link org.hugoandrade.passwordsmanagerapp} **/
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
    interface ProvidedMainModelOps extends ModelOps<RequiredMainPresenterOps> {
        void getAllPasswordEntries();
        void deletePasswordEntryList(List<PasswordEntry> passwordEntryList);
        void updatePasswordEntryItem(PasswordEntry passwordEntry);
    }

    /* ****************************************************************************************** */
    /** For Main Activity {@link org.hugoandrade.passwordsmanagerapp} **/
    /* ****************************************************************************************** */
    interface RequiredAddPasswordEntryViewOps extends ContextView {
        void enableInputFields(boolean areEnabled);
        void reportMessage(String message);
        void successfulAddPasswordEntry(PasswordEntry passwordEntry);
    }
    interface ProvidedAddPasswordEntryPresenterOps extends PresenterOps<RequiredAddPasswordEntryViewOps> {
        void addPasswordEntry(String entryName, String accountName, String password);
        void editPasswordEntry(PasswordEntry passwordEntry, String entryName, String accountName, String password);
    }
    interface RequiredAddPasswordEntryPresenterOps extends ContextView {
        void onInsertPasswordEntry(PasswordEntry passwordEntry);
        void onUpdatePasswordEntry(PasswordEntry passwordEntry);
    }
    interface ProvidedAddPasswordEntryModelOps extends ModelOps<MVP.RequiredAddPasswordEntryPresenterOps> {
        void addPasswordEntry(String entryName, String accountName, String password);
        void editPasswordEntry(PasswordEntry passwordEntry);
    }
}
