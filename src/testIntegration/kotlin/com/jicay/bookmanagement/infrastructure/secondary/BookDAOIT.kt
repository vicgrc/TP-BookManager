package com.jicay.bookmanagement.infrastructure.secondary

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.infrastructure.secondary.adapter.BookDAO
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.ResultSet


//@ExtendWith(TestContainersDBExtension::class)
@SpringBootTest
@Testcontainers
@ActiveProfiles("testIntegration")
class BookDAOIT {

    @Autowired
    private lateinit var bookDAO: BookDAO

    @Test
    fun `get all books from db`() {
        // GIVEN
        performQuery(
            // language=sql
            """
               insert into book (title, author)
               values 
                   ('Hamlet', 'Shakespeare'),
                   ('Les fleurs du mal', 'Beaudelaire'),
                   ('Harry Potter', 'Rowling');
            """.trimIndent())

        // WHEN
        val res = bookDAO.getAllBooks()

        // THEN
        assertThat(res).containsExactlyInAnyOrder(
            Book("Hamlet", "Shakespeare"),
            Book("Les fleurs du mal", "Beaudelaire"),
            Book("Harry Potter", "Rowling")
        )
    }

    protected fun performQuery(sql: String): ResultSet? {
        val hikariConfig = HikariConfig()
        hikariConfig.setJdbcUrl(postgresqlContainer.jdbcUrl)
        hikariConfig.username = postgresqlContainer.username
        hikariConfig.password = postgresqlContainer.password
        hikariConfig.setDriverClassName(postgresqlContainer.driverClassName)
        val ds = HikariDataSource(hikariConfig)

        val statement = ds.getConnection().createStatement()
        statement.execute(sql)
        val resultSet = statement.resultSet
        resultSet?.next()
        return resultSet
    }

    companion object {
        @Container
        private val postgresqlContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:13-alpine")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            postgresqlContainer.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            postgresqlContainer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresqlContainer::getUsername)
            registry.add("spring.datasource.password", postgresqlContainer::getPassword)
        }
    }
}