package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import models.Client;
import models.Driver;
import models.Truck;
import models.User;
import models.orders.Order;
import models.orders.Item;
import models.orders.Transport;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;
import utils.JsonUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static play.data.Form.form;

/**
 * Created by Mate on 12/25/14.
 */
@Transactional
public class Orders extends Controller {
    public static final String ORDERS_MENU = "orders";

    public static Result showOrders() {
        return ok(views.html.orders.orders.render(ORDERS_MENU));
    }

    public static class OrderForm {

        public Long id;

        @Constraints.Required
        public String address;
        @Constraints.Required
        public String fromAddress;

        @Constraints.Required
        public Long clientId;

        @Constraints.Required
        public Long productTypeId;
        @Constraints.Required
        public String productName;
        @Constraints.Required
        public BigDecimal weight;
        public Boolean delivered;
        public String feedbackMsg;
        public List<ValidationError> validate() {
            List<ValidationError> errors = Lists.newArrayList();

            return errors.isEmpty()?null:errors;
        }

    }

    public static final Form<OrderForm> ORDER_FORM = form(OrderForm.class);

    public static Result saveOrder() {
        Form<OrderForm> orderForm = ORDER_FORM.bindFromRequest(request());
        if(orderForm.hasErrors()) {
            return badRequest(orderForm.errorsAsJson());
        }

        OrderForm form = orderForm.get();
        Order order;
        if(form.id != null) {
            order = Ebean.find(Order.class).where().eq("id", form.id).findUnique();
        } else {
            order = new Order();
            order.orderItem = new Item();
        }

        if(form.delivered != null) order.delivered = form.delivered;
        if(form.feedbackMsg != null) order.feedbackMsg = form.feedbackMsg;
        order.client = Ebean.find(Client.class).where().eq("id", form.clientId).findUnique();
        order.address = form.address;

        order.orderItem.fromAddress = form.fromAddress;
        order.orderItem.weight = form.weight;
        order.orderItem.productName = form.productName;
        order.orderItem.productType = form.productTypeId.equals(1L)? Item.ProductType.FRAGILE : Item.ProductType.FLEXIBLE;
        order.orderItem.save();
        order.save();

        return ok();
    }


    public static Result getOrders() {
        ObjectNode json = Json.newObject();
        User user = Application.getLocalUser();
        if(user.client == null) {

                json.put("new", JsonUtils.newArrayNode(Order.queryOrdersByState(Order.OrderState.NEW).stream().map(order -> order.toJson()).collect(Collectors.toList())));
            json.put("verified", JsonUtils.newArrayNode(Order.queryOrdersByState((Order.OrderState.VERIFIED)).stream().map(order -> order.toJson()).collect(Collectors.toList())));
            //json.put("wfs", JsonUtils.newArrayNode(Transport.getOrdersTransportsByState(Order.OrderState.WAITING_FOR_SHIPPING).stream().map(transport -> transport.toJson()).collect(Collectors.toList())));
            //json.put("shipped", JsonUtils.newArrayNode(Transport.getOrdersTransportsByState(Order.OrderState.SHIPPED).stream().map(transport -> transport.toJson()).collect(Collectors.toList())));
        } else {
            json.put("list", JsonUtils.newArrayNode(Order.queryClientOrders(user.client.id)
                    .stream().map(Order::toJson).collect(Collectors.toList())));
            json.put("client", user.client.toJson());
        }
        json.put("clients", JsonUtils.newArrayNode(Client.all().stream().map(client -> client.toJson()).collect(Collectors.toList())));
        json.put("productTypes", Item.ProductType.productTypesAsJson());
        return ok(json);
    }

    public static Result changeState() {
        DynamicForm form = DynamicForm.form().bindFromRequest(request());
        String stateStr = form.get("state");
        String orderIdStr = form.get("orderId");
        String description = form.get("description");

        if(!StringUtils.isEmpty(orderIdStr) && !StringUtils.isEmpty(stateStr)) {
            Order order = Ebean.find(Order.class).where().eq("id", Long.parseLong(orderIdStr)).findUnique();
            if (order == null) {
                return forbidden();
            }
            order.description = null;
            if(Order.OrderState.NEW.getName().equals(stateStr)) {
                order.orderState = Order.OrderState.NEW;
            } else if(Order.OrderState.VERIFIED.getName().equals(stateStr)) {
                order.orderState = Order.OrderState.VERIFIED;
            } else if(Order.OrderState.SHIPPED.getName().equals(stateStr)) {
                order.orderState = Order.OrderState.SHIPPED;
            }
            order.save();
            return ok();
        }
        return badRequest();
    }




