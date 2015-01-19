package models.address;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.EntityNameProvider;
import play.cache.Cache;
import play.libs.Json;
import utils.JsonUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Country extends EntityNameProvider {
    public static final String CACHE_KEY_COUNTRY_BY_COUNTRY_CODE = "country_by_country_code_";

    @Column(unique = true)
    public String countryCode;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("id", id)
                .put("name", name)
                .put("countryCode", countryCode);
        return json;
    }


    public static Country getHungary() {
        return findByCountryCode("HU");
    }

    public static Country findByCountryCode(String countryCode) {
        Country result = (Country) Cache.get(CACHE_KEY_COUNTRY_BY_COUNTRY_CODE + countryCode);
        if( result == null ) {
            result = (Country) Ebean.find(Country.class).where().eq("countryCode", countryCode).findUnique();
            Cache.set(CACHE_KEY_COUNTRY_BY_COUNTRY_CODE + countryCode, result);
        }
        return result;
    }

    public void clearFromCache() {
        Cache.remove(CACHE_KEY_COUNTRY_BY_COUNTRY_CODE + countryCode);
    }

    public static ArrayNode queryCountriesByText(String searchText, int pageSize) {
        List<Country> resultList =  Ebean.find(Country.class).where().like("lower(name)", "%"+searchText.toLowerCase()+"%")
                .setMaxRows(pageSize)
                .findList();

        ArrayNode arrayNode = JsonUtils.newArrayNode();
        for (Country country : resultList) {
            arrayNode.add(country.toJson());
        }

        return arrayNode;
    }

}
