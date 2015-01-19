package models.orders;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.EntityIdProvider;
import models.address.Address;
import play.libs.Json;
import utils.JsonUtils;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Item extends EntityIdProvider{
    public static enum ProductType  {
        FRAGILE("Törékeny"), FLEXIBLE("Rugalmas");

        private final String name;

        ProductType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static ArrayNode productTypesAsJson() {
            ArrayNode node = JsonUtils.newArrayNode();
            node.add(FRAGILE.toJson(1L));
            node.add(FLEXIBLE.toJson(2L));
            return node;
        }
        public ObjectNode toJson(Long id) {
            ObjectNode json = Json.newObject();
            json.put("name", name).put("id", id);
            return json;
        }
    }
    public String productName;

    @Column(scale = 4, precision = 15)
    public BigDecimal weight;

    @ManyToOne
    public Address fromAddress = new Address();

    @Enumerated(value = EnumType.STRING)
    public ProductType productType;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("id", id)
                .put("productName", productName)
                .put("weight", weight)
                .put("fromAddress", fromAddress.toJson());
        json.put("productType", productType.toJson(productType==ProductType.FRAGILE?1L:2L));

        return json;
    }

}
