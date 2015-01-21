package models.orders;


import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import models.Client;
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
    public static enum TransportState {
         WAITING_FOR_SHIPPING("Kiszállításra vár"), SHIPPED("Kiszállítva");

        private final String name;

        TransportState(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
    public Date date;

    @ManyToOne
    public Driver driver;

    @ManyToOne
    public Client client;

    @ManyToOne
    public Truck truck;

    @Column(scale = 4, precision = 15)
    public BigDecimal distance;

    @ManyToMany
    public List<Item> transportItems = Lists.newArrayList();

    @Enumerated(value = EnumType.STRING)
    public TransportState transportState = TransportState.WAITING_FOR_SHIPPING;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("id", id);
        json.put("driver", driver.toJson());
        json.put("truck", truck.toJson());
        json.put("client", client.toJson());
        json.put("date", DateUtils.formatHUTime(date));
        ArrayNode node = JsonUtils.newArrayNode();
        for (Item transportItem : transportItems) {
            node.add(transportItem.toJson());
        }
        json.put("transportItems", node);

        return json;
    }

    public static List<Transport> getOrdersTransportsByState(TransportState state) {
        List<SqlRow> rows = Ebean.createSqlQuery("select t.id as id from transport t " +
                " inner join transport_item ti  on t.id=ti.transport_id " +
                " inner join item i             on ti.item_id=i.id " +
                " where t.transport_state = :state" +
                " group by t.id").setParameter("state", state).findList();

        List<Transport> transports = Lists.newArrayList();
        for (SqlRow sqlRow : rows) {
            transports.add(Ebean.find(Transport.class).where().eq("id", sqlRow.get("id")).findUnique());
        }

        return transports;
    }

}
