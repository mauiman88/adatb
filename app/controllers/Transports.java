package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import models.orders.Order;
import models.orders.Transport;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.JsonUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static play.data.Form.form;

@Transactional
public class Transports extends Controller{

    public static Result getDirection() {
        ObjectNode json = Json.newObject();
        json.put("transportsForMap", JsonUtils.newArrayNode(Transport.getOrdersTransportsByState(Order.OrderState.WAITING_FOR_SHIPPING).stream().map(
                t -> t.toMapJson()
        ).collect(Collectors.toList())));
        json.put("transports", JsonUtils.newArrayNode(Transport.getOrdersTransportsByState(Order.OrderState.WAITING_FOR_SHIPPING).stream().map(
                t -> t.toJson()
        ).collect(Collectors.toList())));

        return ok(json);
    }

    public static class SortForm {
        public Long orderId;
        public int sortOrder;
        public BigDecimal distance;

        public List<ValidationError> validate() {
            List<ValidationError> errors = Lists.newArrayList();

            return errors.isEmpty()?null:errors;
        }

    }

}
