package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.Entity;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Client extends EntityNameProvider {
    public String address;


    @Constraints.Email
    public String email;

    public String phoneNumber;

    public String taxNumber;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("id", id)
                .put("name", name)
                .put("email", email)
                .put("phoneNumber", phoneNumber)
                .put("taxNumber", taxNumber)
                .put("address", address);
        return json;
    }

    public static Finder<Long, Client> find = new Finder(Long.class, Client.class);

    public static List<Client> all(){ return find.orderBy("name").findList(); }

    public static Integer count() {
        return Ebean.find(Client.class).findRowCount();
    }
}
