package fr.insa.chatsystem.contacts;

public interface ContactListener {
    void onContactAdded(User user);
    void onContactRemoved(User user);
    void onUsernameChanged(User user, String oldUsername);
}