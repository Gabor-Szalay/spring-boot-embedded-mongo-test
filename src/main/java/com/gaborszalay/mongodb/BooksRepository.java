package com.gaborszalay.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BooksRepository extends MongoRepository<Book, String> {
	List<Book> findAll();
}
