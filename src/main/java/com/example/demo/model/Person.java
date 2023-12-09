package com.example.demo.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Person")
public class Person {
    @Id
    private final String name;

    @Relationship(type = "PARENT",direction = Relationship.Direction.OUTGOING)
    private List<Person> childs = new ArrayList<>();

    @Relationship(type = "CHILD",direction = Relationship.Direction.OUTGOING)
    private List<Person> parents = new ArrayList<>();

    public List<Person> getParents() {
        return parents;
    }

    public String getName() {
        return name;
    }

    public List<Person> getChilds() {
        return childs;
    }

    public Person(String name) {
        this.name = name;
    }
}
