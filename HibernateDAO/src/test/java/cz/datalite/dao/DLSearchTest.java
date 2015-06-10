package cz.datalite.dao;

import org.hibernate.sql.JoinType;
import org.junit.Test;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bubnik on 1.10.13.
 */
public class DLSearchTest {

    public static class Entity {
        @Embedded EmbeddedEntity embeddedEntity;
        @Embedded EmbeddedEntity getEmbeddedEntityMethod() { return null; };
        @ManyToOne ManyToOneEntity manyToOneEntity;
        String property;
    }

    public static class ManyToOneEntity {
        @ManyToOne ManyToOneEntity recursiveManyToOneEntity;
        @Embedded EmbeddedEntity embeddedEntity;
        @Embedded EmbeddedEntity getEmbeddedEntityMethod() { return null; };
        String property;
    };

    @Embeddable
    public static class EmbeddedEntity {
        @ManyToOne ManyToOneEntity recursiveManyToOneEntity;
        String property;
    };

    @Test
    public void testAddAliasWithoutEntityClass() throws Exception {
        // search object without entity class information - no way hot to check property values
        DLSearch dlSearch = new DLSearch();

        // basic checks (how alias is created)
        assertEquals("Basic alias", "propAlias", dlSearch.addAlias("prop"));
        assertEquals("Basic alias path", "prop", dlSearch.getAlias("propAlias").getPath());
        assertEquals("Basic alias join", JoinType.INNER_JOIN, dlSearch.getAlias("propAlias").getJoinType());
        assertEquals("Basic alias full path", "prop", dlSearch.getAlias("propAlias").getFullPath());

        // basic checks for created inner alias
        assertEquals("Inner alias", "innerAlias", dlSearch.addAlias("propAlias.inner"));
        assertEquals("Inner alias path", "propAlias.inner", dlSearch.getAlias("innerAlias").getPath());
        assertEquals("Inner alias join", JoinType.INNER_JOIN, dlSearch.getAlias("innerAlias").getJoinType());
        assertEquals("Inner alias full path", "prop.inner", dlSearch.getAlias("innerAlias").getFullPath());

        // basic checks for created inner alias added with full path
        assertEquals("Inner alias", "inner2Alias", dlSearch.addAliases("prop.inner2"));
        assertEquals("Inner alias path", "propAlias.inner2", dlSearch.getAlias("inner2Alias").getPath());
        assertEquals("Inner alias join", JoinType.INNER_JOIN, dlSearch.getAlias("inner2Alias").getJoinType());
        assertEquals("Inner alias full path", "prop.inner2", dlSearch.getAlias("inner2Alias").getFullPath());

        // if duplicate name should be created, whole path is used
        assertEquals("Duplicate property name", "#prop#inner#propAlias", dlSearch.addAliases("prop.inner.prop"));
        // first inner property, than root (special case of duplicate name)
        assertEquals("Duplicate property name setup", "propDupAlias", dlSearch.addAlias("prop.inner.propDup"));
        assertEquals("Duplicate property name check", "#propDupAlias", dlSearch.addAlias("propDup"));

        String longEmbedded = "prop.inner.prop.my.long.property.a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.last";
        assertEquals("Long embedded", "lastAlias", dlSearch.addAliases(longEmbedded));
        assertEquals("Alias found", "qAlias.last", dlSearch.getAliasForFullPath(longEmbedded));
        assertEquals("Alias found", "myAlias.long", dlSearch.getAliasForFullPath("prop.inner.prop.my.long"));

        assertEquals("Long add Alias in the middle", "anotherPropertyAlias", dlSearch.addAlias("longAlias.anotherProperty"));

    }

