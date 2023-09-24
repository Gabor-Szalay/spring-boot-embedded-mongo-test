package com.gaborszalay.mongodb;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("books")
@AllArgsConstructor
public class BooksController {

    private final BooksService booksService;

    @GetMapping(value = "/", produces = "application/json")
    public List<Book> getBooks() {
        return booksService.getBooks();
    }
}
