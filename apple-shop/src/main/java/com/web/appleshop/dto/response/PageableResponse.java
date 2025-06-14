package com.web.appleshop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageableResponse {
    int currentPage;
    int pageSize;
    int totalPage;
    long totalElements;
}
