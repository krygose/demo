package com.example.demo.services;

import com.example.demo.model.Person;
import com.example.demo.model.PersonDto;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void addNode(String name) throws Exception{
        try(var sesion = driver.session()){
            sesion.run("MATCH (c:Count)\n" +
                            "            SET c.count = c.count + 1\n" +
                            "            WITH c.count AS newId"+
                    "CREATE ("+name.toLowerCase().trim()+":Person {id: toString(newId),name: \""+ name+"\"})");
        }
    }
    public void addNodeWithRelationParent(String name, Integer personId) throws Exception{
        try(var sesion = driver.session()){
            sesion.run("MATCH (c:Count)\n" +
                    "            SET c.count = c.count + 1\n" +
                    "            WITH c.count AS newId"+
                    "CREATE ("+ name.toLowerCase().trim() +":Person {id: toString(newId),name: \""+ name +"\"})"
            );
        }
        try(var session = driver.session()) {
            session.run("match (a:Person{id:"+ personId +"}),("+name.toLowerCase().trim()+":Person),(c:Count)"+
                    "where"+name.toLowerCase().trim()+".id=c.count"+
                    "create ("+name.toLowerCase().trim()+")<-[:PARENT]-(a)"+
                    "create ("+name.toLowerCase().trim()+")-[:CHILD]->(a)");
        }
    }
//    public void addNodeWithRelationChild(String name, String familyName) throws Exception{
//        try(var sesion = driver.session()){
//            sesion.run("CREATE ("+ name.toLowerCase().trim() +":Person {name: \""+ name +"\"})" +
//                    " with "+name.toLowerCase().trim() +
//                    "match ("+familyName.toLowerCase().trim()+":Person)"+
//                    "where "+familyName.toLowerCase().trim()+" = \""+ familyName +"\""+
//                    "create ("+familyName.toLowerCase().trim()+")-[:PARENT]->("+name.toLowerCase().trim()+")"+
//                    "create ("+familyName.toLowerCase().trim()+")<-[:CHILD]-("+name.toLowerCase().trim()+")"
//
//            );
//        }
//    }


    private static PersonDto toPersonDto(Person person) {
        return new PersonDto(
                person.getId(),
                person.getName(),
                person.getChildren().stream()
                        .map(PersonService::toPersonDto).toList());
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
