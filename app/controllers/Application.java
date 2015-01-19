package controllers;

import play.*;
import play.mvc.*;
import com.avaje.ebean.Ebean;
import java.sql.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

}
