import com.avaje.ebean.Ebean;
import models.Client;
import models.User;
import play.Logger;


public class InitialData {

    public static void load() {
        createAdmin();
        createClient();
    }


    private static void createAdmin() {
        if(Ebean.find(User.class).findRowCount() < 1) {
            Logger.info("Creating users...");
            User newUser = new User();
            newUser.email = "test@example.com";
            newUser.name = "Máté";
            newUser.password = User.hashPassword("test");
            newUser.role = User.UserRole.ADMIN;
            newUser.save();
        }
    }

    private static void createClient() {
        if(Ebean.find(Client.class).findRowCount() < 1) {
            Logger.info("Creating clients...");
            Client client = new Client();
            client.address = "Budapest Rakosi utca 12";
            client.name = "Ügyfél 1";
            client.email = "ugyfel1@example.com";
            client.phoneNumber = "21312321";
            client.taxNumber = "234e23432";
            client.save();

        }
    }

}
