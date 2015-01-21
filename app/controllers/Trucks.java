package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Truck;
import org.apache.commons.lang3.StringUtils;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;
import utils.JsonUtils;

import java.util.List;
import java.util.stream.Collectors;

import static play.data.Form.form;


@Transactional
public class Trucks extends Controller {

    public static final String TRUCKS_MENU = "trucks";

    public static class TruckForm {

        public Long id;

        @Constraints.Required
        public String lpNumber;

        @Constraints.Required
        public Long kgLimit;

        @Constraints.Required
        public String moTTestDate;

        @Constraints.Required
        public Boolean available;

    }
    public static final Form<TruckForm> TRUCK_FORM = form(TruckForm.class);

    public static Result saveTruck() {
        Form<TruckForm> truckForm = TRUCK_FORM.bindFromRequest(request());
        if(truckForm.hasErrors()) {
            return badRequest(truckForm.errorsAsJson());
        }

        TruckForm form = truckForm.get();
        Truck truck;
        if(form.id == null) {
            truck = new Truck();
        } else {
            truck = Ebean.find(Truck.class).where().eq("id", form.id).findUnique();
            if(truck == null) return forbidden();
        }
        truck.lpNumber = form.lpNumber;
        truck.kgLimit = form.kgLimit;
        truck.moTTestDate = DateUtils.formatToDateFromYear(form.moTTestDate);
        truck.available = form.available;
        //truck.truckType = form.truckType;
        truck.save();

        return ok();
    }

    public static Result getTrucks(){
        ObjectNode result = Json.newObject();
        result.put("list", JsonUtils.newArrayNode(Truck.all().stream().map(truck -> truck.toJson()).collect(Collectors.toList())));
        return ok(result);
    }

    public static Result deleteTruck() {
        DynamicForm dynamicForm = DynamicForm.form().bindFromRequest(request());
        if(StringUtils.isEmpty(dynamicForm.get("id"))) return forbidden();
        Long id = Long.parseLong(dynamicForm.get("id"));
        Truck truck = Ebean.find(Truck.class).where().eq("id", id).findUnique();

        truck.delete();
        return ok();
    }

    public static Result showTrucks() {
        return ok(views.html.trucks.trucks.render(TRUCKS_MENU));
    }

}


