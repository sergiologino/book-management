package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Category;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Cacheable(value = "books", key = "#title + '-' + #author")
    public Book findBookByTitleAndAuthor(String title, String author) {
        return bookRepository.findByTitleAndAuthor(title, author);
    }

    @Cacheable(value = "booksByCategory", key = "#categoryName")
    public List<Book> findBooksByCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        return category != null ? bookRepository.findByCategory(category) : List.of();
    }

    @Caching(evict = {
            @CacheEvict(value = "books", allEntries = true),
            @CacheEvict(value = "booksByCategory", allEntries = true)
    })
    @Transactional
    public Book createBook(String title, String author, String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            category = new Category(categoryName);
            category = categoryRepository.save(category);
        }

        Book book = new Book(title, author, category);
        return bookRepository.save(book);
    }

    @Caching(evict = {
            @CacheEvict(value = "books", key = "#bookId"),
            @CacheEvict(value = "booksByCategory", allEntries = true)
    })
    @Transactional
    public Book updateBook(Long bookId, String title, String author, String categoryName) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + bookId + " not found"));

        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            category = new Category(categoryName);
            category = categoryRepository.save(category);
        }

        existingBook.setTitle(title);
        existingBook.setAuthor(author);
        existingBook.setCategory(category);

        return bookRepository.save(existingBook);
    }

    @Caching(evict = {
            @CacheEvict(value = "books", key = "#bookId"),
            @CacheEvict(value = "booksByCategory", allEntries = true)
    })
    @Transactional
    public void deleteBook(Long bookId) {
        if (bookRepository.existsById(bookId)) {
            bookRepository.deleteById(bookId);
        } else {
            throw new IllegalArgumentException("Book with ID " + bookId + " not found");
        }
    }
}
