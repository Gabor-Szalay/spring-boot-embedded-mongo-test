package com.gaborszalay.mongodb;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class BooksService {
    private final BooksRepository booksRepository;

    public List<Book> getBooks() {
        return booksRepository.findAll();
    }
}
