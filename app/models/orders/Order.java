package models.orders;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Client;
import models.EntityIdProvider;
import play.data.validation.Constraints;
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
@Table(name = "orders")
public class Order extends EntityIdProvider{

    public static enum OrderState {
        NEW("Új"),VERIFIED("Jóváhagyva"), DONE("Done");

        private final String name;

        OrderState(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public Date created = new Date();

    @Constraints.Required
    public String address;

    @ManyToOne
    @Constraints.Required
    public Client client;

    @ManyToOne
    public Item orderItem;

    @Enumerated(value = EnumType.STRING)
    public OrderState orderState = OrderState.NEW;

    public Date selectedDate;
    @Column(scale = 4, precision = 15)
    public BigDecimal amount;

    public String description;

    public String feedbackMsg; //after delivery done client can leave feedback

    public Boolean delivered = false;



    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("id", id)
                .put("created", DateUtils.formatHUTime(created))
                .put("selectedDate", DateUtils.formatHUYear(selectedDate))
                .put("description", description)
                .put("feedbackMsg", feedbackMsg)
                .put("deliveredValue", delivered)
                .put("address", address)
                .put("delivered", delivered?"Igen":"Még nem")
                .put("amount", amount);
        json.put("orderItem", orderItem.toJson());
        json.put("state", orderState.getName());
        json.put("client", client.toJson());
        return json;
    }


    public static List<Order> queryClientOrders(Long id) {
        return Ebean.find(Order.class).where().eq("client.id", id).findList();
    }

    public static List<Order> queryOrdersByState(OrderState ... state) {
        return Ebean.find(Order.class).where().in("orderState", state).findList();
    }
}
