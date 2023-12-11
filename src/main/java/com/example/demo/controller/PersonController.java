package com.example.demo.controller;

import com.example.demo.model.PersonDto;
import com.example.demo.model.ResponseDto;
import com.example.demo.services.PersonService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @GetMapping("/root")
    public PersonDto getRoot() {
        return personService.getRoot();
    }

    @PostMapping("/addNode")
    public PersonDto addNode() throws Exception {
        return personService.addNode("czlek");
    }

    @PostMapping("/addNodeWithParent")
    public PersonDto addNodeWithParent() throws Exception {
        return personService.addNodeWithRelationParent("czlek6",22);
    }

    @PostMapping("/countCousins")
    public Integer countCousin() throws Exception {
        return personService.countCousins(8);
    }

    @PostMapping("/countSiblings")
    public Integer countSiblings() throws Exception {
        return personService.countSiblings(8);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<ResponseDto> deleteAll() throws Exception {
        personService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteNode")
    public PersonDto deleteNode() throws Exception {
        var person = personService.deleteNode(21);
            if(person == null){
                deleteAll();
            }
        return person;
    }
}
