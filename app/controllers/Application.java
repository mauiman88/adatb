package controllers;

import play.*;
import play.mvc.*;
import com.avaje.ebean.Ebean;
import java.sql.*;
import views.html.*;

public class Application extends Controller {
    public static final String INDEX_MENU = "index";

    public static Result index() {
        return ok(index.render(INDEX_MENU));
    }

}
