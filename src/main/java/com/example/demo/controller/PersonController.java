package com.example.demo.controller;

import com.example.demo.model.PersonDto;
import com.example.demo.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @GetMapping("/root")
    public PersonDto getRoot() {
        return personService.getRoot();
    }

    @GetMapping("/addNode")
    public PersonDto addNode() throws Exception {
        return personService.addNode("czlek");
    }

    @GetMapping("/addNodeWithParent")
    public PersonDto addNodeWithParent() throws Exception {
        return personService.addNodeWithRelationParent("czlek2",1);
    }
}
