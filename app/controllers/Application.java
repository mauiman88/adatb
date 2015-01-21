package controllers;

import com.google.common.collect.Lists;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.*;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.*;
import com.avaje.ebean.Ebean;
import java.sql.*;
import java.util.List;

import views.html.*;

import static play.data.Form.form;

public class Application extends Controller {
    public static final String SESSION_KEY = "user";
    public static final String INDEX_MENU = "index";

    public static Result index() {
        return ok(index.render(INDEX_MENU));
    }

    public static Result login(String redirect) {
        User user = Application.getLocalUser();
        if (user != null) {
            return redirect(controllers.routes.Application.index());
        }
        return ok(views.html.login.render(redirect));
    }

    public static Result logout() {
        session().clear();
        return redirect("/");
    }

    public static class LoginForm {

        @Constraints.Email
        @Constraints.Required
        public String email;
        @Constraints.Required
        @Constraints.MinLength(4)
        public String password;

        public String redirect;

        public User user;

        public List<ValidationError> validate() {
            List<ValidationError> errors = Lists.newArrayList();

            if(StringUtils.isEmpty(email)) {
                errors.add(new ValidationError("email", "error.required"));
            }

            if(StringUtils.isEmpty(password)) {
                errors.add(new ValidationError("password", "error.required"));
            }

            if(!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
                user = User.authenticate(email, password);
                if (user == null) {
                    errors.add(new ValidationError("auth", "Error during authentication."));
                    errors.add(new ValidationError("auth", "Wrong email or password."));
                }
            }

            return errors.isEmpty()?null:errors;
        }

    }
    public static final Form<LoginForm> LOGIN_FORM = form(LoginForm.class);

    public static Result authenticate() {
        Form<LoginForm> loginForm = LOGIN_FORM.bind(request().body().asJson());
        if(loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }
        LoginForm form = loginForm.get();

        if(form.user != null) {
            session().put("user", form.user.id.toString());
            if(StringUtils.isEmpty(form.redirect)) {
                form.redirect = "/";
            }
            return redirect(form.redirect);
        }
        return badRequest();
    }

    public static User getLocalUser() {
        if( session().containsKey(SESSION_KEY) ) {
            return User.findById(session().get(SESSION_KEY));
        }
        return null;
    }


}
