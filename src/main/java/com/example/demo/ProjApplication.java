package com.example.demo;

import com.example.demo.model.Person;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableNeo4jRepositories(considerNestedRepositories = true)
public class ProjApplication implements CommandLineRunner{
	@Autowired
	Driver driver;
	@Autowired
	PersonRepository personRepository;

	public static void main(String[] args) {
		SpringApplication.run(ProjApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Person p = personRepository.findElder();
		System.out.println(p.getName());
		printAll(p,1);
	}

	private static void printAll(Person p, int depht) {
		for(var i : p.getChilds()){
			printIndentation(depht);
			System.out.println(i.getName());
			if(i.getChilds().size() != 0) {
				printAll(i,depht+1);
			}
		}
	}

	private static void printIndentation(int depth) {
		for (int i = 0; i < depth; i++) {
			System.out.print("  ");
		}
	}

	interface PersonRepository extends Neo4jRepository<Person,String>{
		public default Person findElder(){
			List<Person> tmp = new ArrayList<>();
			Person max = null;
			for(Person k : this.findAll()){
				if(k.getParents().size() == 0){
					tmp.add(k);
				}
			}
			int i = 0;

			for(var k : tmp){
				int t = searchDeep(k);
				if(t>i){
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

			if (obj.getChilds() == null || obj.getChilds().isEmpty()) {
				return 1; // Obiekt nie ma zagnieżdżonych obiektów
			}

			int maxChildDepth = 0;

			for (Person nestedObj : obj.getChilds()) {
				int childDepth = searchDeep(nestedObj);
				maxChildDepth = Math.max(maxChildDepth, childDepth);
			}

			return 1 + maxChildDepth;
		}

	}


}


