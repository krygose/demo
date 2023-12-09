package com.example.demo.services;

import com.example.demo.model.Person;
import com.example.demo.model.PersonDto;

public class PersonService {

    public PersonDto<PersonDto> getUserCars(String userId) {
        List<PersonDto> carDtoList = carSqlService.getCars(userId).stream().map(CarService::carDtoMapper).toList();

        return new ContentDto<>(carDtoList);
    }

    private static PersonDto carDtoMapper(CarSqlRow carSqlRow) {
        return new PersonDto(
                String.valueOf(carSqlRow.id()),
                carSqlRow.make(),
                carSqlRow.model(),
                carSqlRow.productionYear(),
                carSqlRow.size(),
                carSqlRow.colour());
    }
}
