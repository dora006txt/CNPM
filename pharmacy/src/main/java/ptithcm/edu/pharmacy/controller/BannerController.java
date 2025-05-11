package ptithcm.edu.pharmacy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.BannerDTO;
import ptithcm.edu.pharmacy.dto.CreateBannerDTO;
import ptithcm.edu.pharmacy.dto.UpdateBannerDTO;
import ptithcm.edu.pharmacy.service.BannerService;

import java.util.List;

@RestController
@RequestMapping("/api") // Base path for banner related APIs
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    // --- Admin Endpoints (Consider adding security e.g.
    // @PreAuthorize("hasRole('ADMIN')")) ---
    @PostMapping("/admin/banners")
    public ResponseEntity<BannerDTO> createBanner(@RequestBody CreateBannerDTO createBannerDTO) {
        BannerDTO createdBanner = bannerService.createBanner(createBannerDTO);
        return new ResponseEntity<>(createdBanner, HttpStatus.CREATED);
    }

    @GetMapping("/admin/banners")
    public ResponseEntity<List<BannerDTO>> getAllBanners() {
        List<BannerDTO> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/admin/banners/{id}")
    public ResponseEntity<BannerDTO> getBannerById(@PathVariable Integer id) {
        BannerDTO banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(banner);
    }

    @PutMapping("/admin/banners/{id}")
    public ResponseEntity<BannerDTO> updateBanner(@PathVariable Integer id,
            @RequestBody UpdateBannerDTO updateBannerDTO) {
        BannerDTO updatedBanner = bannerService.updateBanner(id, updateBannerDTO);
        return ResponseEntity.ok(updatedBanner);
    }

    @DeleteMapping("/admin/banners/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Integer id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    // --- Public Endpoint ---
    @GetMapping("/banners/active")
    public ResponseEntity<List<BannerDTO>> getActiveBanners() {
        List<BannerDTO> activeBanners = bannerService.getActiveBanners();
        return ResponseEntity.ok(activeBanners);
    }
}