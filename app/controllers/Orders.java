package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import models.Client;
import models.Driver;
import models.Truck;
import models.User;
import models.address.City;
import models.address.Country;
import models.orders.Item;
import models.orders.Order;
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
 * Created by Mate on 2015.01.21..
 */
@Transactional
public class Orders extends Controller {

    public static class OrderForm {

        public Long id;

        @Constraints.Required
        public Long cityId;
        @Constraints.Required
        public Long countryId;
        @Constraints.Required
        public String street;
        @Constraints.Required
        public String streetNumber;

        @Constraints.Required
        public Long clientId;

        @Constraints.Required
        public Long productTypeId;
        @Constraints.Required
        public String productName;
        @Constraints.Required
        public Long cityIdFrom;
        @Constraints.Required
        public Long countryIdFrom;
        @Constraints.Required
        public String streetFrom;
        @Constraints.Required
        public String streetNumberFrom;
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
        order.address.country = Ebean.find(Country.class).where().eq("id", form.countryId).findUnique();
        order.address.city = Ebean.find(City.class).where().eq("id", form.cityId).findUnique();
        order.address.street = form.street;
        order.address.streetNumber = form.streetNumber;
        order.address.save();
        order.client = Ebean.find(Client.class).where().eq("id", form.clientId).findUnique();

        order.orderItem.fromAddress.country = Ebean.find(Country.class).where().eq("id", form.countryIdFrom).findUnique();
        order.orderItem.fromAddress.city = Ebean.find(City.class).where().eq("id", form.cityIdFrom).findUnique();
        order.orderItem.fromAddress.street = form.streetFrom;
        order.orderItem.fromAddress.streetNumber = form.streetNumberFrom;
        order.orderItem.fromAddress.save();
        order.orderItem.weight = form.weight;
        order.orderItem.productName = form.productName;
        order.orderItem.productType = form.productTypeId.equals(1L)? Item.ProductType.FRAGILE : Item.ProductType.FLEXIBLE;
        order.orderItem.save();
        order.save();

        return ok();
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


    public static Result getOrders() {
        ObjectNode json = Json.newObject();
       // User user = Application.getLocalUser();
       // if(user.client == null) {
            Transport.getOrdersTransportsByState(Order.OrderState.WAITING_FOR_SHIPPING);
            json.put("verified", JsonUtils.newArrayNode(Order.queryOrdersByState((Order.OrderState.VERIFIED)).stream().map(order -> order.toJson()).collect(Collectors.toList())));
            json.put("wfs", JsonUtils.newArrayNode(Transport.getOrdersTransportsByState(Order.OrderState.WAITING_FOR_SHIPPING).stream().map(transport -> transport.toJson()).collect(Collectors.toList())));
            json.put("shipped", JsonUtils.newArrayNode(Transport.getOrdersTransportsByState(Order.OrderState.SHIPPED).stream().map(transport -> transport.toJson()).collect(Collectors.toList())));
       // } else {
        //    json.put("list", JsonUtils.newArrayNode(Order.queryClientOrders(user.client.id)
            //        .stream().map(Order::toJson).collect(Collectors.toList())));
          //  json.put("client", user.client.toJson());
        //}
        json.put("clients", JsonUtils.newArrayNode(Client.all().stream().map(client -> client.toJson()).collect(Collectors.toList())));
        json.put("productTypes", Item.ProductType.productTypesAsJson());
        return ok(json);
    }


    public static Result sendOutOrder() {
        DynamicForm form = DynamicForm.form().bindFromRequest(request());
        String dateStr = form.get("selectedDate");
        String orderIdStr = form.get("orderId");
        String amountStr = form.get("amount");

        boolean hasError = false;
        if(StringUtils.isEmpty(dateStr) || new Date().compareTo(new DateTime(dateStr).toDate()) == 1) {
            form.reject(new ValidationError("selectedDate", "error.required"));
            hasError = true;
        }
        if(StringUtils.isEmpty(amountStr)) {
            form.reject(new ValidationError("amount", "error.required"));
            hasError = true;
        }
        if(hasError) {
            return badRequest(form.errorsAsJson());
        }

        if(!StringUtils.isEmpty(orderIdStr)) {
            Order order = Ebean.find(Order.class).where().eq("id", Long.parseLong(orderIdStr)).findUnique();
            if (order == null) {
                return forbidden();
            }

            order.orderState = Order.OrderState.WAITING_FOR_SHIPPING;
            order.selectedDate = DateUtils.formatToDateFromYear(dateStr);
            order.amount = new BigDecimal(amountStr);
            order.save();
            return ok();
        }
        return badRequest();
    }
}
