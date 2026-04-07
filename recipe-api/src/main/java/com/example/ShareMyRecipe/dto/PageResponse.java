package com.example.ShareMyRecipe.dto;

import java.util.List;

public class PageResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponse(List<T> data, int page, int size, long totalElements, int totalPages) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
