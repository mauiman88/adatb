package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.collect.Lists;
import models.Driver;
import org.apache.commons.lang3.StringUtils;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtils;
import utils.JsonUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static play.data.Form.form;

/**
 * Created by ujvaricsaba on 12/24/14.
 */
@Transactional
public class Drivers extends Controller {
    public static final String DRIVERS_MENU = "drivers";

    public static class DriverForm {

        public Long id;

        @Constraints.Required
        public String name;

        @Constraints.Required
        public String phoneNumber;

        @Constraints.Required
        public BigDecimal totalKilometers;

        public String since;
        public String languages;
        public String countries;

        public List<ValidationError> validate() {
            List<ValidationError> errors = Lists.newArrayList();

            if(StringUtils.isEmpty(since)) {
                errors.add(new ValidationError("since", "error.required"));
            }

            return errors.isEmpty()?null:errors;
        }

    }

    public static final Form<DriverForm> DRIVER_FORM = form(DriverForm.class);


    public static Result saveDriver() {
        Form<DriverForm> driverForm = DRIVER_FORM.bindFromRequest(request());
        if(driverForm.hasErrors()) {
            return badRequest(driverForm.errorsAsJson());
        }

        DriverForm form = driverForm.get();

        Driver driver;
        if(form.id != null) {
            driver = Ebean.find(Driver.class).where().eq("id", form.id).findUnique();
        } else {
            driver = new Driver();
        }
        driver.name = form.name;
        driver.phoneNumber = form.phoneNumber;
        driver.totalKilometers = form.totalKilometers;
        if(StringUtils.isEmpty(form.since)) {
            driver.since = new Date();
        } else {
            driver.since = DateUtils.formatToDateFromYear(form.since);
        }
        //driver.languages = form.languages;
        driver.countries = form.countries;
        driver.save();

        return ok();
    }

    public static Result deleteDriver() {
        DynamicForm dynamicForm = DynamicForm.form().bindFromRequest(request());
        Long id = Long.parseLong(dynamicForm.get("id"));

        Driver driver = Ebean.find(Driver.class).where().eq("id", id).findUnique();
        if(driver != null) {
            driver.delete();
        } else {
            return forbidden();
        }

        return ok();
    }

    public static Result getDrivers() {
        return ok(JsonUtils.newArrayNode(Driver.all().stream().map(driver -> driver.toJson()).collect(Collectors.toList())));
    }

    public static Result showDrivers() {
        return ok(views.html.drivers.drivers.render(DRIVERS_MENU));
    }


}
