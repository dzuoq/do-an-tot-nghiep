package com.example.computershop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "New")
public class New {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newId;

    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content; // Trường này có thể chứa HTML
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime publishDate;
    private Boolean isDeleted;
    private String image; // Trường để lưu trữ đường dẫn hoặc tên tệp hình ảnh

    // Constructors
    public New() {
    }

    public New(String title, String content, LocalDateTime publishDate, Boolean isDeleted, String image) {
        this.title = title;
        this.content = content;
        this.publishDate = publishDate;
        this.isDeleted = isDeleted;
        this.image = image;
    }

    // Getters and Setters
    public Long getNewId() {
        return newId;
    }

    public void setNewId(Long newsId) {
        this.newId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content; // HTML sẽ được lưu trữ ở đây
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
