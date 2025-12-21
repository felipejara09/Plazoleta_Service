package com.pragma.powerup.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageModel<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;

    public PageModel(List<T> content,
                     int pageNumber,
                     int pageSize,
                     long totalElements,
                     int totalPages,
                     boolean first,
                     boolean last) {
        this.content = content == null ? Collections.emptyList() : content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public static <T> PageModel<T> empty(int pageNumber, int pageSize) {
        return new PageModel<>(Collections.emptyList(), pageNumber, pageSize, 0, 0, true, true);
    }

    public List<T> getContent() { return content; }
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isFirst() { return first; }
    public boolean isLast() { return last; }

    public boolean hasNext() { return !last; }
    public boolean hasPrevious() { return !first; }

    public <U> PageModel<U> map(Function<? super T, ? extends U> converter) {
        List<U> mapped = content.stream().map(converter).collect(Collectors.toList());
        return new PageModel<>(mapped, pageNumber, pageSize, totalElements, totalPages, first, last);
    }
}
