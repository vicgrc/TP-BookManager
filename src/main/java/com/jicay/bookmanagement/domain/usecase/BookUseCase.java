package com.jicay.bookmanagement.domain.usecase;

import com.jicay.bookmanagement.domain.model.Book;

import java.util.List;

public class BookUseCase {
    public List<Book> getAllBooks() {
        return List.of(
                new Book("Les Misérables", "Victor Hugo"),
                new Book("Hamlet", "William Shakespeare1")
        );
    }
}
