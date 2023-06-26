package com.niyiment.proccessor.config;

import com.github.javafaker.Faker;
import com.niyiment.proccessor.domain.entity.Person;
import com.niyiment.proccessor.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeed implements CommandLineRunner {
    private final PersonRepository repository;

    @Override
    public void run(String... args) {
        List<Person> people = new ArrayList<>();
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {
            Person person = Person.builder()
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .phoneNumber(faker.phoneNumber().phoneNumber())
                    .build();
            people.add(person);
        }

        repository.saveAll(people);
    }
}
