import com.avaje.ebean.Ebean;
import models.User;
import play.Logger;


public class InitialData {
    public static void load() {
        createAdmin();
    }


    private static void createAdmin() {
        if(Ebean.find(User.class).findRowCount() < 1) {
            Logger.info("Creating users...");
            User newUser = new User();
            newUser.email = "test@example.com";
            newUser.name = "Csabi";
            newUser.password = User.hashPassword("test");
            newUser.role = User.UserRole.ADMIN;
            newUser.save();
        }
    }
}
