package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "Поиск книги по названию и автору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга найдена",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "404", description = "Книга не найдена",
                    content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Book> findBookByTitleAndAuthor(@RequestParam String title, @RequestParam String author) {
        Book book = bookService.findBookByTitleAndAuthor(title, author);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Найти книги в категории")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книги найдены",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "404", description = "Книг не обнаружено",
                    content = @Content)
    })
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Book>> findBooksByCategory(@PathVariable String categoryName) {
        List<Book> books = bookService.findBooksByCategory(categoryName);
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Добавить книгу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Книга добавлена",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)) })
    })
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestParam String title, @RequestParam String author, @RequestParam String category) {
        Book createdBook = bookService.createBook(title, author, category);
        return ResponseEntity.status(201).body(createdBook);
    }

    @Operation(summary = "Изменить имеющуюуся книгу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга обновлена",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "404", description = "Не удалось обновить книгу",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestParam String title, @RequestParam String author, @RequestParam String category) {
        try {
            Book updatedBook = bookService.updateBook(id, title, author, category);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить книгу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Книга удалена"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
