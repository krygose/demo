package com.example.demo.services;

import com.example.demo.model.Person;
import com.example.demo.model.PersonDto;
import com.fasterxml.jackson.core.sym.Name;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@EnableNeo4jRepositories(considerNestedRepositories = true)
public class PersonService {

    @Autowired
    Driver driver;
    @Autowired
    PersonRepository personRepository;

    public PersonDto getRoot() {
        return PersonService.toPersonDto(personRepository.findElder());
    }

    public void deleteAll() throws Exception{
        try(var sesion = driver.session()){
            sesion.run("MATCH (n:Person)\n" +
                    "      detach delete n");
        }
    }

    public PersonDto deleteNode(int id) throws Exception{
        if(id  > 0) {
            try (var sesion = driver.session()) {
                sesion.run("MATCH (n:Person{id:'" + id + "'})\n" +
                        "      detach delete n");
            }
        }
        return PersonService.toPersonDto(personRepository.findElder());
    }

    public int countSiblings(int id) throws Exception{
        if(id  > 0) {
            try (var sesion = driver.session()) {
                var result = sesion.run("match (Person {id: '" + id + "'})<-[:PARENT]-()-[:PARENT]->(child)\n" +
                        "return  count(DISTINCT child.name)");

                var test = result.stream().findFirst().get();
                if(test != null) {
                    return test.values().get(0).asInt();
                }
                else return 0;
            }
        }
        return 0;
    }

    public int countCousins(int id) throws Exception{
        if(id  > 0) {
            try (var sesion = driver.session()) {
                var result = sesion.run("match (Person {id: '" + id + "'})<-[:PARENT]-()<-[:PARENT]-(grand)-[:PARENT]->(parents)-[:PARENT]->(child)\n" +
                        "return Count(distinct child.name)");
                var test = result.stream().findFirst().get();
                if(test != null) {
                    return test.values().get(0).asInt();
                }
                else return 0;
            }
        }
        return 0;
    }

    public PersonDto addNode(String name) throws Exception{
        if(name != null) {
            String trimName =name;
            var splitName2 = name.split(" ");
            if (Arrays.stream(splitName2).count() > 1) {
                trimName = splitName2[0] + splitName2[1];
            }
            try (var sesion = driver.session()) {
                sesion.run("MATCH (c:Count)\n" +
                        "            SET c.count = c.count + 1\n" +
                        "            WITH c.count AS newId\n" +
                        "CREATE (" + trimName + ":Person {id: toString(newId),name: \"" + name + "\"})");
            }
        }
        return PersonService.toPersonDto(personRepository.findElder());
    }


    public PersonDto addNodeWithRelationParent(String name, Integer personId) throws Exception{
        if(name != null && personId  > 0) {
            try (var sesion = driver.session()) {
                String personName = personRepository.findById(personId.toString()).orElse(null).getName().toLowerCase().trim();
                var splitName = personName.split(" ");
                if (Arrays.stream(splitName).count() > 1) {
                    personName = splitName[0] + splitName[1];
                }
                String trimName =name;
                var splitName2 = name.split(" ");
                if (Arrays.stream(splitName2).count() > 1) {
                    trimName = splitName2[0] + splitName2[1];
                }
                sesion.run("MATCH (c:Count)\n" +
                        "SET c.count = c.count + 1\n" +
                        "WITH c.count AS newId\n" +
                        "CREATE (" + trimName + ":Person {id: toString(newId),name: \"" + name + "\"}\n)" +
                        "WITH * \n" +
                        "MATCH (c:Count)\n" +
                        "WITH c.count AS newId\n" +
                        "match (" + personName + ":Person{id:'" + personId + "'}),(" + trimName + ":Person {id: toString(newId)})\n" +
                        "create (" + trimName + ")<-[:PARENT]-(" + personName.trim() + ")\n" +
                        "create (" + trimName + ")-[:CHILD]->(" + personName.trim() + ")");
            }
        }
        return PersonService.toPersonDto(personRepository.findElder());
    }



    private static PersonDto toPersonDto(Person person) {
        if(person != null) {
            return new PersonDto(
                    person.getId(),
                    person.getName(),
                    person.getChildren().stream()
                            .map(PersonService::toPersonDto).toList());
        }
        else{
            Object List =new ArrayList<>();
            return new PersonDto(
                    0,
                    "noname",
                    (java.util.List<PersonDto>) List);
        }
    }

    interface PersonRepository extends Neo4jRepository<Person, String> {
        public default Person findElder(){
            List<Person> tmp = new ArrayList<>();
            Person max = null;
            List<Person> allPeopleInDb = this.findAll();

            for(Person k : allPeopleInDb){
                if(k.getParents().size() == 0){
                    tmp.add(k);
                }
            }
            int i = 0;

            for(var k : tmp){
                int t = searchDeep(k);
                if(t > i){
                    i = t;
                    max = k;
                }

            }
            return max;
        }

        private static int searchDeep(Person obj) {
            if (obj == null) {
                return 0;
            }

            if (obj.getChildren() == null || obj.getChildren().isEmpty()) {
                return 1; // Obiekt nie ma zagnieżdżonych obiektów
            }

            int maxChildDepth = 0;

            for (Person nestedObj : obj.getChildren()) {
                int childDepth = searchDeep(nestedObj);
                maxChildDepth = Math.max(maxChildDepth, childDepth);
            }

            return 1 + maxChildDepth;
        }

        private static void printAll(Person p, int depht) {
            for(var i : p.getChildren()){
                printIndentation(depht);
                System.out.println(i.getName());
                if(i.getChildren().size() != 0) {
                    printAll(i,depht+1);
                }
            }
        }

        private static void printIndentation(int depth) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
        }

    }
}
