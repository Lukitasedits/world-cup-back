package com.lukitasedits.app.mundial.repositories;

public class EmailValuesDTO {

    private String mailFrom;
    private String mailTo;
    private String subject;
    private String username;
    private String tokenEmail;
    private String tokenPassword;

    public EmailValuesDTO(){

    }

    public EmailValuesDTO(String mailFrom, String mailTo, String subject, String username, String tokenEmail, String tokenPassword) {
        this.mailFrom = mailFrom;
        this.mailTo = mailTo;
        this.subject = subject;
        this.username = username;
        this.tokenEmail = tokenEmail;
        this.tokenPassword = tokenPassword;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenEmail() {
        return tokenEmail;
    }

    public void setTokenEmail(String tokenEmail) {
        this.tokenEmail = tokenEmail;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
    }
}
