import com.avaje.ebean.Ebean;
import models.Client;
import models.Driver;
import models.Truck;
import models.User;
import play.Logger;

import java.util.Date;


public class InitialData {

    public static void load() {
        createAdmin();
        createClient();
        createTruck();
        createDriver();
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

    private static void createDriver() {
        if(Ebean.find(Driver.class).findRowCount() < 1) {
            Logger.info("Creating drivers");

            Driver driver = new Driver();
            driver.name = "Driver 1";
            driver.address = "Address 1";
            driver.countries = "adsfasd fadfa";
            driver.licence = Driver.LicenceType.B_TYPE;
            driver.phoneNumber = "123123123";
            driver.save();
        }
    }
    private static void createTruck() {
        if(Ebean.find(Truck.class).findRowCount() < 1) {
            Logger.info("Creating trucks");

            Truck truck = new Truck();
            truck.lpNumber = "IIF-011";
            truck.available = true;
            truck.kgLimit = 500L;
            truck.moTTestDate = new Date();
            truck.save();
        }
    }

}
