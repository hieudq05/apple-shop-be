package com.web.appleshop.controller;

import com.web.appleshop.entity.Blog;
import com.web.appleshop.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<Blog> create(@RequestBody Blog blog) {
        return ResponseEntity.ok(blogService.create(blog));
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAll() {
        return ResponseEntity.ok(blogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getById(@PathVariable Integer id) {
        return blogService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Blog> update(@PathVariable Integer id, @RequestBody Blog blog) {
        return ResponseEntity.ok(blogService.update(id, blog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
