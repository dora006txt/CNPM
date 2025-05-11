package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.BannerDTO;
import ptithcm.edu.pharmacy.dto.CreateBannerDTO;
import ptithcm.edu.pharmacy.dto.UpdateBannerDTO;

import java.util.List;

public interface BannerService {
    List<BannerDTO> getAllBanners();
    BannerDTO getBannerById(Integer id);
    BannerDTO createBanner(CreateBannerDTO createBannerDTO);
    BannerDTO updateBanner(Integer id, UpdateBannerDTO updateBannerDTO);
    void deleteBanner(Integer id);
    List<BannerDTO> getActiveBanners();
}