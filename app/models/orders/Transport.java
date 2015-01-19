package models.orders;

import com.google.common.collect.Lists;
import models.Driver;
import models.EntityIdProvider;
import models.Truck;
import play.Play;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Mate on 2015.01.19..
 */
@Entity
public class Transport extends EntityIdProvider{
    public static int AMORTIZATION_PRICE = Play.application().configuration().getInt("amortization.price");
    public static int PETROL_PRICE = Play.application().configuration().getInt("transport.petrol");
    public static final String SITE_LOCATION = "Magyarország HU, 8200 Veszprém Egyetem Utca 12";
    public static final Integer MAX_TRANSPORTS_PER_DAY = 3;

    public Date date;

    @ManyToOne
    public Driver driver;

    @ManyToOne
    public Truck truck;

    @Column(scale = 4, precision = 15)
    public BigDecimal distance;

    @OneToMany(mappedBy = "transport")
    public List<Item> transportItems = Lists.newArrayList();

}
