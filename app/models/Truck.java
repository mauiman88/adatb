package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import utils.DateUtils;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Truck extends EntityIdProvider {
    public String lpNumber;

    public static enum TruckType{
        SMALL("Small"), MEDIUM("Medium"), BIG("Big");

        private final String name;

        TruckType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    @Enumerated(EnumType.STRING)
    public TruckType truckType= TruckType.MEDIUM;

    public Long kgLimit; //kg limit

    public Date moTTestDate; //műszaki

    public Boolean available = true;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();

        json.put("id", id)
                .put("lpNumber", lpNumber)
                .put("moTTestDate", DateUtils.formatHUTime(moTTestDate))
                .put("kgLimit", kgLimit)
                //.put("truckType", truckType.getName())
                .put("available", available);

        return json;
    }

    public static Finder<Long, Truck> find = new Finder(Long.class, Truck.class);

    public static List<Truck> all(){ return find.orderBy("lpNumber").findList(); }

    public static Integer count() {
        return Ebean.find(Truck.class).where().eq("available", true).findRowCount();
    }
}