    public static class WfsForm {

        public Long id;

        public Order order;
        public Driver driver;
        public Truck truck;

        @Constraints.Required
        public Long driverId;

        @Constraints.Required
        public Long truckId;

        public List<ValidationError> validate() {
            List<ValidationError> errors = Lists.newArrayList();

            order = Ebean.find(Order.class).where().eq("id", id).findUnique();
            truck = Ebean.find(Truck.class).where().eq("id", truckId).findUnique();
            driver = Ebean.find(Driver.class).where().eq("id", driverId).findUnique();
            if(truck == null) errors.add(new ValidationError("truckId", "error.required"));
            if(driver == null) errors.add(new ValidationError("driverId", "error.required"));
           /* Integer isAvailable = Transport.isAvailable(new DateTime(order.selectedDate).plusHours(6).toDate(), driver, truck, order.orderItem.weight);
            if(isAvailable != null && isAvailable == -1) {
                Transport dt = Transport.driversTransport(new DateTime(order.selectedDate).plusHours(6).toDate(), driver);
                Transport tt = Transport.trucksTransport(new DateTime(order.selectedDate).plusHours(6).toDate(), truck);
                if(tt == null) {
                    errors.add(new ValidationError("transport",
                            DateUtils.formatHUYear(order.selectedDate) + " időpontban, " + driver.name +" sofőr szállítást hajt végre " + dt.truck.lpNumber+ " gépjárművel!"));
                } else if(dt == null) {
                    errors.add(new ValidationError("transport",
                            DateUtils.formatHUYear(order.selectedDate) + " időpontban, " + truck.lpNumber + " gépjármű szállítást hajt végre " + (tt.driver.name + " sofőrrel!")));
                } else {
                    errors.add(new ValidationError("transport",
                            DateUtils.formatHUYear(order.selectedDate) + " időpontban, nincs lehetőség több megrendelés kiszállítására a kiválasztott sofőrrel és gépjárművel!"));
                }
            } else if(isAvailable != null && isAvailable == -2) {
                errors.add(new ValidationError("transport","Elérte a gépjárművel kg határát!"));
            }
*/
            return errors.isEmpty()?null:errors;
        }

    }
    public static final Form<WfsForm> WFS_FORM = form(WfsForm.class);

    public static Result wfsOrder() {
        Form<WfsForm> wfsForm = WFS_FORM.bindFromRequest(request());
        if(wfsForm.hasErrors()) {
            return badRequest(wfsForm.errorsAsJson());
        }

        WfsForm form = wfsForm.get();
        Transport transport;
      /*  if(Transport.isAvailable(new DateTime(form.order.selectedDate).plusHours(6).toDate(), form.driver, form.truck, form.order.orderItem.weight) == null) {
            transport = new Transport();
            transport.driver = form.driver;
            transport.truck = form.truck;
            transport.date = new DateTime(form.order.selectedDate).plusHours(6).toDate();
            transport.save();
        } else {
            transport = Ebean.find(Transport.class).where().eq("date", new DateTime(form.order.selectedDate).plusHours(6).toDate()).eq("driver", form.driver).eq("truck", form.truck).findUnique();
        }
        TransportItem.createAndSave(form.order, transport, transport.transportItems.size()+1);
*/
        form.order.orderState = Order.OrderState.WAITING_FOR_SHIPPING;
        form.order.save();

        return ok();
    }

    public static Result shippedOut(){
        DynamicForm form = DynamicForm.form().bindFromRequest(request());
        String id = form.get("transportId");
        if(StringUtils.isEmpty(id)) return forbidden();
        Transport transport = Ebean.find(Transport.class).where().eq("id", Long.parseLong(id)).findUnique();
       /* for (TransportItem ti : transport.transportItems) {
            ti.order.orderState = Order.OrderState.SHIPPED;
            ti.order.save();
        }*/
        return ok();
    }
}
