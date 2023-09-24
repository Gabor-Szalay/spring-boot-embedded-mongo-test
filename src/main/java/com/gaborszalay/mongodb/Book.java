package com.gaborszalay.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "books")
@Data
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class Book {

        @Id
        private String id;
        private String name;
        private BigDecimal price;
        private String author;
        private BigDecimal yearOfPublishing;
        private String genre;
        private BigDecimal reviewScore;
        private BigDecimal numberOfPages;
}