    @Test
    public void testAddAliasWithEntityClass() throws Exception {
        // search object with entity class information - check actual property with class definition
        DLSearch<Entity> dlSearch = new DLSearch<>(Collections.<DLSort>emptyList(), 10, 0, Entity.class);

        // basic checks (how alias is created)
        assertEquals("Basic alias", "manyToOneEntityAlias", dlSearch.addAlias("manyToOneEntity"));
        assertEquals("Basic alias path", "manyToOneEntity", dlSearch.getAlias("manyToOneEntityAlias").getPath());
        assertEquals("Basic alias join", JoinType.INNER_JOIN, dlSearch.getAlias("manyToOneEntityAlias").getJoinType());
        assertEquals("Basic alias full path", "manyToOneEntity", dlSearch.getAlias("manyToOneEntityAlias").getFullPath());
        assertEquals("Basic alias class", ManyToOneEntity.class, dlSearch.getAlias("manyToOneEntityAlias").getPersistentClass());

        // basic checks for created inner alias
        assertEquals("Inner alias", "propertyAlias", dlSearch.addAlias("manyToOneEntityAlias.property"));
        assertEquals("Inner alias path", "manyToOneEntityAlias.property", dlSearch.getAlias("propertyAlias").getPath());
        assertEquals("Inner alias join", JoinType.INNER_JOIN, dlSearch.getAlias("propertyAlias").getJoinType());
        assertEquals("Inner alias full path", "manyToOneEntity.property", dlSearch.getAlias("propertyAlias").getFullPath());
        assertEquals("Inner alias class", String.class, dlSearch.getAlias("propertyAlias").getPersistentClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAliasWithEntityClassUnknownProperty() throws Exception {
        // search object with entity class information - check actual property with class definition
        DLSearch<Entity> dlSearch = new DLSearch<>(Collections.<DLSort>emptyList(), 10, 0, Entity.class);
        assertEquals("unknown property should fail", "unknownAlias", dlSearch.addAlias("unknown"));
    }

    @Test
    public void isEmbeddableField() throws Exception {
        DLSearch dlSearch = new DLSearch<>(Collections.<DLSort>emptyList(), 10, 1, Entity.class);

        assertTrue("@Embedded on field resolved", dlSearch.isEmbeddableField(Entity.class, "embeddedEntity"));
        assertTrue("@Embedded on method resolved", dlSearch.isEmbeddableField(Entity.class, "embeddedEntityMethod"));
        assertFalse("@Embedded on field missing", dlSearch.isEmbeddableField(Entity.class, "manyToOneEntity"));

        assertTrue("@Embedded on inner field resolved", dlSearch.isEmbeddableField(ManyToOneEntity.class, "embeddedEntity"));
        assertTrue("@Embedded on inner method resolved", dlSearch.isEmbeddableField(ManyToOneEntity.class, "embeddedEntityMethod"));
        assertFalse("@Embedded on inner field missing", dlSearch.isEmbeddableField(ManyToOneEntity.class, "property"));
    }

    @Test
    public void testAddAliasForEmbbedded() throws Exception {
        // search object with entity class information - check actual property with class definition
        DLSearch<Entity> dlSearch = new DLSearch<>(Collections.<DLSort>emptyList(), 10, 0, Entity.class);

        assertEquals("Embedded alias", "recursiveManyToOneEntityAlias", dlSearch.addAliases("embeddedEntity.recursiveManyToOneEntity"));
        assertNull("Alias for embedded entity not created", dlSearch.getAliasForPath("embeddedEntity"));
        assertEquals("Alias for embedded entity not created", "embeddedEntity", dlSearch.getAliasForFullPath("embeddedEntity"));
        assertEquals("Embedded recursive alias", "#embeddedEntity#recursiveManyToOneEntity#embeddedEntity#recursiveManyToOneEntityAlias",
                dlSearch.addAliases("embeddedEntity.recursiveManyToOneEntity.embeddedEntity.recursiveManyToOneEntity"));
        assertNull("Alias for recursive embedded entity not created", dlSearch.getAliasForPath("manyToOneEntityAlias.embeddedEntity"));
        assertEquals("Alias for recursive embedded entity not created", "recursiveManyToOneEntityAlias.embeddedEntity",
                dlSearch.getAliasForFullPath("embeddedEntity.recursiveManyToOneEntity.embeddedEntity"));

    }

    @Test
    public void testAddAliasesForProperty() {
        // search object with entity class information - check actual property with class definition
        DLSearch<Entity> dlSearch = new DLSearch<>();

        dlSearch.addAliasesForProperty("inner.property", JoinType.INNER_JOIN);
        assertNotNull("Alias created", dlSearch.getAlias("innerAlias"));
        assertNull("Alias for property not created", dlSearch.getAlias("propertyAlias"));
    }

}
