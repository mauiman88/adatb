package models.address;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.EntityIdProvider;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Address extends EntityIdProvider {
    @ManyToOne
    public Country country;

    @ManyToOne
    public City city;

    public String street;

    public String streetNumber;

    public String generateTooltipString() {
        StringBuilder sb = new StringBuilder();
        if( country != null ) {
            sb.append(country.name);
            sb.append(" ");
        }
        if( city != null ) {
            sb.append(city.name);
            sb.append(", ");
        }
        sb.append(street);
        sb.append(" ");
        sb.append(streetNumber);
        return sb.toString();
    }

    public String getDisplayName() {
        return (city.zipCode + " " + city.name + ", " + street + " " + streetNumber +".");
    }

    public ObjectNode toJson(){
        ObjectNode json = Json.newObject();
        json.put("display", getDisplayName());
        json.put("city", city.toJson());
        json.put("country", country.toJson());
        json.put("street", street)
                .put("streetNumber", streetNumber);
        return json;
    }
}
