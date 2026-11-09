package fr.insa.chatsystem;

public interface ContactListener {
    void onContactAdded(User user);
    void onContactRemoved(User user);
    void onUsernameChanged(User user, String oldUsername);
}