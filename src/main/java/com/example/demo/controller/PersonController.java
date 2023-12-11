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

    @PostMapping("/addChild")
    public PersonDto addChild(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name) throws Exception {
        return personService.addNodeWithRelationParent(name, id);
    }

    @PostMapping("/countCousins")
    public Integer countCousin(@RequestParam("id") Integer id) throws Exception {
        return personService.countCousins(id);
    }

    @PostMapping("/countSiblings")
    public Integer countSiblings(@RequestParam("id") Integer id) throws Exception {
        return personService.countSiblings(id);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<ResponseDto> deleteAll() throws Exception {
        personService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteNode")
    public PersonDto deleteNode(@RequestParam("id") Integer id) throws Exception {
        var person = personService.deleteNode(id);
            if(person == null){
                deleteAll();
            }
        return person;
    }
}
