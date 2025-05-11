package ptithcm.edu.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerDTO {
    private Integer id;
    private String name;
    private String imageUrl;
    private String targetUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}