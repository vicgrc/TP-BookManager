package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BookUseCaseTest {

    @InjectMocks
    private lateinit var bookUseCase: BookUseCase

    @Test
    fun `get all books should returns all books sorted by name`() {
        val res = bookUseCase.getAllBooks()
        assertThat(res).contains(
            Book("Hamlet", "William Shakespeare"),
            Book("Les Misérables", "Victor Hugo")
        )
    }
}