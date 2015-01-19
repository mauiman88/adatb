package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import play.data.validation.Constraints;
import play.libs.Crypto;
import play.libs.Json;
import utils.JsonUtils;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
@Table( name = "users")
public class User extends EntityNameProvider{

    public static enum UserRole {
        ADMIN("Admin"), CLIENT("Ügyfél");

        private final String name;

        UserRole(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Constraints.Email
    @Column(unique = true, columnDefinition = "varchar")
    public String email;

    public String password;

    @Enumerated(EnumType.STRING)
    public UserRole role;

    @OneToOne
    public Client client;

    public static User authenticate(String email, String password) {
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        if( user != null && hashPassword(password).equals(user.password)) {
            return user;
        }
        return null;
    }

    public static User findByEmail(String email) {
        return Ebean.find(User.class).where().eq("email", email).findUnique();
    }

    public static User findById(String id) {
        if(StringUtils.isEmpty(id)) return null;
        return Ebean.find(User.class).where().eq("id", Long.parseLong(id)).findUnique();
    }

    public static ObjectNode queryUsers() {
        List<User> users = new Finder(String.class, User.class).all();
        ArrayNode userArray = JsonUtils.newArrayNode();
        for (User user : users) {
            userArray.add(user.toJson());
        }

        ObjectNode result = Json.newObject();
        result.put("list", userArray);
        return result;
    }

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject()
                .put("id", id)
                .put("name", name)
                .put("email", email)
                .put("role", role==null?"undefined":role.getName());
        if(client != null) {
            json.put("client", client.toJson());
        }
        return json;
    }

    public boolean isAdmin() {
        return role.equals(UserRole.ADMIN);
    }

    public static String hashPassword(String password) {
        return Crypto.sign(password);
    }
}
