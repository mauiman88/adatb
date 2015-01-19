package models.address;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.EntityNameProvider;
import play.data.validation.Constraints;
import play.libs.Json;
import utils.JsonUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */

@Entity
public class City extends EntityNameProvider{
    @Constraints.Required
    public String zipCode;

    @Constraints.Required
    @ManyToOne
    public Country country;


    public String getDisplayName() {
        return zipCode + " " + name;
    }

    public static ArrayNode queryCitiesByText(String searchText, int pageSize ){
        List<City> resultList = Ebean.find(City.class).where().like("lower(name)", "%"+searchText.toLowerCase()+"%")
                .setMaxRows(pageSize)
                .findList();

        ArrayNode arrayNode = JsonUtils.newArrayNode();
        for (City city : resultList) {
            arrayNode.add(city.toJson());
        }
        return arrayNode;
    }

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json
                .put("id", id)
                .put("zipCode", zipCode)
                .put("name", name);
        return json;
    }

}
