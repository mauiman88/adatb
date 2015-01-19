package models;

import play.data.validation.Constraints;

import javax.persistence.MappedSuperclass;

/**
 * Created by Mate on 2015.01.19..
 */
@MappedSuperclass
public class EntityNameProvider extends EntityIdProvider {

    @Constraints.Required
    public String name;

}
