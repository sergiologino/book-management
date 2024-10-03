package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByTitleAndAuthor(String title, String author);
    List<Book> findByCategory(Category category);
}
