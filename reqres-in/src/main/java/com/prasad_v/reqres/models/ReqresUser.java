package com.prasad_v.reqres.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqresUser {
    private String id;
    private String name;
    private String job;
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String avatar;
    private String createdAt;
    private String updatedAt;

    public ReqresUser() {
    }

    public ReqresUser(String id, String name, String job, String email, String firstName, String lastName, String avatar, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.job = job;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String job;
        private String email;
        private String firstName;
        private String lastName;
        private String avatar;
        private String createdAt;
        private String updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder job(String job) { this.job = job; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder avatar(String avatar) { this.avatar = avatar; return this; }
        public Builder createdAt(String createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(String updatedAt) { this.updatedAt = updatedAt; return this; }

        public ReqresUser build() {
            return new ReqresUser(id, name, job, email, firstName, lastName, avatar, createdAt, updatedAt);
        }
    }
}
