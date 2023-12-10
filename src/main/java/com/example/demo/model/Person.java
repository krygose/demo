package com.example.demo.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Person")
public class Person {
    @Id
    private final String id;
    private final String name;

    @Relationship(type = "PARENT",direction = Relationship.Direction.OUTGOING)
    private List<Person> children = new ArrayList<>();

    @Relationship(type = "CHILD",direction = Relationship.Direction.OUTGOING)
    private List<Person> parents = new ArrayList<>();

    public List<Person> getParents() {
        return parents;
    }

    public String getName() {
        return name;
    }
    public Integer getId() {
        return Integer.parseInt(this.id);
    }

    public List<Person> getChildren() {
        return children;
    }

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
