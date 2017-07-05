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
        void getAllPasswordEntries(String userID);
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
        void addPasswordEntry(String userID, String accountName, String password);
    }

    /* ****************************************************************************************** */
    /** For Main Activity {@link com.hugoandrade.passwordsmanagerapp} **/
    /* ****************************************************************************************** */
    interface RequiredLoginViewOps extends ContextView {
        void enableInputFields(boolean areEnabled);
        void successfulLogin();
        void reportMessage(String message);
    }
    interface ProvidedLoginPresenterOps extends PresenterOps<RequiredLoginViewOps> {
        void login(String username, String password);
    }
    interface RequiredLoginPresenterOps extends ContextView {
        void onGetAccount(Account account);
    }
    interface ProvidedLoginModelOps extends ModelOps<MVP.RequiredLoginPresenterOps> {
        void login(String username, String password);
    }

    /* ****************************************************************************************** */
    /** For Main Activity {@link com.hugoandrade.passwordsmanagerapp} **/
    /* ****************************************************************************************** */
    interface RequiredSignUpViewOps extends ContextView {
        void enableInputFields(boolean areEnabled);
        void successfulSignUp(Account account);
        void reportMessage(String message);
    }
    interface ProvidedSignUpPresenterOps extends PresenterOps<RequiredSignUpViewOps> {
        void signUp(String username, String password);
    }
    interface RequiredSignUpPresenterOps extends ContextView {
        void onInsertAccount(Account account);
    }
    interface ProvidedSignUpModelOps extends ModelOps<MVP.RequiredSignUpPresenterOps> {
        void signUp(String username, String password);
    }

}
