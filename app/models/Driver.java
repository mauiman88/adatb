package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import utils.DateUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Driver extends EntityNameProvider {

    public String address;

    public String phoneNumber;

    public static enum LicenceType{
        B_TYPE("A category"), C_TYPE("C category"), E_TYPE("B category");

        private final String name;

        LicenceType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    @Enumerated(EnumType.STRING)
    public LicenceType licence = LicenceType.B_TYPE;

    @ElementCollection
    public String countries;

    @Column(scale = 4, precision = 15)
    public BigDecimal totalKilometers;

    public Date since;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();

        json.put("id", id)
                .put("name", name)
                .put("phoneNumber", phoneNumber)
                .put("totalKilometers", totalKilometers)
                .put("since", DateUtils.formatHUYear(since))
                .put("countries", countries);

        return json;
    }

    public static Finder<Long, Driver> find = new Finder(Long.class, Driver.class);

    public static List<Driver> all(){ return find.orderBy("name").findList(); }

    public static Integer count() {
        return Ebean.find(Driver.class).findRowCount();
    }
}
