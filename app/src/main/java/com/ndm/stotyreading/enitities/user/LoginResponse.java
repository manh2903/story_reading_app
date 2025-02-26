package com.ndm.stotyreading.enitities.user;

public class LoginResponse {
    private String message;
    private String token;
    private User user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {
        private String id;
        private String UserName;
        private String Password;
        private String FullName;
        private int Age;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String password) {
            Password = password;
        }

        public String getFullName() {
            return FullName;
        }

        public void setFullName(String fullName) {
            FullName = fullName;
        }

        public int getAge() {
            return Age;
        }

        public void setAge(int age) {
            Age = age;
        }
    }
}
