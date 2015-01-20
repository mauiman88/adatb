package models.orders;


import com.google.common.collect.Lists;
import models.Driver;
import models.EntityIdProvider;
import models.Truck;
import play.Play;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Transport extends EntityIdProvider {

    public Date date;

    @ManyToOne
    public Driver driver;

    @ManyToOne
    public Truck truck;

    @Column(scale = 4, precision = 15)
    public BigDecimal distance;

    @ManyToMany
    public List<Item> transportItems = Lists.newArrayList();

}
