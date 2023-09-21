package com.jicay.bookmanagement.domain.usecase;

import com.jicay.bookmanagement.domain.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookUseCaseTest {
    @InjectMocks
    private BookUseCase bookUseCase;

    @Test
    public void getAllBooks_shouldReturnAllBooks() {
        List<Book> res = bookUseCase.getAllBooks();

        assertThat(res).contains(
                new Book("Les Misérables", "Victor Hugo"),
                new Book("Hamlet", "William Shakespeare")
        );
    }
}