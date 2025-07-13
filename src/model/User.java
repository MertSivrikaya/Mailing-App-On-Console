package model;

public class User {
    private final String username;
    private final String name;
    private final String surname;
    private final String birthdate;
    private final String gender;
    private final String email;
    private final String location;
    private final boolean isAdmin;

    public User(String username, String name, String surname,
                String birthdate, String gender, String email,
                String location, boolean isAdmin) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
        this.location = location;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return "model.User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
