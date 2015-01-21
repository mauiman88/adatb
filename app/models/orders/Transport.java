package models.orders;


import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import models.Driver;
import models.EntityIdProvider;
import models.Truck;
import play.Play;
import play.libs.Json;
import utils.DateUtils;
import utils.JsonUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Transport extends EntityIdProvider {

    public Date date;

    @ManyToOne
    public Driver driver;

    @ManyToOne
    public Truck truck;

    @Column(scale = 4, precision = 15)
    public BigDecimal distance;

    @ManyToMany
    public List<Item> transportItems = Lists.newArrayList();

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("id", id);
        json.put("driver", driver.toJson());
        json.put("truck", truck.toJson());
        json.put("date", DateUtils.formatHUTime(date));
        ArrayNode node = JsonUtils.newArrayNode();
        json.put("transportItems", node);

        return json;
    }

    public ObjectNode toMapJson() {
        ObjectNode json = Json.newObject();
        json.put("id", id);
        json.put("displayName", DateUtils.formatHUTime(date) + " " + driver.name + " " + truck.lpNumber);
        return json;
    }

    public static List<Transport> getOrdersTransportsByState(Order.OrderState state) {
        List<SqlRow> resultList = Ebean.createSqlQuery("select t.id from transport t " +
                " inner join transport_item ti on t.id=ti.transport_id " +
                " inner join orders o on o.id=ti.order_id " +
                " where o.order_state = :state " +
                " group by t.id " +
                " order by t.date").setParameter("state", state).findList();

        List<Transport> transports = Lists.newArrayList();
        for (SqlRow sqlRow : resultList) {
            transports.add(Ebean.find(Transport.class).where().eq("id", sqlRow.get("id")).findUnique());
        }

        return transports;

    }

}
