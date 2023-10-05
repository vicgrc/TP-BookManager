package com.jicay.bookmanagement.infrastructure.application

import com.jicay.bookmanagement.domain.usecase.BookUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {
    @Bean
    fun bookUseCase(): BookUseCase {
        return BookUseCase()
    }
}