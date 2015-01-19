package models;

import play.db.ebean.Model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by Mate on 2015.01.19..
 */
@MappedSuperclass
public class EntityIdProvider extends Model {

    @Id
    @GeneratedValue
    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
